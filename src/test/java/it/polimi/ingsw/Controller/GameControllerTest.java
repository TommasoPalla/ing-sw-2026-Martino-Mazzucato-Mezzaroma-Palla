package it.polimi.ingsw.Controller;

import it.polimi.ingsw.CustomException.*;
import it.polimi.ingsw.CustomException.UIException.*;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.EventManagement.PlayerEventResults;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Networking.Shared.ClientNotifier;
import it.polimi.ingsw.View.GamePlayers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {

    private GameController gameController;
    private Game game;
    private TestClientNotifier notifier1;
    private TestClientNotifier notifier2;

    private static class TestClientNotifier implements ClientNotifier {
        public List<String> notifications = new ArrayList<>();
        public boolean gameStarted = false;
        public Map<String, Color> chosenColors = new HashMap<>();

        @Override public void notifyGameCreated(int gameID, String playerName, int playerNum) { notifications.add("notifyGameCreated:" + gameID); }
        @Override public void notifyGameStarted(List<String> order, Map<String, Integer> food, ArrayList<Card> top, ArrayList<Card> bottom, ArrayList<BuildingCard> bTop, ArrayList<BuildingCard> bBottom) { 
            gameStarted = true; 
            notifications.add("notifyGameStarted");
        }
        @Override public void notifyNewPlayerConnected(String playerName) { notifications.add("notifyNewPlayerConnected:" + playerName); }
        @Override public void notifySuccessfullyJoinedGame(int gameID, int playerNum, ArrayList<String> players, Map<String, Color> totemColors) { notifications.add("notifySuccessfullyJoinedGame"); }
        @Override public void notifyPlayerLeftGame(String playerName) { notifications.add("notifyPlayerLeftGame:" + playerName); }
        @Override public void notifyNewHost() { notifications.add("notifyNewHost"); }
        @Override public void notifyAvailableGames(Map<Integer, GamePlayers> availableGames) { notifications.add("notifyAvailableGames"); }
        @Override public void notifyTotemColor(String playerName, Color totemColor) { 
            chosenColors.put(playerName, totemColor); 
            notifications.add("notifyTotemColor:" + playerName + ":" + totemColor);
        }
        @Override public void notifyStartRound(Map<EventType, ArrayList<PlayerEventResults>> results, ArrayList<Card> top, ArrayList<Card> bottom, ArrayList<BuildingCard> bTop, ArrayList<BuildingCard> bBottom) { notifications.add("notifyStartRound"); }
        @Override public void notifyDrawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index) { notifications.add("notifyDrawnCard:" + playerName + ":" + index); }
        @Override public void notifyChosenTile(String playerName, int index) { notifications.add("notifyChosenTile:" + playerName + ":" + index); }
        @Override public void notifyPassedTurn(String playerName) { notifications.add("notifyPassedTurn:" + playerName); }
        @Override public void notifyNewFood(String playerName, int food) { notifications.add("notifyNewFood:" + playerName + ":" + food); }
        @Override public void notifyNewPrestigePoints(String playerName, int pp) { notifications.add("notifyNewPrestigePoints:" + playerName + ":" + pp); }
        @Override public void notifyNewShamansStars(String playerName, int stars) { notifications.add("notifyNewShamansStars:" + playerName + ":" + stars); }
        @Override public void notifyNewBuildersDiscount(String playerName, int discount) { notifications.add("notifyNewBuildersDiscount:" + playerName + ":" + discount); }
        @Override public void notifyNewGatherersDiscount(String playerName, int discount) { notifications.add("notifyNewGatherersDiscount:" + playerName + ":" + discount); }
        @Override public void notifyGamePhase(GamePhase newPhase) { notifications.add("notifyGamePhase:" + newPhase); }
        @Override public void notifyEra(int era) { notifications.add("notifyEra:" + era); }
        @Override public void notifyEndGame(Map<String, Integer> finalRanking) { notifications.add("notifyEndGame"); }
        @Override public void notifyLeaderboardInfo(List<String> leaderboard, int playerPosition) { notifications.add("notifyLeaderboardInfo"); }
        @Override public void notifyForceQuit(String disconnectedPlayer) { notifications.add("notifyForceQuit:" + disconnectedPlayer); }
    }

    @BeforeEach
    void setUp() {
        game = new Game(1, 2);
        gameController = new GameController(game);
        game.setController(gameController);
        notifier1 = new TestClientNotifier();
        notifier2 = new TestClientNotifier();
    }

    @Test
    void testAddClient() {
        gameController.addClient("P1", notifier1);
        assertEquals(1, gameController.getConnectedClients().size());
        assertTrue(gameController.getConnectedClients().contains("P1"));


        gameController.addClient("P2", notifier2);
        assertEquals(2, gameController.getConnectedClients().size());
        assertTrue(gameController.getConnectedClients().contains("P2"));
        assertTrue(game.isReadyToStart());

        // Notify verification (needs to wait for notification thread to actually send it)
        try {Thread.sleep(50);} catch (InterruptedException e) {}
        assertTrue(notifier1.notifications.contains("notifyNewPlayerConnected:P2"));
    }

    @Test
    void testRemoveClient() {
        gameController.addClient("P1", notifier1);
        gameController.addClient("P2", notifier2);
        
        gameController.removeClient("P1");
        assertEquals(1, gameController.getConnectedClients().size());
        assertFalse(gameController.getConnectedClients().contains("P1"));
        assertFalse(game.isReadyToStart());

        // Notify verification (needs to wait for notification thread to actually send it)
        try {Thread.sleep(50);} catch (InterruptedException e) {}
        assertTrue(notifier2.notifications.contains("notifyPlayerLeftGame:P1"));
    }

    @Test
    void testChooseTotemColor() {
        gameController.addClient("P1", notifier1);
        gameController.chooseTotemColor("P1", Color.BLUE);
        
        assertEquals(Color.BLUE, game.getPlayersTotemColors().get("P1"));

        // Notify verification (needs to wait for notification thread to actually send it)
        try {Thread.sleep(50);} catch (InterruptedException e) {}
        assertTrue(notifier1.notifications.contains("notifyTotemColor:P1:BLUE"));
    }

    @Test
    void testStartGame() {
        gameController.addClient("P1", notifier1);
        gameController.addClient("P2", notifier2);
        
        gameController.chooseTotemColor("P1", Color.BLUE);
        gameController.chooseTotemColor("P2", Color.RED);
        
        gameController.startGame("P1");
        
        assertTrue(game.isStarted());
        assertEquals(GamePhase.START_TURN, game.getGamePhase());
    }

    // Used for test readability only
    private void addChooseAndStart(){
        gameController.addClient("P1", notifier1);
        gameController.addClient("P2", notifier2);
        gameController.chooseTotemColor("P1", Color.BLUE);
        gameController.chooseTotemColor("P2", Color.RED);
        gameController.startGame("P1");
    }

    @Test
    void testHandleChooseOfferTile() {
        addChooseAndStart();

        // After startGame, phase is START_TURN, it's P1's turn (shuffled, but here we can check)
        String currentPlayer = game.getCurrentPlayer().getName();
        gameController.handleChooseOfferTile(currentPlayer, 0);
        
        assertTrue(game.getOfferTrack().getOfferTiles().getFirst().isOccupied());
        assertEquals(currentPlayer, game.getOfferTrack().getOfferTiles().getFirst().getCurrentOccupant());
    }

    @Test
    void testSetNextPlayer() {
        addChooseAndStart();

        // First player is chosen randomly by Game.startGame() -> OfferTrack.getTurnTile().initTurnOrder()
        String p1 = game.getCurrentPlayer().getName();
        String p2 = p1.equals("P1") ? "P2" : "P1";

        gameController.handleChooseOfferTile(p1, 0);
        assertEquals(p2, game.getCurrentPlayer().getName());

        gameController.handleChooseOfferTile(p2, 1);
        // Phase should change to ON_DRAW after all players placed totems
        assertEquals(GamePhase.ON_DRAW, game.getGamePhase());
    }

    @Test
    void testHandleDraw() {
        addChooseAndStart();

        // We need to know who is first to simulate the turn
        String p1Name = game.getCurrentPlayer().getName();
        gameController.handleChooseOfferTile(p1Name, 0); // Tile B: 0 Above, 1 Below

        String p2Name = game.getCurrentPlayer().getName();
        gameController.handleChooseOfferTile(p2Name, 1); // Tile C: 1 Above, 0 Below

        // Phase is ON_DRAW. Order is determined by tile positions (B before C)
        // P1 chose B, P2 chose C. So P1 is first in ON_DRAW.
        assertEquals(p1Name, game.getCurrentPlayer().getName());
        assertEquals(0, game.getPlayerByName(p1Name).getRemainingAbove());
        assertEquals(1, game.getPlayerByName(p1Name).getRemainingBelow());

        // Perform a draw from bottom row (index 0)
        gameController.handleDraw(p1Name, false, false, 0);


        // P1 should have finished draws and moved back to turn tile
        assertEquals(0, game.getPlayerByName(p1Name).getRemainingBelow());
        assertEquals(p2Name, game.getCurrentPlayer().getName());

        // Notify verification (needs to wait for notification thread to actually send it)
        try {Thread.sleep(50);} catch (InterruptedException e) {}
        assertTrue(notifier1.notifications.contains("notifyDrawnCard:" + p1Name + ":0"));
        assertTrue(notifier2.notifications.contains("notifyDrawnCard:" + p1Name + ":0"));
    }

    @Test
    void testHandlePassTurn() {
        addChooseAndStart();

        String p1Name = game.getCurrentPlayer().getName();
        gameController.handleChooseOfferTile(p1Name, 0); // Tile B: 0 Above, 1 Below
        String p2Name = game.getCurrentPlayer().getName();
        gameController.handleChooseOfferTile(p2Name, 1); // Tile C: 1 Above, 0 Below

        // Clear character cards to allow passing (simulated)
        game.getOfferTrack().getBottomRow().clear();
        // Since it's empty, P1 should be able to pass
        gameController.handlePassTurn(p1Name);

        assertEquals(p2Name, game.getCurrentPlayer().getName());
    }

    @Test
    void testStartRound() {
        addChooseAndStart();

        int initialRound = game.getCurrentRound();
        gameController.startRound(new HashMap<>());

        assertEquals(initialRound + 1, game.getCurrentRound());
        assertEquals(GamePhase.START_TURN, game.getGamePhase());
    }

    // --- EXCEPTION TESTS ---
    @Test
    void testAddClientFullLobby() {
        gameController.addClient("P1", notifier1);
        gameController.addClient("P2", notifier2);
        TestClientNotifier notifier3 = new TestClientNotifier();
        assertThrows(NotJoinableGameException.class, () -> gameController.addClient("P3", notifier3));
    }

    @Test
    void testAddClientDuplicateName() {
        gameController.addClient("P1", notifier1);
        assertThrows(NotJoinableGameException.class, () -> gameController.addClient("P1", notifier2));
    }

    @Test
    void testChooseTotemColorUnavailable() {
        gameController.addClient("P1", notifier1);
        gameController.addClient("P2", notifier2);
        gameController.chooseTotemColor("P1", Color.BLUE);
        assertThrows(UnavailableColorException.class, () -> gameController.chooseTotemColor("P2", Color.BLUE));
    }

    @Test
    void testStartGameNotHost() {
        gameController.addClient("Host", notifier1);
        gameController.addClient("NotHost", notifier2);
        assertThrows(NotTheHostException.class, () -> gameController.startGame("NotHost"));
    }

    @Test
    void testHandleChooseOfferTileOccupied() {
        addChooseAndStart();

        String p1 = game.getCurrentPlayer().getName();
        gameController.handleChooseOfferTile(p1, 0);
        
        // After P1's turn, currentPlayer is already set to P2 by gameController
        String p2 = game.getCurrentPlayer().getName();
        
        assertThrows(OccupiedTileException.class, () -> gameController.handleChooseOfferTile(p2, 0));
    }

    @Test
    void testHandleIllegalDraw() {
        addChooseAndStart();

        String p1Name = game.getCurrentPlayer().getName();
        gameController.handleChooseOfferTile(p1Name, 0); // Tile B: 0 Above, 1 Below
        
        String p2Name = game.getCurrentPlayer().getName();
        gameController.handleChooseOfferTile(p2Name, 1); // Tile C: 1 Above, 0 Below

        // P1 tries to draw from above (but has 0 draws allowed)
        gameController.handleDraw(p1Name, true, false, 0);

        // Remaining draws should NOT change and turn should NOT pass
        assertEquals(0, game.getPlayerByName(p1Name).getRemainingAbove());
        assertEquals(1, game.getPlayerByName(p1Name).getRemainingBelow());
        assertEquals(p1Name, game.getCurrentPlayer().getName());
    }
}
