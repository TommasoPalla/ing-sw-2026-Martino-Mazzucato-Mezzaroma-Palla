package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Users.Illegal_Draw_Exception;
import it.polimi.ingsw.Model.Users.Occupied_Tile_Exception;
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
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");    //forces the server to use 127.0.0.1 as localhost
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
    public void startGame(int numPlayers){

    }

    @Override
    public void joinGame(String gameId){

    }

    @Override
    public void getActiveGames(){

    }

    @Override
    public void chooseOfferTile(int index) throws Occupied_Tile_Exception {

    }

    @Override
    public void drawCardFromTop(int index) throws Illegal_Draw_Exception {

    }

    @Override
    public void drawCardFromBottom(int index) throws Illegal_Draw_Exception {

    }

    @Override
    public void chooseTotem(Color totemColor) {

    }
}
