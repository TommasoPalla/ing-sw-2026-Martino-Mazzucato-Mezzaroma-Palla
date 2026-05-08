package it.polimi.ingsw.Utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.Artist;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;

import it.polimi.ingsw.Enums.*;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Model.EventManagement.ShamanicRitualEvent;
import org.junit.jupiter.api.*;

import javax.swing.*;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GsonFactoryTest {
    Gson gson = GsonFactory.create();

    @Test
    void serializeDeserializeTest(){
        CharacterCard artistCard = new Artist(1, "TEST_A_1", 2);
        String jsonArtist = gson.toJson(artistCard);

        CharacterCard deserializedArtist = gson.fromJson(jsonArtist, Artist.class);
        assertEquals(Artist.class, deserializedArtist.getClass());
        assertEquals(1, deserializedArtist.getEra());
        assertEquals("TEST_A_1", deserializedArtist.getCardID());
        assertEquals(2, deserializedArtist.getNumPlayersFlag());
    }
    @Test
    void arrayListTest(){
        CharacterCard artist = new Artist(1, "TEST_A_1", 2);
        EventCard ritualEvent = new ShamanicRitualEvent(1, "TEST_SR_1", 3, 5);
        ArrayList<Card> cards = new ArrayList<>();
        cards.add(artist);
        cards.add(ritualEvent);

        Type cardType = new TypeToken<ArrayList<Card>>(){}.getType();
        String jsonCards = gson.toJson(cards, cardType);

        Type type = new TypeToken<ArrayList<Card>>(){}.getType();
        ArrayList<Card> deserializedCards = gson.fromJson(jsonCards, type);

        assertEquals(artist.getClass(), deserializedCards.getFirst().getClass());
        assertEquals(ritualEvent.getClass(), deserializedCards.getLast().getClass());

        CharacterCard deserializedArtist = (CharacterCard) deserializedCards.getFirst();
        assertEquals(artist.getRole(), deserializedArtist.getRole());
        assertEquals(artist.getUsefulStats(), deserializedArtist.getUsefulStats());

        EventCard deserializedShamanicRitual = (EventCard) deserializedCards.getLast();
        assertEquals(ritualEvent.getPrestigeBonus(), deserializedShamanicRitual.getPrestigeBonus());
        assertEquals(ritualEvent.getEventType(), deserializedShamanicRitual.getEventType());
    }
}
