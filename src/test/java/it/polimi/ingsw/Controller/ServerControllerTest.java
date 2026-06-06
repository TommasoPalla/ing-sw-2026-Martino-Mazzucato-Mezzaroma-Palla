package it.polimi.ingsw.Controller;

import it.polimi.ingsw.CustomException.UIException.*;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.EventManagement.PlayerEventResults;
import it.polimi.ingsw.Networking.Shared.ClientNotifier;
import it.polimi.ingsw.Networking.Shared.PlayerRecord;
import it.polimi.ingsw.Networking.Shared.ServerController;
import it.polimi.ingsw.View.GamePlayers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ServerControllerTest {

    private ServerController serverController;
    private TestClientNotifier notifier1;
    private TestClientNotifier notifier2;

    private static class TestClientNotifier implements ClientNotifier {
        public int gameCreatedId = -1;
        public List<String> availableGames = new ArrayList<>();
        public List<String> firstTurnOrder = new ArrayList<>();

        @Override public void notifyGameCreated(int gameID, String playerName, int playerNum) { this.gameCreatedId = gameID; }
        @Override public void notifyGameStarted(List<String> order, Map<String, Integer> food, ArrayList<Card> top, ArrayList<Card> bottom, ArrayList<BuildingCard> bTop, ArrayList<BuildingCard> bBottom) {
            this.firstTurnOrder = order;
        }
        @Override public void notifyNewPlayerConnected(String playerName) {}
        @Override public void notifySuccessfullyJoinedGame(int gameID, int playerNum, ArrayList<String> players, Map<String, Color> totemColors) {}
        @Override public void notifyPlayerLeftGame(String playerName) {}
        @Override public void notifyNewHost() {}
        @Override public void notifyAvailableGames(Map<Integer, GamePlayers> availableGames) { this.availableGames.add(availableGames.toString()); }
        @Override public void notifyTotemColor(String playerName, Color totemColor) {}
        @Override public void notifyStartRound(Map<EventType, ArrayList<PlayerEventResults>> results, ArrayList<Card> top, ArrayList<Card> bottom, ArrayList<BuildingCard> bTop, ArrayList<BuildingCard> bBottom) {}
        @Override public void notifyDrawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index) {}
        @Override public void notifyChosenTile(String playerName, int index) {}
        @Override public void notifyPassedTurn(String playerName) {}
        @Override public void notifyNewFood(String playerName, int newFoodReserve) {}
        @Override public void notifyNewPrestigePoints(String playerName, int newPP) {}
        @Override public void notifyNewShamansStars(String playerName, int stars) {}
        @Override public void notifyNewBuildersDiscount(String playerName, int discount) {}
        @Override public void notifyNewGatherersDiscount(String playerName, int discount) {}
        @Override public void notifyGamePhase(GamePhase newPhase) {}
        @Override public void notifyEra(int era) {}
        @Override public void notifyEndGame(Map<String, Integer> finalRanking) {}
        @Override public void notifyLeaderboardInfo(List<String> leaderboard, int playerPosition) {}
        @Override public void notifyForceQuit(String disconnectedPlayer) {}
    }

    @BeforeEach
    void setUp() {
        serverController = new ServerController();
        notifier1 = new TestClientNotifier();
        notifier2 = new TestClientNotifier();
    }

    @Test
    void testCreateNewGame() {
        int gameId = serverController.createNewGame(notifier1, "Player1", 2);
        assertTrue(gameId >= 0);
        // Wait for notification thread
        try { Thread.sleep(50); } catch (InterruptedException e) {}
        assertEquals(gameId, notifier1.gameCreatedId);
    }

    @Test
    void testJoinGame() {
        int gameId = serverController.createNewGame(notifier1, "Player1", 2);
        PlayerRecord joiner = new PlayerRecord(gameId, "Player2");
        serverController.joinGame(notifier2, joiner);
        // If correct no exception should be thrown
    }

    @Test
    void testLeaveGame() {
        int gameId = serverController.createNewGame(notifier1, "Player1", 2);
        PlayerRecord leaver = new PlayerRecord(gameId, "Player1");
        serverController.leaveGame(leaver);
        
        // Game is removed since it's empty
        PlayerRecord joiner = new PlayerRecord(gameId, "Player2");
        assertThrows(NotJoinableGameException.class, () -> serverController.joinGame(notifier2, joiner));
    }

    @Test
    void testStartGame() {
        int gameId = serverController.createNewGame(notifier1, "Host", 2);
        serverController.joinGame(notifier2, new PlayerRecord(gameId, "Player2"));
        
        serverController.chooseTotemColor(new PlayerRecord(gameId, "Host"), Color.BLUE);
        serverController.chooseTotemColor(new PlayerRecord(gameId, "Player2"), Color.RED);
        
        serverController.startGame("Host", gameId);
        // If correct no exception should be thrown
    }

    @Test
    void testChooseTotemColor() {
        int gameId = serverController.createNewGame(notifier1, "P1", 2);
        serverController.chooseTotemColor(new PlayerRecord(gameId, "P1"), Color.BLUE);
        // If correct no exception should be thrown
    }

    @Test
    void testChooseOfferTile() {
        int gameId = serverController.createNewGame(notifier1, "P1", 2);
        serverController.joinGame(notifier2, new PlayerRecord(gameId, "P2"));
        serverController.chooseTotemColor(new PlayerRecord(gameId, "P1"), Color.BLUE);
        serverController.chooseTotemColor(new PlayerRecord(gameId, "P2"), Color.RED);
        serverController.startGame("P1", gameId);

        // Wait for async notifications to create turnOrder
        try { Thread.sleep(50); } catch (InterruptedException e) {}
        
        String firstPlayer = notifier1.firstTurnOrder.getFirst();
        
        // Simulating first player placing totem
        serverController.chooseOfferTile(new PlayerRecord(gameId, firstPlayer), 0);
        // If correct no exception should be thrown
    }

    @Test
    void testHandleDisconnectionInLobby() {
        int gameId = serverController.createNewGame(notifier1, "P1", 2);
        serverController.handleDisconnection(new PlayerRecord(gameId, "P1"));
        
        // Player should be removed and, since he was the only one, game should be eliminated
        assertThrows(NotJoinableGameException.class, () -> serverController.joinGame(notifier2, new PlayerRecord(gameId, "P2")));
    }

    // --- EXCEPTION TESTS ---

    @Test
    void testJoinNonExistentGame() {
        PlayerRecord record = new PlayerRecord(999, "Player1");
        assertThrows(NotJoinableGameException.class, () -> serverController.joinGame(notifier1, record));
    }

    @Test
    void testStartGameNotHost() {
        int gameId = serverController.createNewGame(notifier1, "Host", 2);
        serverController.joinGame(notifier2, new PlayerRecord(gameId, "NotHost"));
        
        assertThrows(NotTheHostException.class, () -> serverController.startGame("NotHost", gameId));
    }

    @Test
    void testStartGameNotEnoughPlayers() {
        int gameId = serverController.createNewGame(notifier1, "Host", 3);
        // Only 1 player (Host), need 3
        assertThrows(NotEnoughPlayersException.class, () -> serverController.startGame("Host", gameId));
    }
}
