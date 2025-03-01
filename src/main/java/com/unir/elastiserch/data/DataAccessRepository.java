package com.unir.elastiserch.data;

import java.util.*;

import com.unir.elastiserch.data.model.Book;
import com.unir.elastiserch.controller.model.AggregationDetails;
import com.unir.elastiserch.controller.model.BooksQueryResponse;
import lombok.SneakyThrows;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder.Type;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Repository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Repository
@RequiredArgsConstructor
@Slf4j
public class DataAccessRepository {

    @Value("${server.fullAddress}")
    private String serverFullAddress;

    // Esta clase (y bean) es la unica que usan directamente los servicios para
    // acceder a los datos.
    private final BookRepository bookRepository;
    private final ElasticsearchOperations elasticClient;

    private final String[] descriptionSearchFields = {"description", "description._2gram", "description._3gram"};

    public Book save(Book book) {
        return bookRepository.save(book);
    }

    public Boolean delete(Book book) {
        bookRepository.delete(book);
        return Boolean.TRUE;
    }

	public Optional<Book> findById(String id) {
		return bookRepository.findById(id);
	}

    @SneakyThrows
    public BooksQueryResponse findBooks(String title, String description, String category, Boolean aggregate) {
        BoolQueryBuilder querySpec = QueryBuilders.boolQuery();

        // Usamos should para permitir coincidencias con al menos uno de los filtros
        if (!StringUtils.isEmpty(category)) {
            querySpec.should(QueryBuilders.termQuery("category", category));
        }

        if (!StringUtils.isEmpty(title)) {
            querySpec.should(QueryBuilders.matchQuery("title", title));
        }

        if (!StringUtils.isEmpty(description)) {
            querySpec.should(QueryBuilders.multiMatchQuery(description, descriptionSearchFields).type(Type.BOOL_PREFIX));
        }

        // Si no se ha dado ningún parámetro, devolvemos todos los libros
        if (!querySpec.hasClauses()) {
            querySpec.must(QueryBuilders.matchAllQuery());
        }


        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder().withQuery(querySpec);

        if (aggregate) {
            nativeSearchQueryBuilder.addAggregation(AggregationBuilders.terms("Category Aggregation").field("category").size(1000));
            nativeSearchQueryBuilder.withMaxResults(0); // Solo agregaciones, no resultados de búsqueda
        }

        Query query = nativeSearchQueryBuilder.build();
        SearchHits<Book> result = elasticClient.search(query, Book.class);

        List<AggregationDetails> responseAggs = new LinkedList<>();

        if (result.hasAggregations()) {
            Map<String, Aggregation> aggs = result.getAggregations().asMap();
            ParsedStringTerms countryAgg = (ParsedStringTerms) aggs.get("Author Aggregation");

            String queryParams = getQueryParams(title, description, category);
            countryAgg.getBuckets()
                    .forEach(
                            bucket -> responseAggs.add(
                                    new AggregationDetails(
                                            bucket.getKey().toString(),
                                            (int) bucket.getDocCount(),
                                            serverFullAddress + "/books?category=" + bucket.getKey() + queryParams)));
        }
        return new BooksQueryResponse(result.getSearchHits().stream().map(SearchHit::getContent).toList(), responseAggs);
    }


    /**
     * Componemos una URI basada en serverFullAddress y query params para cada argumento, siempre que no viniesen vacios
     *
     * @param title        
     * @param description 
     * @param category
     * @return
     */
    private String getQueryParams(String title, String description, String category) {
        String queryParams = (StringUtils.isEmpty(title) ? "" : "&title=" + title)
                + (StringUtils.isEmpty(description) ? "" : "&description=" + description);
        // Eliminamos el ultimo & si existe
        return queryParams.endsWith("&") ? queryParams.substring(0, queryParams.length() - 1) : queryParams;
    }
}
