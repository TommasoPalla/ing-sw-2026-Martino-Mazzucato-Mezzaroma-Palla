package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.CustomException.UIException.NotEnoughPlayersException;
import it.polimi.ingsw.CustomException.UIException.NotJoinableGameException;
import it.polimi.ingsw.CustomException.UIException.NotTheHostException;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.CustomException.IllegalDrawException;
import it.polimi.ingsw.CustomException.OccupiedTileException;
import it.polimi.ingsw.CustomException.UnavailableColorException;
import it.polimi.ingsw.Networking.Configs.ServerConfigs;
import it.polimi.ingsw.Networking.Shared.ClientNotifier;
import it.polimi.ingsw.Networking.Shared.PlayerRecord;
import it.polimi.ingsw.Networking.Shared.ServerController;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class RMIServer implements VirtualRMIServer {
    final ServerController serverController;
    final ArrayList<VirtualRMIClient> clients = new ArrayList<>();   //lista dei client connessi al server in generale

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
        serverController.updateRMIClients(this.clients);
        serverController.notifyAvailableGames();
    }

    @Override
    public void createGame(VirtualRMIClient client, String playerName, int numPlayers) throws RemoteException {
        ClientNotifier clientNotifier = new RMIClientNotifier(client);
        //da fare il clientRecord.put() capendo come prendere il game id
        int gameID = serverController.createNewGame(clientNotifier, playerName, numPlayers); //QUI viene creato il game e assegnatogli il gameID
        PlayerRecord playerRecord = new PlayerRecord(gameID, playerName);
        clientRecords.put(client, playerRecord);
    }

    @Override
    public void joinGame(VirtualRMIClient client, String playerName, int gameID) throws RemoteException {
        try {
            PlayerRecord playerRecord = new PlayerRecord(gameID, playerName);
            clientRecords.put(client, playerRecord);
            ClientNotifier clientNotifier = new RMIClientNotifier(client);
            serverController.joinGame(clientNotifier, playerRecord);
            //System.out.println(playerRecord + "added to game");
        } catch (NotJoinableGameException e) {
            throw new NotJoinableGameException(e.getMessage());
        }
    }

    @Override
    public void leaveGame(String playerName, int gameID) throws RemoteException {
        PlayerRecord playerRecord = new PlayerRecord(gameID, playerName);
        serverController.leaveGame(playerRecord);
    }

    @Override
    public void startGame(String playerName, int gameID) throws NotTheHostException, NotEnoughPlayersException, RemoteException {
        try {
            serverController.startGame(playerName, gameID);
        } catch (NotTheHostException e) {
            throw new NotTheHostException(e.getMessage());
        } catch (NotEnoughPlayersException e) {
            throw new NotEnoughPlayersException();
        }
    }

    @Override
    public void disconnect(VirtualRMIClient clientStub) {
        this.clients.remove(clientStub);
        serverController.removeClientFromGame(clientRecords.get(clientStub));
        //serverController.removeNotifierFromGame(clientRecords.get(clientStub));
        System.out.println(clientRecords.get(clientStub) + "removed from RMI server");
        //in realtà in questo caso forse il controller potrebbe capirlo internamente ma è più complicato
        serverController.updateRMIClients(this.clients);
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
        try{
            PlayerRecord callerRecord = clientRecords.get(client);
            serverController.chooseTotemColor(callerRecord, totemColor);
        }catch(UnavailableColorException e){
            throw new UnavailableColorException(totemColor);
        }
    }

    @Override
    public void chooseOfferTile(VirtualRMIClient client, PlayerRecord playerRecord, int index) throws RemoteException {
        try{
            PlayerRecord callerRecord = clientRecords.get(client);
            serverController.chooseOfferTile(playerRecord, index);
        }catch(OccupiedTileException e){
            throw new OccupiedTileException();
        }
    }

    public ArrayList<VirtualRMIClient> getClients(){
        return clients;
    }

}