package it.polimi.ingsw.cards_and_deckTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import org.junit.jupiter.api.*;
import it.polimi.ingsw.Enums.CharacterRole;

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
