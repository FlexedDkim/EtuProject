package com.example.servingwebcontent.repository;

import com.example.servingwebcontent.database.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long>  {
    List<Card> findAllByIdOwn(Long idown);

    Optional<Card> findAllById(Long id);

    List<Card> findAllByStatus(String status);
}
