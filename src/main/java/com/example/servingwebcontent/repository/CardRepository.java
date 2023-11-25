package com.example.servingwebcontent.repository;

import com.example.servingwebcontent.database.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long>  {
    List<Card> findAllByIdOwn(Long idown);

    Optional<Card> findAllById(Long id);

    List<Card> findAllByIdExecutorAndStatus(Long idown, String status);

    List<Card> findAllByStatus(String status);

    List<Card> findAllByNameContainingIgnoreCaseAndDescriptionContainingIgnoreCaseAndStatusContainingIgnoreCaseAndIdObject(Optional<String> name,Optional<String> description,Optional<String> status,Long idobject);

}