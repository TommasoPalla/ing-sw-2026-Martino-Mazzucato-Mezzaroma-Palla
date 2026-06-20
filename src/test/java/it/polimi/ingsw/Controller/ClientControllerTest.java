package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Controller.ClientController.ClientController;
import it.polimi.ingsw.CustomException.*;
import it.polimi.ingsw.Enums.ClientState;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.Artist;
import it.polimi.ingsw.Networking.Shared.ServerConnection;
import it.polimi.ingsw.View.ViewInterface;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ClientControllerTest {

    private ClientController clientController;
    private TestServerConnection connection;
    private TestView view;

    private static class TestServerConnection implements ServerConnection {
        public String lastAction = "";
        @Override public void connect() {}
        @Override public void createGame(String playerName, int numPlayers) { lastAction = "createGame:" + numPlayers; }
        @Override public void joinGame(String playerName, int gameID) { lastAction = "joinGame:" + gameID; }
        @Override public void startGame(String playerName, int gameID) { lastAction = "startGame"; }
        @Override public void leaveGame(String playerName, int gameID) { lastAction = "leaveGame"; }
        @Override public void chooseTotem(Color totemColor) { lastAction = "chooseTotem:" + totemColor; }
        @Override public void chooseOfferTile(int index) { lastAction = "chooseOfferTile:" + index; }
        @Override public void drawCard(boolean fromTopRow, boolean fromBuildings, int index) { lastAction = "drawCard"; }
        @Override public void passTurn() { lastAction = "passTurn"; }
    }

    private static class TestView implements ViewInterface {
        public String lastShown = "";
        @Override public void runView() {}
        @Override public void showNameSet(String newName) { lastShown = "nameSet:" + newName; }
        @Override public void showNewAvailableGames() {}
        @Override public void showGameCreated(int gameID) { lastShown = "gameCreated:" + gameID; }
        @Override public void showGameStarted() {}
        @Override public void showPlayerJoinedLobby(String playerName) {}
        @Override public void showSuccessfullyJoinedGame(int gameID, ArrayList<String> playerNames, Map<String, Color> totemColors) {}
        @Override public void showPlayerLeftLobby(String playerName, Color oldColor) {}
        @Override public void showForceQuit(String disconnectedPlayerName, Color oldColor) {}
        @Override public void showNewHost() {}
        @Override public void showStartRound(int round) {}
        @Override public void showChosenTotemColor(String playerName, Color totemColor) {}
        @Override public void showCardDrawn(String player, Card card, boolean topRow, boolean fromBuildings) {}
        @Override public void showEraChanged(int era) {}
        @Override public void showTurnPassed(String playerThatPassed, String newCurrentPlayer) {}
        @Override public void showInitialFood(Map<String, Integer> initialFood) {}
        @Override public void showTileChosen(String playerName, int index) {}
        @Override public void showFoodBonusTile(String playerName, int foodBonus) {}
        @Override public void showNewCurrentPlayer(String playerName, ClientState clientState) {}
        @Override public void showNewGamePhase(GamePhase newGamePhase) {}

        @Override public void showFoodModified(String playerName, int deltaFood, int finalFood) {}
        @Override public void showPrestigePointsModified(String playerName, int deltaPP, int finalPP) {}
        @Override public void showShamanStarsModified(String playerName, int stars){}
        @Override public void showBuildersDiscountModified(String playerName, int discount){}
        @Override public void showGatherersDiscountModified(String playerName, int discount){}
        @Override public void showTotemToTurnTile(String playerName, int index) {}
        @Override public void showEvent(String playerName, EventType eventType, int foodModified, int ppModified) {}

        @Override public void showEndGame(Map<String, Integer> finalRanking) {}
        @Override public void showLeaderboardInfo(int playerPosition) {}
        @Override public void showError(String errorMessage) {}

        @Override
        public void showEndGameLeft() {

        }
    }

    @BeforeEach
    void setUp() {
        clientController = new ClientController();
        connection = new TestServerConnection();
        view = new TestView();
        clientController.bindConnection(connection);
        clientController.bindView(view);
    }

    // --- FUNCTIONAL TESTS ---

    @Test
    void testSetPlayerName() {
        clientController.setClientState(ClientState.SETUP);
        clientController.setPlayerName("TestPlayer");
        assertEquals("TestPlayer", clientController.getPlayerName());
        assertEquals("nameSet:TestPlayer", view.lastShown);
    }

    @Test
    void testCreateGame() {
        clientController.setClientState(ClientState.SETUP);
        clientController.setPlayerName("P1");
        clientController.createGame(3);
        assertEquals("createGame:3", connection.lastAction);
        assertEquals(ClientState.IN_LOBBY, clientController.getClientState());
    }

    @Test
    void testJoinGame() {
        clientController.setClientState(ClientState.SETUP);
        clientController.setPlayerName("P1");
        clientController.joinGame(42);
        assertEquals("joinGame:42", connection.lastAction);
    }

    @Test
    void testLeaveGame() {
        clientController.setPlayerName("P1");
        clientController.updateGameCreated(1, 2); // Sets state to IN_LOBBY
        clientController.leaveGame();
        assertEquals("leaveGame", connection.lastAction);
        assertEquals(ClientState.SETUP, clientController.getClientState());
    }

    @Test
    void testStartGame() {
        clientController.setPlayerName("P1");
        clientController.updateGameCreated(1, 2);
        clientController.startGame();
        assertEquals("startGame", connection.lastAction);
    }

    @Test
    void testChooseTotemColor() {
        clientController.setPlayerName("P1");
        clientController.updateGameCreated(1, 2);
        clientController.chooseTotemColor(Color.BLUE);
        assertEquals("chooseTotem:BLUE", connection.lastAction);
    }

    @Test
    void testChooseOfferTile() {
        clientController.setPlayerName("P1");
        clientController.updateGameCreated(1, 2);
        clientController.updateGameStarted(new ArrayList<>(List.of("P1", "P2")), new HashMap<>(), 
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        assertEquals(ClientState.PLACE_TOTEM, clientController.getClientState());

        clientController.chooseOfferTile(0);
        assertEquals("chooseOfferTile:0", connection.lastAction);
    }

    @Test
    void testDrawCard() {
        clientController.setPlayerName("P1");
        clientController.updateGameCreated(1, 2);
        ArrayList<Card> testTopRow = new ArrayList<>(List.of(new Artist(1, "TEST", 2)));
        clientController.updateGameStarted(new ArrayList<>(List.of("P1", "P2")), new HashMap<>(),
                testTopRow, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        // Simulation of players placing their totems
        clientController.updateCurrentOfferTile("P1", 1);   // Tile B: 0 Above, 1 Below
        clientController.updateCurrentOfferTile("P2", 0);   // Tile C: 1 Above, 0 Below
        clientController.updateCurrentPlayer();

        assertEquals(ClientState.DRAW_CARD, clientController.getClientState());

        clientController.drawCard(true, false, 0);
        assertEquals("drawCard", connection.lastAction);
    }

    @Test
    void testPassTurn() {
        clientController.setPlayerName("P1");
        clientController.updateGameCreated(1, 2);
        clientController.updateGameStarted(new ArrayList<>(List.of("P1", "P2")), new HashMap<>(), 
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        // Simulation of players placing their totems
        clientController.updateCurrentOfferTile("P1", 0);   // Tile B: 0 Above, 1 Below
        clientController.updateCurrentOfferTile("P2", 1);   // Tile C: 1 Above, 0 Below
        // P1 can't draw anything
        clientController.getLocalModel().getPlayerTribe("P1").setRemainingDraws(0, 0);

        // P1 can pass only if he is in DRAW_CARD
        assertEquals(ClientState.DRAW_CARD, clientController.getClientState());

        clientController.passTurn();
        assertEquals("passTurn", connection.lastAction);
    }
    @Test
    void testLeave() {
        clientController.setClientState(ClientState.END_GAME);
        clientController.leave();
        assertEquals(ClientState.SETUP, clientController.getClientState());
    }

    @Test
    void testUpdateGameCreated() {
        clientController.updateGameCreated(10, 2);
        assertEquals(ClientState.IN_LOBBY, clientController.getClientState());
        assertEquals(10, clientController.getLocalModel().getGameId());
        assertEquals("gameCreated:10", view.lastShown);
    }

    // --- EXCEPTION TESTS ---

    @Test
    void testSetPlayerNameIllegalState() {
        clientController.setClientState(ClientState.IN_LOBBY);
        assertThrows(IllegalClientStateActionException.class, () -> clientController.setPlayerName("NewName"));
    }

    @Test
    void testCreateGameIllegalState() {
        clientController.setClientState(ClientState.IN_LOBBY);
        assertThrows(IllegalClientStateActionException.class, () -> clientController.createGame(2));
    }

    @Test
    void testJoinGameIllegalState() {
        clientController.setClientState(ClientState.IN_LOBBY);
        assertThrows(IllegalClientStateActionException.class, () -> clientController.joinGame(1));
    }

    @Test
    void testLeaveGameIllegalState() {
        clientController.setClientState(ClientState.SETUP);
        assertThrows(IllegalClientStateActionException.class, () -> clientController.leaveGame());
    }

    @Test
    void testStartGameIllegalState() {
        clientController.setClientState(ClientState.SETUP);
        assertThrows(IllegalClientStateActionException.class, () -> clientController.startGame());
    }

    @Test
    void testChooseTotemColorIllegalState() {
        clientController.setClientState(ClientState.SETUP);
        assertThrows(IllegalClientStateActionException.class, () -> clientController.chooseTotemColor(Color.BLUE));
    }

    @Test
    void testChooseOfferTileIllegalState() {
        clientController.setClientState(ClientState.SETUP);
        assertThrows(IllegalClientStateActionException.class, () -> clientController.chooseOfferTile(0));
    }

    @Test
    void testDrawCardIllegalState() {
        clientController.setClientState(ClientState.SETUP);
        assertThrows(IllegalClientStateActionException.class, () -> clientController.drawCard(true, false, 0));
    }

    @Test
    void testPassTurnIllegalState() {
        clientController.setClientState(ClientState.SETUP);
        assertThrows(IllegalClientStateActionException.class, () -> clientController.passTurn());
    }

    @Test
    void testLeaveIllegalState() {
        clientController.setClientState(ClientState.SETUP);
        assertThrows(IllegalClientStateActionException.class, () -> clientController.leave());
    }

    @Test
    void testSetPlayerNameSameName() {
        clientController.setClientState(ClientState.SETUP);
        clientController.setPlayerName("TestName");
        assertThrows(IllegalArgumentException.class, () -> clientController.setPlayerName("TestName"));
    }

    @Test
    void testCreateGameInvalidPlayers() {
        clientController.setClientState(ClientState.SETUP);
        assertThrows(IllegalArgumentException.class, () -> clientController.createGame(1));
        assertThrows(IllegalArgumentException.class, () -> clientController.createGame(6));
    }

    @Test
    void testDrawCardNotInTurn() {
        clientController.setClientState(ClientState.NOT_IN_TURN);
        assertThrows(IllegalClientStateActionException.class, () -> clientController.drawCard(true, false, 0));
    }

    @Test
    void testChooseOfferTileNotInTurn() {
        clientController.setClientState(ClientState.NOT_IN_TURN);
        assertThrows(IllegalClientStateActionException.class, () -> clientController.chooseOfferTile(0));
    }

}
