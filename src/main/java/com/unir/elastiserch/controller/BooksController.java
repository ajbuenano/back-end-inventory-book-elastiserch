package com.unir.elastiserch.controller;


import java.util.List;
import java.util.Map;

import com.unir.elastiserch.controller.model.BookStockRequest;
import com.unir.elastiserch.controller.model.BooksQueryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.unir.elastiserch.data.model.Book;
import com.unir.elastiserch.controller.model.CreateBookRequest;
import com.unir.elastiserch.service.BooksService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BooksController {

	private final BooksService service;

	@GetMapping("/books")
	public ResponseEntity<BooksQueryResponse> getBooks(
			@RequestHeader Map<String, String> headers,
			@RequestParam(required = false) String description, 
			@RequestParam(required = false) String title, 
			@RequestParam(required = false) String category,
			@RequestParam(required = false, defaultValue = "false") Boolean aggregate) {

		log.info("headers: {}", headers);
		BooksQueryResponse books = service.getBooks(title, description, category, aggregate);
		return ResponseEntity.ok(books);
	}

	@GetMapping("/books/{bookId}")
	public ResponseEntity<Book> getBook(@PathVariable String bookId) {

		log.info("Request received for book {}", bookId);
		Book book = service.getBook(bookId);

		if (book != null) {
			return ResponseEntity.ok(book);
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@DeleteMapping("/books/{bookId}")
	public ResponseEntity<Void> deleteBook(@PathVariable String bookId) {

		Boolean removed = service.removeBook(bookId);

		if (Boolean.TRUE.equals(removed)) {
			return ResponseEntity.ok().build();
		} else {
			return ResponseEntity.notFound().build();
		}

	}

	@PostMapping("/books")
	public ResponseEntity<Book> createBook(@RequestBody CreateBookRequest request) {

		Book createdBook = service.createBook(request);

		if (createdBook != null) {
			return ResponseEntity.status(HttpStatus.CREATED).body(createdBook);
		} else {
			return ResponseEntity.badRequest().build();
		}

	}


	@PostMapping("/books/check-stock")
	public ResponseEntity<?> checkAndUpdateStock(@RequestBody List<BookStockRequest> requestList) {
		Object result = service.checkAndUpdateStock(requestList);

		if (result instanceof String) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
		}

		return ResponseEntity.ok(result);
	}
}
