package it.polimi.ingsw.Model.Cards;

import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Parser.CardLoader;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardLoaderTest {
    CardLoader loader = new CardLoader();
    @Test
    void loadBuildings() {
        List<BuildingCard> allBuildings = loader.loadBuildings("json/test_card.json");
        assertEquals("E2_B_7", allBuildings.get(1).getCardID());
    }
    @Test
    void loadCharacters() {
        List<CharacterCard> allCharacters = loader.loadCharacters("json/cards.json");
        assertEquals("E3_CS_5", allCharacters.getLast().getCardID());
        assertEquals(2, allCharacters.get(6).getBuildingDiscount());
    }
    void loadEvents() {
        List<EventCard> allEvents = loader.loadEvents("json/cards.json");
        assertEquals(EventType.SHAMANIC_RITUAL, allEvents.getLast().getEventType());
        assertEquals(15, allEvents.getLast().getPrestigeBonus());
        assertEquals(7, allEvents.getLast().getPrestigeMalus());
    }
}