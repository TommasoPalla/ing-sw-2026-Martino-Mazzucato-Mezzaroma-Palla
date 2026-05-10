package it.polimi.ingsw.Model.Parser;

import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Model.Cards.EventCard;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.*;

class EventRegistryTest {

    @Test
    void createEventTest() {
        EventRegistry registry = new EventRegistry();
        EventCardDTO card = new EventCardDTO(1, "id", EventType.SUSTENANCE, 0, 1, 0, 2, 0);
        try {
            EventCard createdCard = registry.createEvent(card);
            assertEquals(1, createdCard.getEra());
            assertEquals("id", createdCard.getCardID());
            assertEquals(EventType.SUSTENANCE, createdCard.getEventType());
            assertEquals(0, createdCard.getArtistThreshold());
            assertEquals(1, createdCard.getPrestigeMalus());
            assertEquals(0, createdCard.getPrestigeBonus());
            assertEquals(2, createdCard.getFoodMalus());
            assertEquals(0, createdCard.getFoodBonus());
        }catch (InvocationTargetException | NoSuchMethodException | InstantiationException
                | IllegalAccessException e) {
            System.out.println("problem!");
        }

    }
}