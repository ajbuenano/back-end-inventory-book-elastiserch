package com.unir.elastiserch.service;

import com.unir.elastiserch.controller.model.BookStockRequest;
import com.unir.elastiserch.controller.model.BooksQueryResponse;
import com.unir.elastiserch.data.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.unir.elastiserch.data.DataAccessRepository;
import com.unir.elastiserch.data.model.Book;
import com.unir.elastiserch.controller.model.CreateBookRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BooksServiceImpl implements BooksService {

	private final DataAccessRepository repository;
	private final BookRepository bookRepository;

	@Override
	public BooksQueryResponse getBooks(String title, String description, String category, Boolean aggregate) {
		//Ahora por defecto solo devolvera productos visibles
		return repository.findBooks(title, description, category, aggregate);
	}

	@Override
	public Book getBook(String bookId) {
		return repository.findById(bookId).orElse(null);
	}

	@Override
	public Boolean removeBook(String bookId) {

		Book book = repository.findById(bookId).orElse(null);
		if (book != null) {
			repository.delete(book);
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	@Override
	public Book createBook(CreateBookRequest request) {

		if (request != null && StringUtils.hasLength(request.getTitle().trim())
				&& StringUtils.hasLength(request.getDescription().trim())
				&& StringUtils.hasLength(request.getAuthor().trim()) && request.getVisible() != null) {

			Book book = Book.builder()
					.title(request.getTitle())
					.image(request.getImage())
					.description(request.getDescription())
					.year(request.getYear())
					.isbn(request.getIsbn())
					.author(request.getAuthor())
					.category(request.getCategory())
					.price(request.getPrice())
					.score(request.getScore())
					.stock(request.getStock())
					.visible(request.getVisible())
					.build();

			return repository.save(book);
		} else {
			return null;
		}
	}

	@Override
	public Object checkAndUpdateStock(List<BookStockRequest> requestList) {
		List<BookStockRequest> booksToUpdate = new ArrayList<>();

		for (BookStockRequest request : requestList) {
			Optional<Book> optionalBook = bookRepository.findById(request.getBookId());
			Book book = optionalBook.orElse(null);

			if (book == null) {
				return "Libro con ID " + request.getBookId() + " no encontrado.";
			}

			if (book.getStock() < request.getQuantity()) {
				return "Stock insuficiente para el libro: " + book.getTitle();
			}

			booksToUpdate.add(request);
		}

		for (BookStockRequest request : requestList) {
			Optional<Book> optionalBook = bookRepository.findById(request.getBookId());
			Book book = optionalBook.orElse(null);
			if (book != null) {
				book.setStock(book.getStock() - request.getQuantity());
				bookRepository.save(book);
			}
		}

		return booksToUpdate;
	}
}
