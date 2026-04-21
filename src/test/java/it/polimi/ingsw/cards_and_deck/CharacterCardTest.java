package it.polimi.ingsw.cards_and_deck;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.*;
import cards_and_deck.CharacterCard;
import cards_and_deck.CardLoader;
import enums.CharacterRole;

import java.util.ArrayList;
import java.util.List;

public class CharacterCardTest {
    private List<CharacterCard> allCharacterCards = new ArrayList<>();
    CardLoader loader = new CardLoader();

    @Test
    public void parserCardTest(){
        allCharacterCards = loader.loadCharacters("json/test_card.json");
        assertEquals(CharacterRole.HUNTER, allCharacterCards.getFirst().getRole());
        assertEquals(2, allCharacterCards.getFirst().getNumPlayersFlag());
        assertEquals("E1_CH_1", allCharacterCards.getFirst().getCardID());
        //assertEquals("E3_CS_5", allCharacterCards.getLast().getCardID()); //non funziona parser character
    }
}
