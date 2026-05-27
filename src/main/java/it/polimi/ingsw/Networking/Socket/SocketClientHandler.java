package it.polimi.ingsw.Networking.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.CustomException.IllegalDrawException;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.CustomException.OccupiedTileException;
import it.polimi.ingsw.CustomException.UnavailableColorException;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Networking.Shared.ClientNotifier;
import it.polimi.ingsw.Networking.Shared.PlayerRecord;
import it.polimi.ingsw.Enums.SocketHeaderNames;
import it.polimi.ingsw.Utils.GsonFactory;
import it.polimi.ingsw.View.GamePlayers;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * This class is located server-side and accepts methods invocation requests
 * via JSON messages
 */

public class SocketClientHandler implements ClientNotifier, Runnable {

    private final Map<SocketHeaderNames, Consumer<Object[]>> commandHandlers = new HashMap<>();
    private final Gson gson = GsonFactory.create();
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
        commandHandlers.put(SocketHeaderNames.CREATE_GAME, parameters -> {
            String playerName = (String) parameters[0];
            int numPlayers = ((Double) parameters[1]).intValue();
            try {
                server.createGame(this, playerName, numPlayers);
            } catch (Exception e){
                //TODO: questa exception lanciata e' generica e non specifica -> da farne una specifica per questo caso
            }
        });
        commandHandlers.put(SocketHeaderNames.START_GAME, parameters -> {
            String requestingPlayerName = (String) parameters[0];
            int gameID = ((Double) parameters[1]).intValue();
            try {
                server.startGame(requestingPlayerName, gameID);
            } catch (Exception e) {}
        });
        commandHandlers.put(SocketHeaderNames.JOIN_GAME, parameters -> {
            String playerName = (String) parameters[0];
            int gameID = ((Double) parameters[1]).intValue();
            this.playerRecord = new PlayerRecord(gameID, playerName);
            try {
                server.joinGame(this);
            } catch (Exception e){
                //TODO: questa exception lanciata e' generica e non specifica -> da farne una specifica per questo caso
            }
        });
        commandHandlers.put(SocketHeaderNames.LEAVE_GAME, parameters -> {
            String playerName = (String) parameters[0];
            int gameID = ((Double) parameters[1]).intValue();
            PlayerRecord leavingPlayer = new PlayerRecord(gameID, playerName);
            try {
                server.leaveGame(leavingPlayer);
            } catch (Exception e) {}
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
            int index = ((Double) parameters[2]).intValue();
            try {
                server.drawCard(fromTopRow, fromBuildings, index, this);
            } catch (IllegalDrawException e){
                throw new IllegalDrawException();
            }
        });
        commandHandlers.put(SocketHeaderNames.CHOOSE_OFFER_TILE, parameters -> {
            try {
                int index = ((Double) parameters[0]).intValue();
                server.chooseOfferTile(index, this);
            }catch(OccupiedTileException e){
                throw new OccupiedTileException();
            }
        });
        commandHandlers.put(SocketHeaderNames.PING, parameters -> {
            server.ping(this);
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
    public void notifyGameCreated(int gameID, String playerName, int playerNum){
        this.playerRecord = new PlayerRecord(gameID, playerName);
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.GAME_CREATED, gameID, playerNum);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void notifyGameStarted(List<String> shuffledFirstPlayingOrder, Map<String, Integer> initialFood, ArrayList<Card> firstTopRow, ArrayList<Card> firstBottomRow, ArrayList<BuildingCard> buildingsTopRow, ArrayList<BuildingCard> buildingsBottomRow) {
        Type orderType = new TypeToken<List<String>>(){}.getType();
        Type foodType = new TypeToken<Map<String, Integer>>(){}.getType();
        Type cardListType = new TypeToken<ArrayList<Card>>(){}.getType();
        Type buildingListType = new TypeToken<ArrayList<BuildingCard>>(){}.getType();

        JsonElement orderJE = gson.toJsonTree(shuffledFirstPlayingOrder, orderType);
        JsonElement foodJE = gson.toJsonTree(initialFood, foodType);
        JsonElement topRowJE = gson.toJsonTree(firstTopRow, cardListType);
        JsonElement bottomRowJE = gson.toJsonTree(firstBottomRow, cardListType);
        JsonElement topBuildingsJE = gson.toJsonTree(buildingsTopRow, buildingListType);
        JsonElement bottomBuildingsJE = gson.toJsonTree(buildingsBottomRow, buildingListType);

        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.GAME_STARTED, orderJE, foodJE, topRowJE, bottomRowJE, topBuildingsJE, bottomBuildingsJE);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void notifyNewPlayerConnected(String playerName){
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.PLAYER_JOINED_GAME, playerName);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void notifySuccessfullyJoinedGame(int gameID, int playerNum, ArrayList<String> players, Map<String, Color> totemColors) {
        Type playersType = new TypeToken<ArrayList<String>>(){}.getType();
        Type totemMapType = new TypeToken<Map<String, Color>>(){}.getType();

        JsonElement playersJE = gson.toJsonTree(players, playersType);
        JsonElement totemJE = gson.toJsonTree(totemColors, totemMapType);

        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.SUCCESSFULLY_JOINED, gameID, playerNum, playersJE,  totemJE);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void notifyPlayerLeftGame(String playerName) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.LEFT_GAME, playerName);
        outStream.println(gson.toJson(message));

    }

