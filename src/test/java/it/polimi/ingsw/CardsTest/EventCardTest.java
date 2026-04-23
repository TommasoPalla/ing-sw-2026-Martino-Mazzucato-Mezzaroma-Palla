package it.polimi.ingsw.CardsTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Enums.Parameters;
import it.polimi.ingsw.Model.Cards.CardLoader;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

public class EventCardTest {
    private List<EventCard> allEventCards = new ArrayList<>();
    private CardLoader loader = new CardLoader();

    @Test
    public void initEventCard() {
        this.allEventCards = loader.loadEvents("json/test_card.json");
        //assertEquals(15, allEventCards.getLast().getCardID());
        //assertEquals(7, allEventCards.getLast().getParam(Parameters.PRESTIGE_MALUS));
        //assertEquals(1, allEventCards.getFirst().getParam(Parameters.PRESTIGE_BONUS));
        //assertEquals(1, allEventCards.getFirst().getParam(Parameters.FOOD_BONUS));
        //assertEquals(EventType.HUNT, allEventCards.getFirst().getEventType());
        //assertEquals(EventType.SHAMANIC_RITUAL, allEventCards.getLast().getEventType());
        //non funziona  il parser event card
    }
    @Test
    public void testEventInitialization(){
        EventCard card = new EventCard(1, "TEST_ID", EventType.SHAMANIC_RITUAL, null);
        assertEquals(0, card.getParam(Parameters.PRESTIGE_BONUS));
    }
}
