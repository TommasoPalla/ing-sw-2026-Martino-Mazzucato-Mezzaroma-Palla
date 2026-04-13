package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.*;
import cards_and_deck.CharacterCard;
import cards_and_deck.CardLoader;

import java.util.ArrayList;
import java.util.List;

public class CharacterCardTest {
    private List<CharacterCard> allCharacterCards = new ArrayList<>();
    CardLoader loader = new CardLoader();

    @Test
    public void initCharacters(){
        this.allCharacterCards = loader.loadCharacters();
        System.out.println(this.allCharacterCards.get(0).getCardID());
    }
}
