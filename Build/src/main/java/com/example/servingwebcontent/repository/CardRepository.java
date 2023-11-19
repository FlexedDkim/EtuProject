package com.example.servingwebcontent.repository;

import com.example.servingwebcontent.database.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long>  {
    List<Card> findAllByIdOwn(Long idown);
}
