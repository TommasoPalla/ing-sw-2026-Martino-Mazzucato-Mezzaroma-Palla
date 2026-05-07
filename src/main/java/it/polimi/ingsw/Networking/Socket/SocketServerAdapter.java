package it.polimi.ingsw.Networking.Socket;

import com.google.gson.Gson;
import it.polimi.ingsw.Controller.ClientController.ClientController;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.SocketHeaderNames;
import it.polimi.ingsw.CustomException.IllegalDrawException;
import it.polimi.ingsw.CustomException.OccupiedTileException;
import it.polimi.ingsw.Networking.Shared.ServerConnection;
import it.polimi.ingsw.View.GamePlayers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Map;

public class SocketServerAdapter implements ServerConnection {

    private Gson gson = new Gson();

    private Socket socket;
    private PrintWriter outStream;
    private Thread listenerThread;
    private SocketClient client;

    private final int port;
    private final String host;

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

            //client = new SocketClient();
            SocketServerHandler handler = new SocketServerHandler(inStream, client);
            listenerThread = new Thread(handler);
            listenerThread.setDaemon(true);
            listenerThread.start();

            System.out.println("Connected to TCP server");

        } catch (Exception e){
            System.out.println("Error during connection to TCP server: " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        //qui si invia un messaggio che dice al server che si vuole chiudere la connessione
        try {
            socket.close();
            listenerThread.interrupt();
        } catch (IOException e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }
    }

    @Override
    public void setPlayerName(String playerName){

    }

    @Override
    public Map<Integer, GamePlayers> getActiveGames() {
        return null;
    }

    @Override
    public void leaveGame(String playerName, int gameID){

    }

    @Override
    public void createGame(String playerName, int numPlayers){
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.CREATE_GAME, playerName, numPlayers);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void joinGame(String playerName, int gameID){
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.CONNECT_TO_GAME, playerName, gameID);
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
    public void endTurn(String playerName){

    }

    //Server updates coming from actions of other players, events, drawing from deck etc

}
