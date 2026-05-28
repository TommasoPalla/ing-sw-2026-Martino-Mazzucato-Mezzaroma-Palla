package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.CustomException.StubException;
import it.polimi.ingsw.CustomException.UIException.NotEnoughPlayersException;
import it.polimi.ingsw.CustomException.UIException.NotJoinableGameException;
import it.polimi.ingsw.CustomException.UIException.NotTheHostException;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.CustomException.IllegalDrawException;
import it.polimi.ingsw.CustomException.OccupiedTileException;
import it.polimi.ingsw.CustomException.UnavailableColorException;
import it.polimi.ingsw.Networking.Configs.ServerConfigs;
import it.polimi.ingsw.Networking.Shared.ServerConnection;
import it.polimi.ingsw.Controller.ClientController.ClientController;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Client-side adapter that implements {@link ServerConnection} using the RMI protocol.
 * It manages the lookup of the server stub and provides a local implementation
 * for game actions by forwarding them to the RMI server.
 */
public class RMIServerAdapter implements ServerConnection {
    private VirtualRMIClient clientStub;
    private VirtualRMIServer serverStub;
    private final RMIClient client;

    private final String host;
    private final int port;

    private ScheduledExecutorService heartbeatScheduler;

    /**
     * Constructor for the RMI adapter.
     * @param host the IP address or hostname of the server.
     * @param port the port where the RMI registry is located.
     * @param clientController the local controller to be notified of server updates.
     */
    public RMIServerAdapter(String host, int port, ClientController clientController) {
        this.host = host;
        this.port = port;
        this.client = new RMIClient(clientController);
    }

    @Override
    public void connect() {
        try {
            String clientIp = "127.0.0.1";
            try (java.net.DatagramSocket socket = new java.net.DatagramSocket()) {
                socket.connect(java.net.InetAddress.getByName("8.8.8.8"), 10002);
                clientIp = socket.getLocalAddress().getHostAddress();
            } catch (Exception e) {}

            System.setProperty("java.rmi.server.hostname", clientIp);
            Registry registry = LocateRegistry.getRegistry(host, port);
            serverStub = (VirtualRMIServer) registry.lookup(ServerConfigs.DEFAULT_RMI_SERVER_NAME);
            clientStub = (VirtualRMIClient) UnicastRemoteObject.exportObject(client, 0);
            serverStub.connect(clientStub);

            startHeartBeat();
        } catch (RemoteException e){
            System.err.println("Error during connection to RMI server\n" + e.getMessage());
        } catch (NotBoundException e){
            System.err.println("Error during RMI server lookup\n" + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        stopHeartbeat();
        try{
            serverStub.disconnect(clientStub);
        } catch (RemoteException e){
            System.out.println("Error during server disconnection: " + e.getMessage());
        }
    }

    @Override
    public void createGame(String playerName, int numPlayers){
        try {
            serverStub.createGame(clientStub, playerName, numPlayers);
        } catch (RemoteException e) {
            System.out.println("Error during creating game " + e.getMessage());
        }
    }

    @Override
    public void joinGame(String playerName, int gameID){
        try{
            serverStub.joinGame(clientStub, playerName, gameID);
        } catch (RemoteException e){
            System.out.println("ERROR: remote error, could not join game " + gameID + "\n" + e.getMessage());
        } catch (NotJoinableGameException e) {
            throw new NotJoinableGameException(e.getMessage());
        }
    }

    @Override
    public void startGame(String playerName, int gameID) {
        try{
            serverStub.startGame(playerName, gameID);
        } catch (RemoteException e){
            System.out.println("ERROR: remote error, could not start the game" + gameID + "\n" + e.getMessage());
        }
        catch (NotTheHostException e){
            throw new NotTheHostException(e.getMessage());
        }
        catch (NotEnoughPlayersException e){
            throw new NotEnoughPlayersException();
        }
    }

    @Override
    public void leaveGame(String playerName, int gameID){
        try {
            serverStub.leaveGame(playerName, gameID);
        } catch(RemoteException e){
            throw new StubException("could not leave game.");
        }
    }

    @Override
    public void chooseOfferTile(int index) throws OccupiedTileException {
        try {
            serverStub.chooseOfferTile(clientStub, index);
        }catch (RemoteException e){
            System.out.println("ERROR: remote error, could not choose tile"  + e.getMessage());
        }
        catch (OccupiedTileException e){
            throw new OccupiedTileException();
        }
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
    public void passTurn() {
        try {
            serverStub.passTurn(clientStub);
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
        } catch (UnavailableColorException e1){
            throw new UnavailableColorException(totemColor);
        }

    }

    @Override
    public void endTurn(String playerName){

    }

    private void startHeartBeat() {
        heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();
        heartbeatScheduler.scheduleAtFixedRate(() -> {
            try {
                serverStub.ping(clientStub);
            } catch (RemoteException e) {
                // server irraggiungibile
                stopHeartbeat();
                client.getController().handleServerDisconnection(client.getController().getPlayerName());
            }
        }, 0, ServerConfigs.DEFAULT_PING_INTERVAL, TimeUnit.SECONDS);
    }

    private void stopHeartbeat() {
        if (heartbeatScheduler != null) {
            heartbeatScheduler.shutdownNow();
        }
    }
}