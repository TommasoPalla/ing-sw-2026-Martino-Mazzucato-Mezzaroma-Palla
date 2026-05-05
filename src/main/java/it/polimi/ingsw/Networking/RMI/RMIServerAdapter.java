package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Users.IllegalDrawException;
import it.polimi.ingsw.Model.Users.OccupiedTileException;
import it.polimi.ingsw.Networking.Configs.ServerConfigs;
import it.polimi.ingsw.Networking.Shared.ServerConnection;
import it.polimi.ingsw.Controller.ClientController;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServerAdapter implements ServerConnection {
    private VirtualRMIClient clientStub;
    private VirtualRMIServer serverStub;
    private final RMIClient client;

    private final String host;
    private final int port;

    public RMIServerAdapter(String host, int port, ClientController clientController) {
        this.host = host;
        this.port = port;
        this.client = new RMIClient(clientController);
    }

    @Override
    public void connect() {
        try {
            System.setProperty("java.rmi.server.hostname", ServerConfigs.DEFAULT_RMI_IP_ADDR);    //forces the server to use 127.0.0.1 as localhost
            Registry registry = LocateRegistry.getRegistry(host, port);
            serverStub = (VirtualRMIServer) registry.lookup(ServerConfigs.DEFAULT_RMI_SERVER_NAME);
            clientStub = (VirtualRMIClient) UnicastRemoteObject.exportObject(client, 0);
            serverStub.connect(clientStub);
        } catch (RemoteException e){
            System.out.println("Error during connection to RMI server\n" + e.getMessage());
        } catch (NotBoundException e){
            System.out.println("Error during RMI server lookup\n" + e.getMessage());
        }

    }

    @Override
    public void disconnect() {
        try{
            serverStub.disconnect(clientStub);
        } catch (RemoteException e){
            System.out.println("Error during server disconnection: " + e.getMessage());
        }
    }

    @Override
    public void setPlayerName(String playerName){

    }

    @Override
    public void createGame(String playerName, int numPlayers){

    }

    @Override
    public void joinGame(String playerName, int gameID){
        try{
            serverStub.joinGame(clientStub, playerName, gameID);
        } catch (RemoteException e){
            System.out.println("ERROR: remote error, could not join game " + gameID + "\n" + e.getMessage());
        }
    }

    @Override
    public void leaveGame(String playerName, int gameID){

    }

    @Override
    public void getActiveGames(){

    }

    @Override
    public void chooseOfferTile(int index) throws OccupiedTileException {

    }

    @Override
    public void drawCard(boolean fromTopRow, boolean fromBuildings, int index) throws IllegalDrawException {
        try {
            serverStub.drawCard(clientStub, fromTopRow, fromBuildings, index);
        } catch (RemoteException e) {
            System.out.println("ERROR: remote error, could not draw card" + e.getMessage());
        }

    }

    @Override
    public void chooseTotem(Color totemColor) {
        try{
            serverStub.chooseTotemColor(clientStub, totemColor);
        } catch (RemoteException e){
            System.out.println("ERROR: remote error, could not choose " + totemColor + "\n" + e.getMessage());
        }

    }

    @Override
    public void endTurn(String playerName){

    }
}
