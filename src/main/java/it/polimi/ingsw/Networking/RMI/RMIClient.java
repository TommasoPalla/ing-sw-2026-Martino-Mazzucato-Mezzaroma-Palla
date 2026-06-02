package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Controller.ClientController.ClientController;
import it.polimi.ingsw.CustomException.OccupiedTileException;
import it.polimi.ingsw.CustomException.UnavailableColorException;
import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.EventManagement.PlayerEventResults;
import it.polimi.ingsw.View.GamePlayers;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RMIClient implements VirtualRMIClient {

    private final ClientController controller;
    public RMIClient(ClientController controller) {
        this.controller = controller;
    }

    public ClientController getController(){ return this.controller;}

    public void run() throws RemoteException{}
    public void runCli() throws RemoteException{}
    public void runGUI() throws RemoteException{}

    @Override
    //verosimilmente si dovra' passare per parametro il model e poi chiamare System.out.println(model.toString()) o simile
    public void showUpdate() throws RemoteException {
        // call to client controller update methods
        System.out.println("state model updated");
    }

    @Override
    public void reportError(String errorMessage) throws RemoteException {
        System.out.println(errorMessage);
    }

    //CALLBACKS from players' actions
    @Override
    public synchronized void updateGameCreated(int gameID, int numPlayers){
        controller.updateGameCreated(gameID, numPlayers);
    }

    @Override
    public synchronized void updateGameStarted(List<String> firstTurnOrder, Map<String,Integer> initialFood, ArrayList<Card> firstTopRow, ArrayList<Card> firstBottomRow, ArrayList<BuildingCard> buildingsTopRow, ArrayList<BuildingCard> buildingsBottomRow) {
        controller.updateGameStarted(firstTurnOrder, initialFood, firstTopRow, firstBottomRow, buildingsTopRow, buildingsBottomRow);
    }

    @Override
    public synchronized void playerJoinedGame(String playerName) throws RemoteException {
        controller.updatePlayerConnected(playerName);
    }

    @Override
    public synchronized void successfullyJoinedGame(int gameID, int numPlayers, ArrayList<String> players, Map<String,Color> totemColors) throws RemoteException {
        controller.updateSuccessfullyJoinedGame(gameID, numPlayers, players, totemColors);
    }

    @Override
    public synchronized void playerLeftGame(String playerName) throws RemoteException {
        controller.updatePlayerLeftGame(playerName);
    }

    @Override
    public synchronized void updateNewHost() throws RemoteException {
        controller.updateNewHost();
    }

    @Override
    public synchronized void updateAvailableGames(Map<Integer, GamePlayers> availableGames) throws RemoteException {
        controller.updateAvailableGames(availableGames);
    }

    @Override
    public synchronized void startRound(Map<EventType, ArrayList<PlayerEventResults>> lastEventsResults) throws RemoteException {
        controller.updateStartRound(lastEventsResults);
    }

    @Override
    public synchronized void chosenTotem(String playerName, Color totemColor){
        try{
            controller.updateTotemColor(playerName, totemColor);
        }catch(UnavailableColorException e){
            throw new UnavailableColorException(totemColor);
        }
    }

    @Override
    public synchronized void drawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index){
        controller.updateCardDrawn(fromTopRow, fromBuildings, index, playerName);
    }

    @Override
    public synchronized void turnPassed(String playerName) throws RemoteException {
        controller.updateTurnPassed(playerName);
    }

    @Override
    public synchronized void chosenTile(String playerName, int index){
        try{
            controller.updateCurrentOfferTile(playerName, index);
        }catch(OccupiedTileException e){
            throw new OccupiedTileException();
        }
    }

    //Callbacks from game state (server) updates

    @Override
    public synchronized void updateFood(String playerName, int food) throws RemoteException {
        controller.updateFoodReserve(playerName, food);
    }

    @Override
    public synchronized void updateShamansStars(String playerName, int stars) throws RemoteException {
        controller.updateShamansStars(playerName, stars);
    }

    @Override
    public synchronized void updatePrestigePoints(String playerName, int points) throws RemoteException {
        controller.updatePrestigePoints(playerName, points);
    }

    @Override
    public synchronized void updateBuildersDiscount(String playerName, int discount) throws RemoteException {
        controller.updateBuildersDiscount(playerName, discount);
    }

    @Override
    public synchronized void updateGatherersDiscount(String playerName, int discount) throws RemoteException {
        controller.updateGatherersDiscount(playerName, discount);
    }

    @Override
    public synchronized void updateTopRow(ArrayList<Card> newTopRow) throws RemoteException {
        controller.updateTopRow(newTopRow);
    }

    @Override
    public synchronized void updateTopBuildings(ArrayList<BuildingCard> newTopBuildings) throws RemoteException {
        controller.updateTopBuildings(newTopBuildings);
    }

    @Override
    public synchronized void updateBottomRow(ArrayList<Card> newBottomRow) throws RemoteException {
        controller.updateBottomRow(newBottomRow);
    }

    @Override
    public synchronized void updateBottomBuildings(ArrayList<BuildingCard> newBottomBuildings) throws RemoteException {
        controller.updateBottomBuildings(newBottomBuildings);
    }

//    @Override
//    public void updateNextPlayer(String playerName) throws RemoteException {
//        controller.updateCurrentPlayer(playerName);
//    }

    @Override
    public synchronized void updateGamePhase(GamePhase phase) throws RemoteException {
        controller.updateGamePhase(phase);
    }

    @Override
    public synchronized void updateEra(int era) throws RemoteException {
        controller.updateCurrentEra(era);
    }

    @Override
    public synchronized void updateEndGame(Map<String, Integer> finalRanking) throws RemoteException {
        controller.updateEndGame(finalRanking);
    }

    @Override
    public void updateLeaderboardInfo(List<String> leaderboard, int playerPosition) throws RemoteException {
        controller.updateLeaderboardInfo(leaderboard, playerPosition);
    }

    @Override
    public synchronized void forceQuit(String disconnectedPlayer) {
        controller.handleServerDisconnection(disconnectedPlayer);
    }
}
