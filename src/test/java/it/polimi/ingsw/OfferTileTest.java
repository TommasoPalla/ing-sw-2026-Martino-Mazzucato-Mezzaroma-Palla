package it.polimi.ingsw;

import enums.Color;
import game_board.OfferTile;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.*;
import users.Player;
import users.Tribe;

public class OfferTileTest {
    OfferTile tile = new OfferTile('A');
    Player player = new Player("palle", new Tribe(), Color.BLACK);
    @Test
    void initOfferTile(){
        assertEquals(0, tile.getCardsFromAbove());
        assertEquals(0, tile.getCardsFromBelow());
        assertEquals(false, tile.isOccupied());
        assertEquals(3, tile.getFoodBonus());
        assertEquals(null, tile.getCurrentOccupant());
        tile.occupy(player);
        assertEquals(true, tile.isOccupied());
        assertEquals(player, tile.getCurrentOccupant());

    }
}
