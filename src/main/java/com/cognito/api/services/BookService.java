package com.cognito.api.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import com.cognito.api.models.Book;
import com.cognito.api.repositories.BookRepository;

@Service
public class BookService {
    @Autowired
    private BookRepository bookRepository;

    public void createBook(Book book) {
        bookRepository.save(book);
    }

    public void deleteBook(Book book) {
        bookRepository.deleteById(book.getId());
    }

    public void updateBook(Book book) {
        bookRepository.save(book);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public List<Book> getAvailableBooks() {
        return bookRepository.findByQuantityGreaterThan(0);
    }

    public List<Book> searchBooks(String title) {
        if (title == null || title.length() == 0) {
            return new ArrayList<>();
        }

        return bookRepository.findByTitleContainingIgnoreCase(title);
    }

}

