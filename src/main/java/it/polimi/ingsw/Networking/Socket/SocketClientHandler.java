package it.polimi.ingsw.Networking.Socket;

import com.google.gson.Gson;
import it.polimi.ingsw.CustomException.IllegalDrawException;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.CustomException.OccupiedTileException;
import it.polimi.ingsw.CustomException.UnavailableColorException;
import it.polimi.ingsw.Networking.Shared.ClientNotifier;
import it.polimi.ingsw.Networking.Shared.PlayerRecord;
import it.polimi.ingsw.Enums.SocketHeaderNames;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This class is located server-side and accepts methods invocation requests
 * via JSON messages
 */

public class SocketClientHandler implements ClientNotifier, Runnable {

    private final Map<SocketHeaderNames, Consumer<Object[]>> commandHandlers = new HashMap<>();
    private final Gson gson = new Gson();
    private final BufferedReader inStream;
    private final PrintWriter outStream;
    private final VirtualSocketServer server;
    private PlayerRecord playerRecord;

    public SocketClientHandler(Socket socket, VirtualSocketServer server) throws IOException {
        this.inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.outStream = new PrintWriter(socket.getOutputStream(), true);
        this.server = server;
        initCommandHandler();
    }

    public PlayerRecord getPlayerRecord(){return playerRecord;}

    /**
     * Binding of possible method calls to lambda expressions that defines what those
     * method calls are supposed to do.
     * This uses a "command" design pattern coupled with a map from enums to lambda expressions
     */
    private void initCommandHandler(){
        commandHandlers.put(SocketHeaderNames.CONNECT_TO_GAME, parameters -> {
            String playerName = (String) parameters[0];
            int gameID = (int) parameters[1];
            this.playerRecord = new PlayerRecord(gameID, playerName);
            try {
                server.joinGame(this);
            } catch (Exception e){
                //TODO: questa exception lanciata e' generica e non specifica -> da farne una specifica per questo caso
            }
        });
        commandHandlers.put(SocketHeaderNames.CREATE_GAME, parameters -> {
            String playerName = (String) parameters[0];
            int numPlayers = (int) parameters[1];
            try {
                server.createGame(this, playerName, numPlayers);
            } catch (Exception e){
                //TODO: questa exception lanciata e' generica e non specifica -> da farne una specifica per questo caso
            }
        });
        commandHandlers.put(SocketHeaderNames.CHOOSE_TOTEM_COLOR, parameters -> {
            Color totemColor = Color.valueOf((String) parameters[0]) ;
            try {
                server.chooseTotemColor(totemColor, this);
            }catch(UnavailableColorException e){
                throw new UnavailableColorException(totemColor);
            }
        });
        commandHandlers.put(SocketHeaderNames.DRAW_CARD, parameters -> {
            boolean fromTopRow = (boolean) parameters[0];
            boolean fromBuildings = (boolean) parameters[1];
            int index = (int) parameters[2];
            try {
                server.drawCard(fromTopRow, fromBuildings, index, this);
            } catch (IllegalDrawException e){
                throw new IllegalDrawException();
            }
        });
        commandHandlers.put(SocketHeaderNames.CHOOSE_OFFER_TILE, parameters -> {
            try {
                int index = (int) parameters[0];
                server.chooseOfferTile(index, this);
            }catch(OccupiedTileException e){
                throw new OccupiedTileException();
            }
        });
    }

    /**
     * This run() method is the loop that accept messages from the client and
     * forwards them to the socket (TCP) server. It checks which function needs
     * to be called (written in the JSON message) and calls it with the parameters
     * contained in the same JSON message.
     */
    @Override
    public void run() {
        server.connect(this);
        try{
            String incomingMessage;
            while((incomingMessage = inStream.readLine()) != null){
                SocketMessageDTO socketDTO = gson.fromJson(incomingMessage, SocketMessageDTO.class);
                SocketHeaderNames socketHeader = socketDTO.getCommandName();

                Consumer<Object[]> handler = commandHandlers.get(socketHeader);
                if(handler != null){
                    handler.accept(socketDTO.getParameters());
                } else {
                    System.out.println("ERROR: " + socketHeader + " is not a valid command"); //chiaramente un placeholder, va messo qualcosa di meglio
                }
            }

        } catch (Exception e){
            System.out.println("ERROR: " + e.getMessage());
        }
}

    //CALLBACKS actions from clients
    @Override
    public void notifyDrawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.DRAWN_CARD, playerName, fromTopRow, fromBuildings, index);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void notifyTotemColor(String playerName, Color totemColor) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.CHOSEN_TOTEM_COLOR, playerName, totemColor.toString());
        outStream.println(gson.toJson(message));
    }

    @Override
    public void notifyNewPlayerConnected(String playerName){
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.CONNECTED_TO_GAME, playerName);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void notifyChosenTile(String playerName, int index) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.CHOSEN_OFFER_TILE, playerName, index);
        outStream.println(gson.toJson(message));
    }

    //CALLBACKS updates from server
    @Override
    public void notifyFoodToAdd(String playerName, int food) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.ADDED_FOOD, playerName, food);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void notifyShamansStarsToAdd(String playerName, int stars) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.ADDED_SHAMAN_STARS, stars);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void notifyPrestigePointsToAdd(String playerName, int points) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.ADDED_PRESTIGE_POINTS, points);
        outStream.println(gson.toJson(message));
    }

}
