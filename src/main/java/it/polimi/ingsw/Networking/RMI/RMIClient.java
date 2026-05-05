package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Controller.ClientController;
import it.polimi.ingsw.CustomException.OccupiedTileException;
import it.polimi.ingsw.CustomException.UnavailableColorException;

import java.rmi.RemoteException;

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
    public void gameStarted(int gameID, int numPlayers) {
        controller.createLocalModel(gameID, numPlayers);
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
    public void chosenTile(String playerName, int index){
        try{
            controller.chooseOfferTile(index);
        }catch(OccupiedTileException e){
            throw new OccupiedTileException();
        }
    }

    @Override
    public void drawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index){
        controller.updateCardDrawn(fromTopRow, fromBuildings, index, playerName);
    }

    @Override
    public void playerJoinedGame(String playerName){
        controller.addPlayer(playerName);
    }

    //bozza di messaggio di update dal server
    public void updateMessage(int round){
        controller.updateCurrentRound(round);
    }

    @Override
    public void updateFood(String playerName, int food) throws RemoteException {

    }

    @Override
    public void updateShamansStars(String playerName, int stars) throws RemoteException {

    }

    @Override
    public void updatePrestigePoints(String playerName, int points) throws RemoteException {

    }
}
