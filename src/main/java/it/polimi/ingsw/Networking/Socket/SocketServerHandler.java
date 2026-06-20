package it.polimi.ingsw.Networking.Socket;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Enums.SocketHeaderNames;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import com.google.gson.Gson;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.EventManagement.PlayerEventResults;
import it.polimi.ingsw.Utils.GsonFactory;
import it.polimi.ingsw.View.GamePlayers;

public class SocketServerHandler implements Runnable{

    private final Map<SocketHeaderNames, Consumer<Object[]>> commandHandlers = new HashMap<>();
    private final BufferedReader inStream;
    private final Gson gson = GsonFactory.create();
    private final VirtualSocketClient client;

    public SocketServerHandler(BufferedReader inStream, VirtualSocketClient client) {
        this.inStream = inStream;
        this.client = client;
        initCommandHandler();
    }

    /**
     * Binding of possible method calls to lambda expressions that defines what those
     * method calls are supposed to do.
     * This uses a "command" design pattern coupled with a map from enums to lambda expressions
     */
    private void initCommandHandler(){
        //----------------CALLBACKS FROM PLAYERS' ACTIONS-------------------------
        commandHandlers.put(SocketHeaderNames.GAME_CREATED, parameters -> {
           int gameID = ((Double) parameters[0]).intValue();
           int playerNum = ((Double) parameters[1]).intValue();
           client.updateGameCreated(gameID, playerNum);
        });
        commandHandlers.put(SocketHeaderNames.GAME_STARTED, parameters -> {
            String playersString = gson.toJson(parameters[0]);
            Type playersListType = new TypeToken<List<String>>(){}.getType();
            List<String> firstTurnOrder = gson.fromJson(playersString, playersListType);

            String foodString = gson.toJson(parameters[1]);
            Type foodMapType = new TypeToken<Map<String, Integer>>(){}.getType();
            Map<String, Integer> initialFood = gson.fromJson(foodString, foodMapType);

            String topRowString = gson.toJson(parameters[2]);
            Type topRowType = new TypeToken<ArrayList<Card>>(){}.getType();
            ArrayList<Card> newTopRow = gson.fromJson(topRowString, topRowType);

            String bottomRowString = gson.toJson(parameters[3]);
            Type bottomRowType = new TypeToken<ArrayList<Card>>(){}.getType();
            ArrayList<Card> newBottomRow = gson.fromJson(bottomRowString, bottomRowType);

            String buildingsTopRowString = gson.toJson(parameters[4]);
            Type buildingsTopRowType = new TypeToken<ArrayList<BuildingCard>>(){}.getType();
            ArrayList<BuildingCard> newBuildingTopRow = gson.fromJson(buildingsTopRowString, buildingsTopRowType);

            String buildingsBottomRowString = gson.toJson(parameters[5]);
            Type buildingsBottomRowType = new TypeToken<ArrayList<BuildingCard>>(){}.getType();
            ArrayList<BuildingCard> newBuildingBottomRow = gson.fromJson(buildingsBottomRowString, buildingsBottomRowType);
            client.updateGameStarted(firstTurnOrder, initialFood, newTopRow, newBottomRow, newBuildingTopRow, newBuildingBottomRow);
        });
        commandHandlers.put(SocketHeaderNames.PLAYER_JOINED_GAME, parameters -> {
            String playerName = (String) parameters[0];
            client.updatePlayerConnected(playerName);
        });
        commandHandlers.put(SocketHeaderNames.SUCCESSFULLY_JOINED, parameters -> {
           int gameID = ((Double) parameters[0]).intValue();
           int playerNum = ((Double) parameters[1]).intValue();

           String playersString = gson.toJson(parameters[2]);
           Type playersType = new TypeToken<ArrayList<String>>(){}.getType();
           ArrayList<String> playerNames = gson.fromJson(playersString, playersType);

           String colorsString = gson.toJson(parameters[3]);
           Type totemMapType = new TypeToken<Map<String, Color>>(){}.getType();
           Map<String, Color> playerToColors = gson.fromJson(colorsString, totemMapType);
           client.successfullyJoinedGame(gameID, playerNum, playerNames, playerToColors);
        });
        commandHandlers.put(SocketHeaderNames.LEFT_GAME, parameters -> {
            String playerName = (String) parameters[0];
            client.updatePlayerLeftGame(playerName);
        });
        commandHandlers.put(SocketHeaderNames.NEW_HOST, parameters -> {
            client.updateNewHost();
        });
        commandHandlers.put(SocketHeaderNames.GET_AVAILABLE_GAMES, parameters -> {
            String gamesString = gson.toJson(parameters[0]);
            Type type = new TypeToken<Map<Integer, GamePlayers>>(){}.getType();
            Map<Integer, GamePlayers> availableGames = gson.fromJson(gamesString, type) ;
            client.updateAvailableGames(availableGames);
        });
        commandHandlers.put(SocketHeaderNames.START_ROUND, parameters -> {
            String eventResultsString = gson.toJson(parameters[0]);
            Type eventResultsType = new TypeToken<Map<EventType, ArrayList<PlayerEventResults>>>(){}.getType();
            Map<EventType, ArrayList<PlayerEventResults>> eventResults = gson.fromJson(eventResultsString, eventResultsType);

            String topRowString = gson.toJson(parameters[1]);
            Type topType = new TypeToken<ArrayList<Card>>(){}.getType();
            ArrayList<Card> newTopRow = gson.fromJson(topRowString, topType);

            String bottomRowString = gson.toJson(parameters[2]);
            Type bottomType = new TypeToken<ArrayList<Card>>(){}.getType();
            ArrayList<Card> newBottomRow = gson.fromJson(bottomRowString, bottomType);

            String topBuildingsString = gson.toJson(parameters[3]);
            Type topBuildType = new TypeToken<ArrayList<BuildingCard>>(){}.getType();
            ArrayList<BuildingCard> newTopBuildings = gson.fromJson(topBuildingsString, topBuildType);

            String bottomBuildingsString = gson.toJson(parameters[4]);
            Type bottomBuildType = new TypeToken<ArrayList<BuildingCard>>(){}.getType();
            ArrayList<BuildingCard> newBottomBuildings = gson.fromJson(bottomBuildingsString, bottomBuildType);
            client.updateStartRound(eventResults, newTopRow, newBottomRow, newTopBuildings, newBottomBuildings);
        });
        commandHandlers.put(SocketHeaderNames.CHOSEN_TOTEM_COLOR, parameters -> {
            String playerName = (String) parameters[0];
            Color totemColor = Color.valueOf((String) parameters[1]) ;
            client.updateChosenTotemColor(playerName, totemColor);
        });
        commandHandlers.put(SocketHeaderNames.DRAWN_CARD, parameters -> {
            String playerName = (String) parameters[0];
            boolean fromTopRow = (boolean) parameters[1];
            boolean fromBuildings = (boolean) parameters[2];
            int index = ((Double) parameters[3]).intValue();
            client.updateDrawnCard(playerName, fromTopRow, fromBuildings, index);
        });
        commandHandlers.put(SocketHeaderNames.TURN_PASSED, parameters -> {
           String playerName = (String) parameters[0];
            client.updateTurnPassed(playerName);
        });
        commandHandlers.put(SocketHeaderNames.CHOSEN_OFFER_TILE, parameters -> {
            String playerName = (String) parameters[0];
            int index = ((Double) parameters[1]).intValue();
            client.updateChosenTile(playerName, index);
        });

        //----------------CALLBACKS FROM GAME STATE (SERVER) UPDATES-------------------------
        commandHandlers.put(SocketHeaderNames.ADDED_FOOD, parameters -> {
            String playerName = (String) parameters[0];
            int food = ((Double) parameters[1]).intValue();
            client.updateFood(playerName, food);
        });
        commandHandlers.put(SocketHeaderNames.ADDED_SHAMAN_STARS, parameters -> {
            String playerName = (String) parameters[0];
            int shamanStars = ((Double) parameters[1]).intValue();
            client.updateShamansStars(playerName, shamanStars);
        });
        commandHandlers.put(SocketHeaderNames.ADDED_PRESTIGE_POINTS, parameters -> {
            String playerName = (String) parameters[0];
            int prestigePoints = ((Double) parameters[1]).intValue();
            client.updatePrestigePoints(playerName, prestigePoints);
        });
        commandHandlers.put(SocketHeaderNames.ADDED_BUILDERS_DISCOUNT, parameters -> {
            String playerName = (String) parameters[0];
            int discount = ((Double) parameters[1]).intValue();
            client.updateBuildersDiscount(playerName, discount);
        });
        commandHandlers.put(SocketHeaderNames.ADDED_GATHERERS_DISCOUNT, parameters -> {
            String playerName = (String) parameters[0];
            int discount = ((Double) parameters[1]).intValue();
            client.updateGatherersDiscount(playerName, discount);
        });
        commandHandlers.put(SocketHeaderNames.CHANGED_GAME_PHASE, parameters -> {
            GamePhase phase = GamePhase.valueOf((String) parameters[0]);
            client.updateGamePhase(phase);
        });
        commandHandlers.put(SocketHeaderNames.CHANGED_ERA, parameters -> {
           int newEra = ((Double) parameters[0]).intValue();
           client.updateEra(newEra);
        });
        commandHandlers.put(SocketHeaderNames.GAME_ENDED, parameters -> {
            String finalRankingString = gson.toJson(parameters[0]);
            Type finalRankingType = new TypeToken<Map<String, Integer>>(){}.getType();
            Map<String, Integer> finalRanking = gson.fromJson(finalRankingString, finalRankingType);
            client.updateEndGame(finalRanking);
        });
        commandHandlers.put(SocketHeaderNames.LEADERBOARD_INFO, parameters -> {
            String leaderboardString = gson.toJson(parameters[0]);
            Type leaderboardType = new TypeToken<List<String>>(){}.getType();
            List<String> leaderboard = gson.fromJson(leaderboardString, leaderboardType);

            int playerPosition = ((Double) parameters[1]).intValue();
            client.updateLeaderboardInfo(leaderboard,playerPosition);
        });
        commandHandlers.put(SocketHeaderNames.FORCE_QUIT, parameters -> {
            String disconnectedPlayer = (String) parameters[0];
            client.forceQuit(disconnectedPlayer);
        });
        commandHandlers.put(SocketHeaderNames.ERROR, parameters -> {
            String errorMessage = (String) parameters[0];
            throw new RuntimeException(errorMessage);
        });
    }

    @Override
    public void run() {
        String incomingMessage;
        try{
            while((incomingMessage = inStream.readLine()) != null) {
                SocketMessageDTO socketDTO = gson.fromJson(incomingMessage, SocketMessageDTO.class);
                SocketHeaderNames socketHeader = socketDTO.getCommandName();

                Consumer<Object[]> handler = commandHandlers.get(socketHeader);
                if(handler != null){
                    try {
                        handler.accept(socketDTO.getParameters());
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    System.out.println("ERROR: " + socketHeader + " is not a valid command"); //chiaramente un placeholder, va messo qualcosa di meglio
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}
