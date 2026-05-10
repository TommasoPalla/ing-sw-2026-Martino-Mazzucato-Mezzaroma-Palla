package it.polimi.ingsw.Model.Parser;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Cards.EventCard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardLoaderTest {
    CardLoader loader;

    @BeforeEach
    void initCardLoader(){
        loader = new CardLoader();
    }
    @Test
    void loadCharacters() {
        List<CharacterCard> characters;
        characters = loader.loadCharacters("json/test_card.json");
        assertEquals(1, characters.getFirst().getEra());
        assertEquals("E1_CH_1", characters.getFirst().getCardID());
        assertTrue(characters.getFirst().isAlphaHunter());
        assertEquals(3, characters.getLast().getNumPlayersFlag());

    }

    @Test
    void loadEventsTest() {
        List<EventCard> events;
        events = loader.loadEvents("json/test_card.json");
        assertEquals(1, events.getFirst().getEra());
        assertEquals("E1_E_H", events.getFirst().getCardID());
        assertEquals(EventType.HUNT, events.getFirst().getEventType());
        assertEquals(2, events.get(1).getEra());
        assertEquals("E2_E_S", events.get(1).getCardID());
        assertEquals(EventType.SUSTENANCE, events.get(1).getEventType());
        assertEquals(3, events.getLast().getEra());
        assertEquals("E3_E_C", events.getLast().getCardID());
        assertEquals(EventType.CAVE_PAINTINGS, events.getLast().getEventType());
    }

    @Test
    void loadBuildings() {
        List<BuildingCard> buildings;
        buildings = loader.loadBuildings("json/test_card.json");
        assertEquals(GamePhase.ON_DRAW, buildings.getFirst().getActivatedAt());
        assertEquals(Effect.COMBO_FOOD, buildings.getFirst().getEffect());
        assertEquals("E3_B_8", buildings.get(2).getCardID());
    }
}