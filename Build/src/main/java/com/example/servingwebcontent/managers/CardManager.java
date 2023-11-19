package com.example.servingwebcontent.managers;

import com.example.servingwebcontent.database.Card;
import com.example.servingwebcontent.database.User;
import com.example.servingwebcontent.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public static Card createCard(Card card) {
        return cardRepository.save(card);
    }
}
