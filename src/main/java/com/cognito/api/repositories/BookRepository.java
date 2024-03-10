package com.cognito.api.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.cognito.api.models.Book;

public interface BookRepository extends JpaRepository<Book,Long> {
    List<Book> findByQuantityGreaterThan(int quantity);

    List<Book> findByTitleContainingIgnoreCase(String title);
}
