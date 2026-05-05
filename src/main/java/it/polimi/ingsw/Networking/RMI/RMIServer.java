package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Users.IllegalDrawException;
import it.polimi.ingsw.Model.Users.OccupiedTileException;
import it.polimi.ingsw.Networking.Configs.ServerConfigs;
import it.polimi.ingsw.Networking.Shared.PlayerRecord;
import it.polimi.ingsw.Networking.Shared.ServerController;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RMIServer implements VirtualRMIServer {
    final ServerController serverController;
    final List<VirtualRMIClient> clients = new ArrayList<>();   //lista dei client connessi al server in generale

    //Mappa che associa ad ogni client il proprio player record (nome e gameID) DOPO che ha joinato un game
    private Map<VirtualRMIClient, PlayerRecord> clientRecords = new ConcurrentHashMap<>();

    public RMIServer(ServerController serverController){
        this.serverController = serverController;
    }

    public void startServer(){
        try{
            VirtualRMIServer serverStub = (VirtualRMIServer) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.createRegistry(ServerConfigs.DEFAULT_RMI_SERVER_PORT);
            registry.rebind(ServerConfigs.DEFAULT_RMI_SERVER_NAME, serverStub);
        } catch (RemoteException e){
            System.out.println("Error during RMI server initialization\n" + e.getMessage());
        }
    }

    @Override
    public void connect(VirtualRMIClient clientStub) {
        this.clients.add(clientStub);
        System.out.println(clientStub + "added to RMI server");
    }

    @Override
    public void createGame(String playerName, int numPlayers) throws RemoteException {

    }

    @Override
    public void joinGame(VirtualRMIClient client, String playerName, int gameID) throws RemoteException {
        PlayerRecord playerRecord = new PlayerRecord(gameID, playerName);
        clientRecords.put(client, playerRecord);
        serverController.addPlayerToGame(playerRecord);
        serverController.addNotifierToGame(playerRecord, new RMIClientNotifier(client));
        System.out.println(playerRecord + "added to game");
    }

    @Override
    public void disconnect(VirtualRMIClient clientStub) {
        this.clients.remove(clientStub);
        serverController.removePlayerFromGame(clientRecords.get(clientStub));
        serverController.removeNotifierFromGame(clientRecords.get(clientStub));
        System.out.println(clientRecords.get(clientStub) + "removed from RMI server");
    }

    @Override
    public void chooseOfferTile(VirtualRMIClient client, int index) throws OccupiedTileException {

    }

    @Override
    public void drawCard(VirtualRMIClient client, boolean fromTopRow, boolean fromBuildings, int index) throws IllegalDrawException, RemoteException {
        PlayerRecord callerRecord = clientRecords.get(client);
        serverController.drawCard(callerRecord, fromTopRow, fromBuildings, index);
    }

    @Override
    public void chooseTotemColor(VirtualRMIClient client, Color totemColor) throws RemoteException {
        PlayerRecord callerRecord = clientRecords.get(client);
        serverController.chooseTotemColor(callerRecord, totemColor);
    }
}
