package com.unir.elastiserch.controller.model;

import com.unir.elastiserch.data.model.Book;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BooksQueryResponse {

    private List<Book> books;
    private List<AggregationDetails> aggs;

}
