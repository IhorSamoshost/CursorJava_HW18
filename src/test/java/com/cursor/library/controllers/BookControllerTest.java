package com.cursor.library.controllers;

import com.cursor.library.models.Book;
import com.cursor.library.models.CreateBookDto;
import com.cursor.library.services.BookService;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class BookControllerTest extends BaseControllerTest {

    public static final String BOOK_NAME = "Cool new Book";
    public static final String BOOK_DESCRIPTION = "Cool description";
    public static final int NUMBER_OF_WORDS = 100500;
    public static final int BOOK_RATE = 10;
    public static final int YEAR_OF_PUBLICATION = 2020;
    public static final List<String> BOOK_AUTHORS = Arrays.asList("author1", "author2");
    public static final CreateBookDto createBookDto = new CreateBookDto();
    static {
        createBookDto.setName(BOOK_NAME);
        createBookDto.setDescription(BOOK_DESCRIPTION);
        createBookDto.setAuthors(BOOK_AUTHORS);
        createBookDto.setYearOfPublication(YEAR_OF_PUBLICATION);
        createBookDto.setNumberOfWords(NUMBER_OF_WORDS);
        createBookDto.setRating(BOOK_RATE);
    }

    @Autowired
    BookService bookService;

    @Test
    public void getAllSuccessTest() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/books")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();

        List<Book> books = OBJECT_MAPPER.readValue(
                result.getResponse()
                        .getContentAsString(),
                new TypeReference<>() {
                }
        );
        assertEquals(bookService.getAll(), books);
    }

    @Test
    public void createBookSuccessTest() throws Exception {
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post("/books")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(OBJECT_MAPPER.writeValueAsString(createBookDto));

        MvcResult result = mockMvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.authors").isNotEmpty())
                .andReturn();

        Book book = OBJECT_MAPPER.readValue(
                result.getResponse().getContentAsString(),
                Book.class
        );

        mockMvc.perform(MockMvcRequestBuilders.get("/books/" + book.getBookId()))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void getByIdSuccessTest() throws Exception {
        List<Book> books = bookService.getAll();
        int randomIndex = new Random().nextInt(books.size());
        Book book = books.get(randomIndex);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .get("/books/" + book.getBookId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        Book bookFromDB = OBJECT_MAPPER.readValue(
                result.getResponse().
                        getContentAsString(),
                Book.class
        );
        assertEquals(book, bookFromDB);
    }

    @Test
    public void getByIdExpectNotFoundStatusForMissingBookTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .get("/books/" + UUID.randomUUID().toString()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void deleteByIdSuccessTest() throws Exception {
        List<Book> books = bookService.getAll();
        int randomIndex = new Random().nextInt(books.size());
        Book book = books.get(randomIndex);
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders
                .delete("/books/" + book.getBookId()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        Book bookFromDB = OBJECT_MAPPER.readValue(
                result.getResponse().
                        getContentAsString(),
                Book.class
        );
        assertEquals(book, bookFromDB);
    }

    @Test
    public void deleteByIdExpectNotFoundStatusForMissingBookTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .delete("/books/" + UUID.randomUUID().toString()))
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
