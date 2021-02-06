package com.cursor.library.controllers;

import com.cursor.library.models.Book;
import com.cursor.library.models.CreateBookDto;
import com.cursor.library.services.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@RestController
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping(value = "/books", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Book>> getAll(
            @RequestParam(value = "sortBy", defaultValue = "byName", required = false) String sortBy,
            @RequestParam(value = "limit", defaultValue = "10", required = false) String limit,
            @RequestParam(value = "offset", defaultValue = "0", required = false) String offset,
            @RequestParam(value = "name", defaultValue = "", required = false) String name,
            @RequestParam(value = "description", defaultValue = "", required = false) String description,
            @RequestParam(value = "ratingMoreThan", defaultValue = "0", required = false) String ratingMoreThan
    ) {
        List<Book> books = bookService.getAll();
        return books == null || books.isEmpty()
                ? new ResponseEntity<>(HttpStatus.NO_CONTENT)
                : new ResponseEntity<>(books, HttpStatus.OK);
    }

    @PostMapping(
            value = "/books",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Book> createBook(@RequestBody CreateBookDto createBookDto) {
        Book newBook = bookService.createBook(createBookDto);
        return newBook == null || newBook.getBookId().isBlank() || newBook.getBookId().isEmpty()
                ? new ResponseEntity<>(HttpStatus.NOT_MODIFIED)
                : new ResponseEntity<>(newBook, HttpStatus.CREATED);
    }

    @GetMapping(value = "/books/{bookId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Book> getById(
            @PathVariable(value = "bookId") String bookId
    ) {
        Book book = bookService.getById(bookId);
        if (book == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @DeleteMapping(value = "/books/{bookId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Book> deleteById(
            @PathVariable(value = "bookId") String bookId
    ) {
        Book book = bookService.deleteBook(bookId);
        if (book == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(book, HttpStatus.OK);
    }
}
