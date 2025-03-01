package com.unir.elastiserch.data;

import java.util.List;
import java.util.Optional;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import com.unir.elastiserch.data.model.Book;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends ElasticsearchRepository<Book, String> {

	List<Book> findByTitle(String title);
	
	Optional<Book> findById(String id);
	
	Book save(Book product);
	
	void delete(Book product);
	
	List<Book> findAll();
}