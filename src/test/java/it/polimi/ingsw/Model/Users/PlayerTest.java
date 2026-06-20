package it.polimi.ingsw.Model.Users;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.GameBoard.OfferTile;
import it.polimi.ingsw.Model.GameBoard.OfferTrack;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PlayerTest {
    Game game;
    Player player1;
    Player player2;
    OfferTrack offerTrack;

    @BeforeEach
    void setUp() {
        game = new Game(1, 4);
        player1 = new Player(game, "ONE", Color.RED);
        player2 = new Player(game, "TWO", Color.BLUE);
        offerTrack = new OfferTrack(game, 2);
    }

    @Test
    public void setRemainingDraws(){
        player1.setRemainingDraws(1, 2);
        assertEquals(1, player1.getRemainingAbove());
        assertEquals(2, player1.getRemainingBelow());
    }
    @Test
    public void additionalDraw(){
        player1.setCanDrawAdditional(true);
        assertTrue(player1.getCanDrawAdditional());
        player1.setCanDrawAdditional(false);
        assertFalse(player1.getCanDrawAdditional());
    }
    @Test
    public void currentOfferTile(){
        OfferTile testTile = offerTrack.getOfferTiles().get(1);     //Tile C: 1 draw from above, 0 from below
        player1.setCurrentOfferTile(testTile);
        assertEquals(testTile, player1.getCurrentOfferTile());
        assertEquals(0, player1.getRemainingBelow());
        assertEquals(1, player1.getRemainingAbove());

        player1.freeOfferTile();

        assertNull(player1.getCurrentOfferTile());
        assertEquals(0, player1.getRemainingBelow());
        assertEquals(0, player1.getRemainingAbove());
    }
    @Test
    public void noMoreDraws(){
        player1.setRemainingDraws(0, 0);
        // no more draws left, cannot draw from ANY of the card piles
        assertFalse(player1.drawable(false, false, 0, offerTrack));
        assertFalse(player1.drawable(true, false, 0, offerTrack));
        assertFalse(player1.drawable(true, true, 0, offerTrack));
        assertFalse(player1.drawable(false, true, 0, offerTrack));
    }
}
