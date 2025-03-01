package com.unir.elastiserch.service;

import com.unir.elastiserch.controller.model.BookStockRequest;
import com.unir.elastiserch.data.model.Book;
import com.unir.elastiserch.controller.model.CreateBookRequest;
import com.unir.elastiserch.controller.model.BooksQueryResponse;

import java.util.List;

public interface BooksService {

	BooksQueryResponse getBooks(String name, String description, String country, Boolean aggregate);
	
	Book getBook(String booksId);
	
	Boolean removeBook(String booksId);
	
	Book createBook(CreateBookRequest request);

	Object checkAndUpdateStock(List<BookStockRequest> requestList);

}