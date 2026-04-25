package it.polimi.ingsw.Model.Users;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.GameBoard.OfferTrack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PlayerTest {
    Player player1 = new Player("Giocatore1", Color.YELLOW);
    Player player2 = new Player("Giocatore2", Color.RED);
    OfferTrack  offerTrack= new OfferTrack(2);
    @BeforeEach
    void setUp() {
    }
    @Test
    public void testConstructor() {
        // ChooseOfferTile Testing
        player1.chooseOfferTile(1, offerTrack);
        player2.chooseOfferTile(2, offerTrack);
        offerTrack.getOfferTiles().get(2).free();
        assertTrue(offerTrack.getOfferTiles().get(1).isOccupied()); // funziona, la tile occupata da
        assertEquals(player1.getName(), offerTrack.getOfferTiles().get(1).getCurrentOccupant().getName()); // funziona, la tile ha salvato come occupant il player
        player1.getCurrentOfferTile().free();
        player2.chooseOfferTile(1, offerTrack);
        assertEquals(player1.getCurrentOfferTile(), offerTrack.getOfferTiles().get(1)); // funziona, il player salva la tile come sua currentTile;
        assertEquals(player2.getName(), offerTrack.getOfferTiles().get(1).getCurrentOccupant().getName()); // funziona anche la free, si libera la turn tile 1 e viene occupata dal player
    }
}
