package it.polimi.ingsw.Model.Deck;

import it.polimi.ingsw.Model.Game.Game;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeckTest {
    private Deck deck;

    @ParameterizedTest(name = "Checking deck initialization for {0} players")
    @ValueSource(ints = {2, 3, 4, 5})
    void initDeck(int numPlayers) {
        Game game = new Game(1, numPlayers);
        deck = new Deck(game, "json/cards.json");
    }

    @ParameterizedTest(name = "Checking deck initialization for {0} players")
    @ValueSource(ints = {2, 3, 4, 5})
    void testBuildingDeckCorrectSize(int numPlayers) {
        initDeck(numPlayers);
        int numBuildings;
        if (numPlayers == 2) {
            numBuildings = 6;
        } else if (numPlayers == 3) {
            numBuildings = 8;
        } else if (numPlayers == 4) {
            numBuildings = 9;
        } else {
            numBuildings = 10;
        }
        assertEquals(numBuildings, deck.getBuildingsDeck().size());
    }
}