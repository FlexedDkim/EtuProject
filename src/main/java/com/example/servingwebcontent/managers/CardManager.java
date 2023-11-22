package com.example.servingwebcontent.managers;

import com.example.servingwebcontent.database.Card;
import com.example.servingwebcontent.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CardManager {
    @Autowired
    private static CardRepository cardRepository;

    public CardManager(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }
    public static List<Card> readAllByIdOwn(Long idown) {
        return cardRepository.findAllByIdOwn(idown);
    }

    public static List<Card> readAllByIdOwnAndStatus(Long idown,String status) {
        return cardRepository.findAllByIdOwnAndStatus(idown,status);
    }
    public static Optional<Card> readAllById(Long id) {
        return cardRepository.findAllById(id);
    }
    public static Card createCard(Card card) {
        return cardRepository.save(card);
    }

    public static List<Card> readAllByStatus(String status) {
        return cardRepository.findAllByStatus(status);
    }
}
