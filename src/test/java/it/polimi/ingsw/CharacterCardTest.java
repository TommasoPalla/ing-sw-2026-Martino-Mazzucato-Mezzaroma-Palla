package it.polimi.ingsw;

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
    public void initCharacters(){
        this.allCharacterCards = loader.loadCharacters("json/cards.json");
        System.out.println(this.allCharacterCards.get(1).getRole());
        assertEquals(CharacterRole.HUNTER, this.allCharacterCards.get(1).getRole());
        assertEquals(2, this.allCharacterCards.get(1).getNumPlayersFlag());
    }
}
