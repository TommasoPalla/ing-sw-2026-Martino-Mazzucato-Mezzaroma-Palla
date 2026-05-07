package it.polimi.ingsw.Model.GameBoard;

import it.polimi.ingsw.Enums.Color;

import it.polimi.ingsw.Model.Game.Game;
import org.junit.jupiter.api.*;
import it.polimi.ingsw.Model.Users.Player;

import static org.junit.jupiter.api.Assertions.*;

public class OfferTileTest {
    OfferTile tileA = new OfferTile('A');
    OfferTile tileG = new OfferTile('G');
    Game game = new Game(5, 4);
    Player player = new Player(game,"AAA");
    @Test
    void initOfferTile(){
        //tile A test
        assertEquals(0, tileA.getCardsFromAbove());
        assertEquals(0, tileA.getCardsFromBelow());
        assertFalse(tileA.isOccupied());
        assertEquals(3, tileA.getFoodBonus());
        assertNull(tileA.getCurrentOccupant());
        assertEquals('A', tileA.getTileCode());

        //tile G test
        assertEquals(2, tileG.getCardsFromAbove());
        assertEquals(1, tileG.getCardsFromBelow());
        assertFalse(tileG.isOccupied());
        assertEquals(0, tileG.getFoodBonus());
        assertNull(tileG.getCurrentOccupant());
        assertEquals('G', tileG.getTileCode());
    }
    @Test
    void occupyOfferTileA(){
        //tile A test
        tileA.occupy(player.getName());
        assertTrue(tileA.isOccupied());
        assertEquals(player, tileA.getCurrentOccupant());
        tileA.free();
        assertFalse(tileA.isOccupied());
        assertNull(tileA.getCurrentOccupant());

        //tile G test
        tileG.occupy(player.getName());
        assertTrue(tileG.isOccupied());
        assertEquals(player, tileG.getCurrentOccupant());
        tileG.free();
        assertFalse(tileG.isOccupied());
        assertNull(tileG.getCurrentOccupant());
    }
}
