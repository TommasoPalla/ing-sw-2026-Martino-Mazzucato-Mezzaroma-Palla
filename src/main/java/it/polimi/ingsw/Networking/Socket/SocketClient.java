package it.polimi.ingsw.Networking.Socket;

import it.polimi.ingsw.Controller.ClientController.ClientController;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.View.GamePlayers;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SocketClient implements VirtualSocketClient {
    /*final BufferedReader input;  potrebbe essere necessario un reader diverso: da definire
    final VirtualSocketServer server;   */

    //run(), runCli(), runGUI()
    private final ClientController controller;
    public SocketClient(ClientController controller){
        this.controller = controller;
    }

    public ClientController getController(){ return this.controller; }

    @Override
    public void showUpdate() throws IOException {
        System.out.println("state model updated");
    }

    @Override
    public void reportError(String errorMessage) throws IOException {
        System.out.println("[ERROR]: " + errorMessage);
    }

    //----------------CALLBACKS FROM PLAYERS' ACTIONS-------------------------
    @Override
    public void updateGameCreated(int gameID, int numPlayers){
        controller.updateGameCreated(gameID, numPlayers);
    }

    @Override
    public void updateGameStarted(List<String> firstTurnOrder, Map<String, Integer> initialFood, ArrayList<Card> firstTopRow, ArrayList<Card> firstBottomRow, ArrayList<BuildingCard> buildingsTopRow, ArrayList<BuildingCard> buildingsBottomRow) {
        controller.updateGameStarted(firstTurnOrder, initialFood, firstTopRow, firstBottomRow, buildingsTopRow, buildingsBottomRow);
    }

    @Override
    public void updatePlayerConnected(String playerName) throws IOException {
        controller.updatePlayerConnected(playerName);
    }

    @Override
    public void successfullyJoinedGame(int gameID, int playerNum, ArrayList<String> playerNames, Map<String, Color> totemColors) throws IOException {
        controller.updateSuccessfullyJoinedGame(gameID, playerNum, playerNames, totemColors);
    }

    @Override
    public void updatePlayerLeftGame(String playerName) throws RemoteException {
        controller.updatePlayerLeftGame(playerName);
    }

    @Override
    public void updateNewHost() throws IOException {
        controller.updateNewHost();
    }

    @Override
    public void updateAvailableGames(Map<Integer, GamePlayers> availableGames) {
        controller.updateAvailableGames(availableGames);
    }

    @Override
    public void updateChosenTotemColor(String playerName, Color totemColor) {
        controller.updateTotemColor(playerName, totemColor);
    }

    @Override
    public void updateStartRound() {
        controller.updateStartRound();
    }

    @Override
    public void updateChosenTile(String playerName, int index) {
        controller.updateCurrentOfferTile(playerName, index);
    }

    @Override
    public void updateDrawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index) {
        controller.updateCardDrawn(fromTopRow, fromBuildings, index, playerName);
    }

    //----------------CALLBACKS FROM GAME STATE (SERVER) UPDATES-------------------------
    @Override
    public void updateFood(String playerName, int food) throws IOException {
        controller.updateFoodReserve(playerName, food);
    }

    @Override
    public void updateShamansStars(String playerName, int stars) throws IOException {
        controller.updateShamansStars(playerName, stars);
    }

    @Override
    public void updatePrestigePoints(String playerName, int points) throws IOException {
        controller.updatePrestigePoints(playerName, points);
    }

    @Override
    public void updateTopRow(ArrayList<Card> newTopRow) {
        controller.updateTopRow(newTopRow);
    }

    @Override
    public void updateTopBuildings(ArrayList<BuildingCard> newTopBuildings) {
        controller.updateTopBuildings(newTopBuildings);
    }

    @Override
    public void updateBottomRow(ArrayList<Card> newBottomRow) {
        controller.updateBottomRow(newBottomRow);
    }

    @Override
    public void updateBottomBuildings(ArrayList<BuildingCard> newBottomBuildings) {
        controller.updateBottomBuildings(newBottomBuildings);
    }

//    @Override
//    public void updateNextPlayer(String playerName) {
//        controller.updateCurrentPlayer(playerName);
//    }

    @Override
    public void updateGamePhase(GamePhase phase) {
        controller.updateGamePhase(phase);
    }

    @Override
    public void updateEra(int era) {
        controller.updateCurrentEra(era);
    }

    @Override
    public void forceQuit() {
        controller.handleServerDisconnection();
    }
}
