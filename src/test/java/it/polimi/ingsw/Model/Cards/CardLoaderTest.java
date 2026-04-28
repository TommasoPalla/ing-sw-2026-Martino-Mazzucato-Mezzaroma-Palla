package it.polimi.ingsw.Model.Cards;

import it.polimi.ingsw.Model.Parser.CardLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardLoaderTest {

    @Test
    void loadBuildings() {
        CardLoader loader = new CardLoader();
        List<BuildingCard> allBuildings = loader.loadBuildings("json/buildingTest.json");
        assertEquals("E1_B_2", allBuildings.get(1).getCardID());
    }
}