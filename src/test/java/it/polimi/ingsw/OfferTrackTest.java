package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cards_and_deck.*;
import enums.CharacterRole;
import enums.EventType;
import game_board.OfferTrack;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

public class OfferTrackTest {
    OfferTrack track;
    @BeforeEach
    public void init(){
        track =  new OfferTrack(4);
    }
    @Test
    public void getBottomEventsTest(){
        CharacterCard char1 = CharacterCard.createArtistOrGatherer(1, "AB", 3,
                CharacterRole.ARTIST);
        /*avevo settato bottomRow come public, in realtà andrebbe definita
        questa classe di test come classe doppelganger di OfferTrack, con le adeguate modifiche che la isolano
         da altre classi (e.g. permettere di inizializzare la bottomRow senza dover chiamare il deck) */
        track.bottomRow.add(0, char1);
        CharacterCard char2 = CharacterCard.createArtistOrGatherer(1, "AC", 3,
                CharacterRole.ARTIST);
        track.bottomRow.add(1, char2);
        EventCard ev = new EventCard(1, "E", EventType.SUSTENANCE, null);
        track.bottomRow.add(2, ev);

        ArrayList<EventCard> bottomEventCards = track.getBottomEvents();
        assertEquals(EventType.SUSTENANCE, bottomEventCards.getFirst().getEventType());
    }
}