    @Override
    public void notifyNewHost() {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.NEW_HOST);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void notifyAvailableGames(Map<Integer, GamePlayers> availableGames){
        Type type = new TypeToken<Map<Integer, GamePlayers>>(){}.getType();
        JsonElement serializedGames = gson.toJsonTree(availableGames, type);
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.GET_AVAILABLE_GAMES, serializedGames);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void notifyTotemColor(String playerName, Color totemColor) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.CHOSEN_TOTEM_COLOR, playerName, totemColor.toString());
        outStream.println(gson.toJson(message));
    }

    @Override
    public void notifyStartRound() {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.START_ROUND);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void notifyDrawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.DRAWN_CARD, playerName, fromTopRow, fromBuildings, index);
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
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.ADDED_SHAMAN_STARS, playerName, stars);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void notifyPrestigePointsToAdd(String playerName, int points) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.ADDED_PRESTIGE_POINTS, playerName, points);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void notifyTopRow(ArrayList<Card> newTopRow) {
        Type type = new TypeToken<ArrayList<Card>>(){}.getType();
        JsonElement serializedMessage = gson.toJsonTree(newTopRow, type);
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.UPDATED_TOP_ROW, serializedMessage);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void notifyTopBuildings(ArrayList<BuildingCard> newTopBuildings) {
        Type type = new TypeToken<ArrayList<BuildingCard>>(){}.getType();
        JsonElement serializedMessage = gson.toJsonTree(newTopBuildings, type);
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.UPDATED_TOP_BUILDINGS, serializedMessage);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void notifyBottomRow(ArrayList<Card> newBottomRow) {
        Type type = new TypeToken<ArrayList<Card>>(){}.getType();
        JsonElement serializedMessage = gson.toJsonTree(newBottomRow, type);
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.UPDATED_BOTTOM_ROW, serializedMessage);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void notifyBottomBuildings(ArrayList<BuildingCard> newBottomBuildings) {
        Type type = new TypeToken<ArrayList<BuildingCard>>(){}.getType();
        JsonElement serializedMessage = gson.toJsonTree(newBottomBuildings, type);
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.UPDATED_BOTTOM_BUILDINGS, serializedMessage);
        outStream.println(gson.toJson(message));
    }

    //    @Override
    //    public void notifyNextPlayer(String playerName) {
    //        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.NEXT_PLAYER, playerName);
    //        outStream.println(gson.toJson(message));
    //    }

    @Override
    public void notifyGamePhase(GamePhase newPhase) {
        JsonElement serializedPhase = gson.toJsonTree(newPhase, GamePhase.class);
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.CHANGED_GAME_PHASE, serializedPhase);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void notifyEra(int era) {
        JsonElement serializedEra = gson.toJsonTree(era);
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.CHANGED_ERA, serializedEra);
        outStream.println(gson.toJson(message));
    }


    @Override
    public void notifyForceQuit(String disconnectedPlayer) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.FORCE_QUIT, disconnectedPlayer);
        outStream.println(gson.toJson(message));
    }
}
