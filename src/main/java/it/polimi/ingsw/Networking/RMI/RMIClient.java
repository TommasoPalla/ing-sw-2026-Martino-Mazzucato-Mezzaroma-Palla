package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Controller.ClientController.ClientController;
import it.polimi.ingsw.CustomException.OccupiedTileException;
import it.polimi.ingsw.CustomException.UnavailableColorException;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.View.GamePlayers;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;

public class RMIClient implements VirtualRMIClient {

    private final ClientController controller;
    public RMIClient(ClientController controller) {
        this.controller = controller;
    }

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

    @Override
    public void gameStarted(int id, int numPlayers){}

    //CALLBACKS from players' actions
    @Override
    public void updateGameCreated(int gameID, int numPlayers){
        controller.updateGameCreated(gameID, numPlayers);
    }

    @Override
    public void updateGameStarted(int gameID, int numPlayers) {
        //controller.updateGameStarted(gameID);
    }

    @Override
    public void playerJoinedGame(String playerName) throws RemoteException {
        controller.updatePlayerConnected(playerName);
    }

    @Override
    public void successfullyJoinedGame(int gameID, int numPlayers, ArrayList<String> players) throws RemoteException {
        controller.updateSuccessfullyJoinedGame(gameID, numPlayers, players);
    }

    @Override
    public void playerLeftGame(String playerName) throws RemoteException {
        controller.updatePlayerLeftGame(playerName);
    }

    @Override
    public void updateAvailableGames(Map<Integer, GamePlayers> availableGames) throws RemoteException {
        controller.updateAvailableGames(availableGames);
    }

    @Override
    public void chosenTotem(String playerName, Color totemColor){
        try{
            controller.chooseTotem(totemColor);
        }catch(UnavailableColorException e){
            throw new UnavailableColorException(totemColor);
        }
    }

    @Override
    public void drawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index){
        controller.updateCardDrawn(fromTopRow, fromBuildings, index, playerName);
    }

    @Override
    public void chosenTile(String playerName, int index){
        try{
            controller.chooseOfferTile(index);
        }catch(OccupiedTileException e){
            throw new OccupiedTileException();
        }
    }

    //Callbacks from game state (server) updates

    @Override
    public void updateFood(String playerName, int food) throws RemoteException {
        controller.updateFoodReserve(playerName, food);
    }

    @Override
    public void updateShamansStars(String playerName, int stars) throws RemoteException {
        controller.updateShamansStars(playerName, stars);
    }

    @Override
    public void updatePrestigePoints(String playerName, int points) throws RemoteException {
        controller.updatePrestigePoints(playerName, points);
    }

    @Override
    public void updateTopRow(ArrayList<Card> newTopRow) throws RemoteException {
        controller.updateTopRow(newTopRow);
    }

    @Override
    public void updateTopBuildings(ArrayList<BuildingCard> newTopBuildings) throws RemoteException {
        controller.updateTopBuildings(newTopBuildings);
    }

    @Override
    public void updateBottomRow(ArrayList<Card> newBottomRow) throws RemoteException {
        controller.updateBottomRow(newBottomRow);
    }

    @Override
    public void updateBottomBuildings(ArrayList<BuildingCard> newBottomBuildings) throws RemoteException {
        controller.updateBottomBuildings(newBottomBuildings);
    }

    @Override
    public void updateNextPlayer(String playerName) throws RemoteException {
        controller.updateCurrentPlayer(playerName);
    }

    @Override
    public void updateGamePhase(GamePhase phase) throws RemoteException {
        controller.updateGamePhase(phase);
    }

    @Override
    public void updateEra(int era) throws RemoteException {
        controller.updateCurrentEra(era);
    }
}
