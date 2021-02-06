package com.cursor.library.services;

import com.cursor.library.daos.BookDao;
import com.cursor.library.exceptions.BadIdException;
import com.cursor.library.exceptions.BookNameIsNullException;
import com.cursor.library.exceptions.BookNameIsTooLongException;
import com.cursor.library.models.Book;
import com.cursor.library.models.CreateBookDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {
    private static final String BOOK_NAME = "Awesome book";
    public static final String DESCRIPTION = "Really awesome book";
    public static final List<String> AUTHORS = Arrays.asList("Cool writer", "Another cool writer");
    public static final int YEAR_OF_PUBLICATION = 2021;
    public static final int NUMBER_OF_WORDS = 1_111_111;
    public static final int RATING = 10;
    public static final String NULL_ID = null;
    public static final String BLANK_ID = "       ";
    public static final String NULL_NAME = null;

    private static String bookId;
    private static CreateBookDto bookDto;

    @Mock
    private BookDao bookDao;

    @InjectMocks
    BookService bookService;

    @BeforeAll
    static void init() {
        bookId = UUID.randomUUID().toString();
        bookDto = new CreateBookDto();
        bookDto.setName(BOOK_NAME);
        bookDto.setDescription(DESCRIPTION);
        bookDto.setAuthors(AUTHORS);
        bookDto.setYearOfPublication(YEAR_OF_PUBLICATION);
        bookDto.setNumberOfWords(NUMBER_OF_WORDS);
        bookDto.setRating(RATING);
    }

    @Test
    void getByIdSuccessTest() {
//        GIVEN
        Mockito.when(bookDao.getById(bookId)).thenReturn(new Book(bookId));
//        WHEN
        Book bookFromDB = bookService.getById(bookId);
//        THEN
        assertEquals(bookId, bookFromDB.getBookId());
        Mockito.verify(bookDao).getById(bookId);
    }

    @Test
    void getByIdExpectBadIdExceptionDueNullIdTest() {
        assertThrows(BadIdException.class, () -> bookService.getById(NULL_ID));
        Mockito.verify(bookDao, Mockito.never()).getById(Mockito.any());
        Mockito.verifyNoMoreInteractions(bookDao);
    }

    @Test
    void getByIdExpectBadIdExceptionDueBlankIdTest() {
        assertThrows(BadIdException.class, () -> bookService.getById(BLANK_ID));
        Mockito.verify(bookDao, Mockito.never()).getById(Mockito.any());
        Mockito.verifyNoMoreInteractions(bookDao);
    }

    @Test
    void getValidatedBookNameExpectBookNameIsNullExceptionTest() {
        assertThrows(BookNameIsNullException.class, () -> bookService.getValidatedBookName(NULL_NAME));
    }

    @Test
    void getValidatedBookNameExpectBookNameIsTooLongExceptionTest() {
        assertThrows(BookNameIsTooLongException.class,
                () -> bookService.getValidatedBookName(BOOK_NAME.repeat(120)));
    }

    @Test
    void createBookSuccessTest() {
//        GIVEN
        Book newBook =
                new Book(bookId, BOOK_NAME, DESCRIPTION, AUTHORS, YEAR_OF_PUBLICATION, NUMBER_OF_WORDS, RATING);
        Mockito.when(bookDao.addBook(Mockito.any(Book.class)))
                .thenReturn(newBook);
//        WHEN
        Book bookFromDB = bookService.createBook(bookDto);
//        THEN
        assertEquals(newBook, bookFromDB);
        ArgumentCaptor<Book> bookArgumentCaptor = ArgumentCaptor.forClass(Book.class);
        Mockito.verify(bookDao).addBook(bookArgumentCaptor.capture());
        assertEquals(1, bookArgumentCaptor.getAllValues().size());
        Book capturedBook = bookArgumentCaptor.getValue();
        assertNotNull(capturedBook.getBookId());
        assertEquals(BOOK_NAME, capturedBook.getName());
        assertAll("capturedBook",
                () -> assertEquals(DESCRIPTION, capturedBook.getDescription()),
                () -> assertEquals(AUTHORS, capturedBook.getAuthors()),
                () -> assertEquals(YEAR_OF_PUBLICATION, capturedBook.getYearOfPublication()),
                () -> assertEquals(NUMBER_OF_WORDS, capturedBook.getNumberOfWords()),
                () -> assertEquals(RATING, capturedBook.getRating())
        );
        Mockito.verifyNoMoreInteractions(bookDao);
    }

    @Test
    void deleteBookSuccessTest() {
//        GIVEN
        Mockito.when(bookDao.deleteById(bookId)).thenReturn(new Book(bookId));
//        WHEN
        Book bookFromDB = bookService.deleteBook(bookId);
//        THEN
        assertEquals(bookId, bookFromDB.getBookId());
        Mockito.verify(bookDao).deleteById(bookId);
    }

    @Test
    void deleteBookExpectBadIdExceptionDueNullIdTest() {
        assertThrows(BadIdException.class, () -> bookService.deleteBook(NULL_ID));
        Mockito.verify(bookDao, Mockito.never()).deleteById(Mockito.any());
        Mockito.verifyNoMoreInteractions(bookDao);
    }

    @Test
    void deleteBookExpectBadIdExceptionDueBlankIdTest() {
        assertThrows(BadIdException.class, () -> bookService.deleteBook(BLANK_ID));
        Mockito.verify(bookDao, Mockito.never()).deleteById(Mockito.any());
        Mockito.verifyNoMoreInteractions(bookDao);
    }
}
