package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.*;
import cards_and_deck.CharacterCard;
import cards_and_deck.CardLoader;
import enums.CharacterRole;
import cards_and_deck.EventCard;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class EventCardTest {
    private List<EventCard> allEventCards = new ArrayList<>();
    private CardLoader loader = new CardLoader();

    @Test
    public void initEventCard() {
        this.allEventCards = loader.loadEvents("json/test_card.json");
    }
}
