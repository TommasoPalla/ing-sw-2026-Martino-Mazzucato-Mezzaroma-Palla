package it.polimi.ingsw.Model.Cards;

import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Parser.CardLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardLoaderTest {
    CardLoader loader = new CardLoader();
    @Test
    void loadBuildings() {
        List<BuildingCard> allBuildings = loader.loadBuildings("json/buildingTest.json");
        assertEquals("E1_B_2", allBuildings.get(1).getCardID());
    }
    @Test
    void loadCharacters() {
        List<CharacterCard> allCharacters = loader.loadCharacters("json/test_card.json");
        assertEquals("E1_CA_1", allCharacters.get(1).getCardID());
    }
}