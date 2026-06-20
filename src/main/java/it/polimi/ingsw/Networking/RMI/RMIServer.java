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
import it.polimi.ingsw.Networking.Shared.HeartBeat;
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
    private Registry registry;
    final ServerController serverController;
    final ArrayList<VirtualRMIClient> clients = new ArrayList<>();   //lista dei client connessi al server in generale

    /**
     * Maps every client to its record after joining a game
     */
    private Map<VirtualRMIClient, PlayerRecord> clientRecords = new ConcurrentHashMap<>();

    /**
     * Maps every client to its heartbeat
     */
    private final Map<VirtualRMIClient, HeartBeat> clientHeartBeats = new ConcurrentHashMap<>();

    public RMIServer(ServerController serverController){
        this.serverController = serverController;
    }

    public void startServer(){
        try{
            VirtualRMIServer serverStub = (VirtualRMIServer) UnicastRemoteObject.exportObject(this, 0);
            this.registry = LocateRegistry.createRegistry(ServerConfigs.DEFAULT_RMI_SERVER_PORT);
            this.registry.rebind(ServerConfigs.DEFAULT_RMI_SERVER_NAME, serverStub);
        } catch (RemoteException e){
            System.err.println("[RMI] ERROR: an error has occurred during server initialization\n" + e.getMessage());
        }
    }

    @Override
    public void connect(VirtualRMIClient clientStub) {
        this.clients.add(clientStub);

        HeartBeat heartBeat = new HeartBeat( () -> handleClientTimeout(clientStub));
        clientHeartBeats.put(clientStub, heartBeat);
        heartBeat.start();

        serverController.updateRMIClients(this.clients);
        serverController.notifyAvailableGames();
        System.out.println("[RMI] New client connected: " + clientStub);
    }

    @Override
    public void createGame(VirtualRMIClient client, String playerName, int numPlayers) throws RemoteException {
        ClientNotifier clientNotifier = new RMIClientNotifier(client);
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
    public void chooseOfferTile(VirtualRMIClient client, int index) throws OccupiedTileException {
        PlayerRecord callerRecord = clientRecords.get(client);
        try{
            serverController.chooseOfferTile(callerRecord, index);
        }catch(OccupiedTileException e){
            throw new OccupiedTileException();
        }
    }

    @Override
    public void drawCard(VirtualRMIClient client, boolean fromTopRow, boolean fromBuildings, int index) throws RemoteException {
        PlayerRecord callerRecord = clientRecords.get(client);
        serverController.drawCard(callerRecord, fromTopRow, fromBuildings, index);
    }

    @Override
    public void passTurn(VirtualRMIClient client) throws RemoteException {
        PlayerRecord callerRecord = clientRecords.get(client);
        serverController.passTurn(callerRecord);
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
    public void ping(VirtualRMIClient client) throws RemoteException {
        HeartBeat heartBeat = clientHeartBeats.get(client);
        if(heartBeat != null){
            heartBeat.receivedPing();
        }
    }

    private void handleClientTimeout(VirtualRMIClient clientStub){
        PlayerRecord record = clientRecords.get(clientStub);
        System.err.println("[RMI] Client timeout detected for: " + (record != null ? record.playerName() : clientStub));
        stopHeartBeat(clientStub);
        this.clients.remove(clientStub);
        clientRecords.remove(clientStub);
        if (record != null) {
            serverController.handleDisconnection(record);
        }
        serverController.updateRMIClients(this.clients);
    }

    private void stopHeartBeat(VirtualRMIClient clientStub){
        HeartBeat heartBeat = clientHeartBeats.get(clientStub);
        if(heartBeat != null)
            heartBeat.stop();
    }
}