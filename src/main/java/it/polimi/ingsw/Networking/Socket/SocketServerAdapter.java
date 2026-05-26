package it.polimi.ingsw.Networking.Socket;

import com.google.gson.Gson;
import it.polimi.ingsw.Controller.ClientController.ClientController;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.SocketHeaderNames;
import it.polimi.ingsw.CustomException.IllegalDrawException;
import it.polimi.ingsw.CustomException.OccupiedTileException;
import it.polimi.ingsw.Networking.Configs.ServerConfigs;
import it.polimi.ingsw.Networking.Shared.ServerConnection;
import it.polimi.ingsw.Utils.GsonFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SocketServerAdapter implements ServerConnection {

    private final Gson gson = GsonFactory.create();

    private Socket socket;
    private PrintWriter outStream;
    private Thread listenerThread;
    private final SocketClient client;

    private final int port;
    private final String host;

    private ScheduledExecutorService heartbeatScheduler;

    public SocketServerAdapter(String host, int port, ClientController clientController) {
        this.host = host;
        this.port = port;
        this.client = new SocketClient(clientController);
    }

    //Lobby methods
    @Override
    public void connect() {
        try{
            socket = new Socket(host, port);
            outStream = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            SocketServerHandler handler = new SocketServerHandler(inStream, client);
            listenerThread = new Thread(handler);
            listenerThread.setDaemon(true);
            listenerThread.start();

            startHeartBeat();
            System.out.println("Connected to TCP server");
        } catch (Exception e){
            System.out.println("Error during connection to TCP server: " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        try {
            socket.close();
            listenerThread.interrupt();
        } catch (IOException e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }
    }


    @Override
    public void leaveGame(String playerName, int gameID){
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.LEAVE_GAME, playerName, gameID);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void createGame(String playerName, int numPlayers){
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.CREATE_GAME, playerName, numPlayers);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void startGame(String playerName, int gameID) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.START_GAME,  playerName, gameID);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void joinGame(String playerName, int gameID){
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.JOIN_GAME, playerName, gameID);
        outStream.println(gson.toJson(message));
    }

    //Client direct actions
    @Override
    public void chooseOfferTile(int index) throws OccupiedTileException {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.CHOOSE_OFFER_TILE, index);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void drawCard(boolean fromTopRow, boolean fromBuildings, int index) throws IllegalDrawException {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.DRAW_CARD, fromTopRow, fromBuildings, index);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void chooseTotem(Color totemColor) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.CHOOSE_TOTEM_COLOR, totemColor);
        outStream.println(gson.toJson(message));
    }

    @Override
    //serve davvero? di base non si passa il turno ma si fanno per forza tutte le azioni possibili
    //e poi il turno viene passato automaticamente
    public void endTurn(String playerName){
    }

    private void startHeartBeat() {
        heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();
        heartbeatScheduler.scheduleAtFixedRate(() -> {
            try {
                SocketMessageDTO ping = new SocketMessageDTO(SocketHeaderNames.PING);
                outStream.println(gson.toJson(ping));
            } catch (Exception e) {
                stopHeartbeat();
                client.getController().handleServerDisconnection();
            }
        }, 0, ServerConfigs.DEFAULT_PING_INTERVAL, TimeUnit.SECONDS);
    }

    private void stopHeartbeat() {
        if (heartbeatScheduler != null) {
            heartbeatScheduler.shutdownNow();
        }
    }
}
