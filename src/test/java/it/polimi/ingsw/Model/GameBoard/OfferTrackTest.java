package it.polimi.ingsw.Model.GameBoard;

import static org.junit.jupiter.api.Assertions.assertEquals;

import it.polimi.ingsw.Model.Cards.Characters.Artist;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Model.EventManagement.SustenanceEvent;
import it.polimi.ingsw.Model.Game.Game;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

public class OfferTrackTest {
    OfferTrack track;
    @BeforeEach
    public void init(){
        Game game = new Game(6, 3);
        track =  new OfferTrack(game, 4);
    }
    @Test
    public void getBottomEventsTest(){
        Artist char1 = new Artist(2, "Artistaa", 3);
        /*avevo settato bottomRow come public, in realtà andrebbe definita
        questa classe di test come classe doppelganger di OfferTrack, con le adeguate modifiche che la isolano
         da altre classi (e.g. permettere di inizializzare la bottomRow senza dover chiamare il deck) */
        track.bottomRow.add(0, char1);
        Artist char2 = new Artist(1, "AC2", 4);
        track.bottomRow.add(1, char2);
        SustenanceEvent ev = new SustenanceEvent(1, "E", 0, 0);
        track.bottomRow.add(2, ev);

        ArrayList<EventCard> bottomEventCards = track.getBottomEvents();
        assertEquals(EventType.SUSTENANCE, bottomEventCards.getFirst().getEventType());
    }
}
