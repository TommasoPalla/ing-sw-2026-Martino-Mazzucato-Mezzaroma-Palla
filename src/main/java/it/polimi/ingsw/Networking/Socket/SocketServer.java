package it.polimi.ingsw.Networking.Socket;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.CustomException.IllegalDrawException;
import it.polimi.ingsw.CustomException.OccupiedTileException;
import it.polimi.ingsw.CustomException.UnavailableColorException;
import it.polimi.ingsw.Networking.RMI.VirtualRMIClient;
import it.polimi.ingsw.Networking.Shared.ClientNotifier;
import it.polimi.ingsw.Networking.Shared.HeartBeat;
import it.polimi.ingsw.Networking.Shared.PlayerRecord;
import it.polimi.ingsw.Networking.Shared.ServerController;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SocketServer implements VirtualSocketServer{
    private final ArrayList<SocketClientHandler> clients = new ArrayList<>();
    private final ServerController serverController;

    public SocketServer(ServerController serverController){this.serverController = serverController;}

    private final Map<SocketClientHandler, HeartBeat> clientHeartBeats = new ConcurrentHashMap<>();

    //getters
    public ArrayList<SocketClientHandler> getClients(){
        return clients;
    }

    public void startServer(int port){
        new Thread( () -> {
            try (ServerSocket serverSocket = new ServerSocket(port)) {
                while (true) {
                    Socket socket = serverSocket.accept();
                    SocketClientHandler handler = new SocketClientHandler(socket, this);
                    new Thread(handler).start();
                }
            } catch (IOException e) {
                System.out.println("Error during TCP server initialization\n" + e.getMessage());
            }
        }).start();

    }

    @Override
    public void connect(SocketClientHandler handler) {
        this.clients.add(handler);

        HeartBeat heartBeat = new HeartBeat( () -> handleClientTimeout(handler));
        clientHeartBeats.put(handler, heartBeat);
        heartBeat.start();

        System.out.println("[TCP] New client connected: " + handler);
        serverController.updateSocketClients(this.clients);
        serverController.notifyAvailableGames();
    }

    @Override
    public void disconnect(SocketClientHandler handler) {
        this.clients.remove(handler);
        serverController.updateSocketClients(this.clients);
        serverController.notifyAvailableGames();
        System.out.println("[TCP] Client disconnected " + handler);
    }

    @Override
    public void chooseOfferTile(int index, SocketClientHandler clientHandler) throws OccupiedTileException {
        serverController.chooseOfferTile(clientHandler.getPlayerRecord(), index);
    }

    @Override
    public void drawCard(boolean fromTopRow, boolean fromBuildings, int index, SocketClientHandler clientHandler) throws IllegalDrawException {
        serverController.drawCard(clientHandler.getPlayerRecord(), fromTopRow, fromBuildings, index);
    }

    @Override
    public void chooseTotemColor(Color totemColor, SocketClientHandler handler) {
        try {
            serverController.chooseTotemColor(handler.getPlayerRecord(), totemColor);
        }catch(UnavailableColorException e){
            throw new UnavailableColorException(totemColor);
        }
    }

    @Override
    public void createGame(ClientNotifier notifier, String playerName, int numPlayers) {
        serverController.createNewGame(notifier, playerName, numPlayers);
    }

    @Override
    public void joinGame(SocketClientHandler handler){
        PlayerRecord record = handler.getPlayerRecord();
        this.clients.add(handler);
        serverController.joinGame(handler, record);
    }

    @Override
    public void startGame(String requestingPlayerName, int gameID) {
        serverController.startGame(requestingPlayerName, gameID);
    }

    @Override
    public void leaveGame(PlayerRecord leavingPlayer) {
        serverController.leaveGame(leavingPlayer);
    }

    @Override
    public void ping(SocketClientHandler client) {
        HeartBeat heartBeat = clientHeartBeats.get(client);
        if(heartBeat != null){
            heartBeat.receivedPing();
        }
    }

    private void handleClientTimeout(SocketClientHandler socketClient){
        String playerName = (socketClient.getPlayerRecord() != null) ? socketClient.getPlayerRecord().playerName() : "Unknown";
        System.err.println("[TCP] Client timeout detected for: " + playerName);
        stopHeartBeat(socketClient);
        this.clients.remove(socketClient);
        PlayerRecord record = socketClient.getPlayerRecord();
        if (record != null) {
            serverController.handleDisconnection(record);
        }
        serverController.updateSocketClients(this.clients);
    }

    private void stopHeartBeat(SocketClientHandler socketClient){
        HeartBeat heartBeat = clientHeartBeats.get(socketClient);
        if(heartBeat != null)
            heartBeat.stop();
    }

}
