package it.polimi.ingsw.Networking.Socket;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.CustomException.IllegalDrawException;
import it.polimi.ingsw.CustomException.StubException;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.CustomException.OccupiedTileException;
import it.polimi.ingsw.CustomException.UnavailableColorException;
import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.EventManagement.PlayerEventResults;
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
            server.createGame(this, playerName, numPlayers);
        });
        commandHandlers.put(SocketHeaderNames.START_GAME, parameters -> {
            String requestingPlayerName = (String) parameters[0];
            int gameID = ((Double) parameters[1]).intValue();
            server.startGame(requestingPlayerName, gameID);
        });
        commandHandlers.put(SocketHeaderNames.JOIN_GAME, parameters -> {
            String playerName = (String) parameters[0];
            int gameID = ((Double) parameters[1]).intValue();
            this.playerRecord = new PlayerRecord(gameID, playerName);
            server.joinGame(this);
        });
        commandHandlers.put(SocketHeaderNames.LEAVE_GAME, parameters -> {
            String playerName = (String) parameters[0];
            int gameID = ((Double) parameters[1]).intValue();
            PlayerRecord leavingPlayer = new PlayerRecord(gameID, playerName);
            server.leaveGame(leavingPlayer);
        });
        commandHandlers.put(SocketHeaderNames.CHOOSE_TOTEM_COLOR, parameters -> {
            Color totemColor = Color.valueOf((String) parameters[0]) ;
            try {
                server.chooseTotemColor(totemColor, this);
            }catch(UnavailableColorException e){
                throw new UnavailableColorException(totemColor);
            }
        });
        commandHandlers.put(SocketHeaderNames.PASS_TURN, parameters -> {
            server.passTurn(this);
        });
        commandHandlers.put(SocketHeaderNames.DRAW_CARD, parameters -> {
            boolean fromTopRow = (boolean) parameters[0];
            boolean fromBuildings = (boolean) parameters[1];
            int index = ((Double) parameters[2]).intValue();
            try {
                server.drawCard(fromTopRow, fromBuildings, index, this);
            } catch (IllegalDrawException e){
                throw new IllegalDrawException(e.getMessage());
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
        String incomingMessage;
        try {
            while ((incomingMessage = inStream.readLine()) != null) {
                try {
                    SocketMessageDTO socketDTO = gson.fromJson(incomingMessage, SocketMessageDTO.class);
                    SocketHeaderNames socketHeader = socketDTO.getCommandName();

                    Consumer<Object[]> handler = commandHandlers.get(socketHeader);
                    if (handler != null) {
                        handler.accept(socketDTO.getParameters());
                    } else {
                        System.err.println("ERROR: " + socketHeader + " is not a valid command");
                    }
                } catch (Exception e) {
                    System.err.println("ERROR during message processing: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("TCP Connection lost for handler " + this + ": " + e.getMessage());
        }
    }

    private void sendMessage(SocketMessageDTO message){
        try {
            outStream.println(gson.toJson(message));
        } catch (Exception e) {
            throw new StubException(this.playerRecord.playerName());
        }
    }

    //CALLBACKS actions from clients
    @Override
    public void notifyGameCreated(int gameID, String playerName, int playerNum){
        this.playerRecord = new PlayerRecord(gameID, playerName);
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.GAME_CREATED, gameID, playerNum);
        sendMessage(message);
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
        sendMessage(message);
    }

    @Override
    public void notifyNewPlayerConnected(String playerName){
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.PLAYER_JOINED_GAME, playerName);
        sendMessage(message);
    }

    @Override
    public void notifySuccessfullyJoinedGame(int gameID, int playerNum, ArrayList<String> players, Map<String, Color> totemColors) {
        Type playersType = new TypeToken<ArrayList<String>>(){}.getType();
        Type totemMapType = new TypeToken<Map<String, Color>>(){}.getType();

        JsonElement playersJE = gson.toJsonTree(players, playersType);
        JsonElement totemJE = gson.toJsonTree(totemColors, totemMapType);

        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.SUCCESSFULLY_JOINED, gameID, playerNum, playersJE,  totemJE);
        sendMessage(message);
    }

    @Override
    public void notifyPlayerLeftGame(String playerName) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.LEFT_GAME, playerName);
        sendMessage(message);
    }

    @Override
    public void notifyNewHost() {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.NEW_HOST);
        sendMessage(message);
    }

    @Override
    public void notifyAvailableGames(Map<Integer, GamePlayers> availableGames){
        Type type = new TypeToken<Map<Integer, GamePlayers>>(){}.getType();
        JsonElement serializedGames = gson.toJsonTree(availableGames, type);
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.GET_AVAILABLE_GAMES, serializedGames);
        sendMessage(message);
    }

    @Override
    public void notifyTotemColor(String playerName, Color totemColor) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.CHOSEN_TOTEM_COLOR, playerName, totemColor.toString());
        sendMessage(message);
    }

    @Override
    public void notifyStartRound(Map<EventType, ArrayList<PlayerEventResults>> lastEventsResults, ArrayList<Card> newTopRow,
                                 ArrayList<Card> newBottomRow, ArrayList<BuildingCard> newTopBuildings,
                                 ArrayList<BuildingCard> newBottomBuildings) {

        Type type = new TypeToken<Map<EventType, ArrayList<PlayerEventResults>>>(){}.getType();
        JsonElement serializedEvents = gson.toJsonTree(lastEventsResults, type);

        Type topType = new TypeToken<ArrayList<Card>>(){}.getType();
        JsonElement serializedTopRow = gson.toJsonTree(newTopRow, topType);

        Type bottomType = new TypeToken<ArrayList<Card>>(){}.getType();
        JsonElement serializedBottomRow = gson.toJsonTree(newBottomRow, bottomType);

        Type topBuildType = new TypeToken<ArrayList<BuildingCard>>(){}.getType();
        JsonElement serializedTopBuilding = gson.toJsonTree(newTopBuildings, topBuildType);

        Type bottomBuildType = new TypeToken<ArrayList<BuildingCard>>(){}.getType();
        JsonElement serializedBottomBuilding = gson.toJsonTree(newBottomBuildings, bottomBuildType);
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.START_ROUND, serializedEvents, serializedTopRow, serializedBottomRow, serializedTopBuilding, serializedBottomBuilding);
        sendMessage(message);
    }

    @Override
    public void notifyDrawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.DRAWN_CARD, playerName, fromTopRow, fromBuildings, index);
        sendMessage(message);
    }

    @Override
    public void notifyChosenTile(String playerName, int index) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.CHOSEN_OFFER_TILE, playerName, index);
        sendMessage(message);
    }

    //CALLBACKS updates from server
    @Override
    public void notifyNewFood(String playerName, int newFoodReserve) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.ADDED_FOOD, playerName, newFoodReserve);
        sendMessage(message);
    }

    @Override
    public void notifyPassedTurn(String playerName) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.TURN_PASSED, playerName);
        sendMessage(message);
    }

    @Override
    public void notifyNewShamansStars(String playerName, int stars) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.ADDED_SHAMAN_STARS, playerName, stars);
        sendMessage(message);
    }

    @Override
    public void notifyNewBuildersDiscount(String playerName, int discount) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.ADDED_BUILDERS_DISCOUNT, playerName, discount);
        sendMessage(message);
    }

    @Override
    public void notifyNewGatherersDiscount(String playerName, int discount) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.ADDED_GATHERERS_DISCOUNT, playerName, discount);
        sendMessage(message);
    }

    @Override
    public void notifyNewPrestigePoints(String playerName, int newPP) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.ADDED_PRESTIGE_POINTS, playerName, newPP);
        sendMessage(message);
    }

    @Override
    public void notifyEra(int era) {
        JsonElement serializedEra = gson.toJsonTree(era);
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.CHANGED_ERA, serializedEra);
        sendMessage(message);
    }

    @Override
    public void notifyEndGame(Map<String, Integer> finalRanking) {
        Type type = new TypeToken<Map<String, Integer>>(){}.getType();
        JsonElement serializedRanking = gson.toJsonTree(finalRanking, type);
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.GAME_ENDED, serializedRanking);
        sendMessage(message);
    }

    @Override
    public void notifyLeaderboardInfo(List<String> leaderboard, int playerPosition) {
        Type type = new TypeToken<List<String>>(){}.getType();
        JsonElement leaderboardJE = gson.toJsonTree(leaderboard, type);
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.LEADERBOARD_INFO, leaderboardJE, playerPosition);
        sendMessage(message);
    }

    @Override
    public void notifyForceQuit(String disconnectedPlayer) {
        SocketMessageDTO message = new SocketMessageDTO(SocketHeaderNames.FORCE_QUIT, disconnectedPlayer);
        sendMessage(message);
    }

    public void sendError(SocketMessageDTO errorMessage){
        outStream.println(gson.toJson(errorMessage));
    }
}
