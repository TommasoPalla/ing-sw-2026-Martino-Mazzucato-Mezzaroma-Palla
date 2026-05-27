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
           try{
               client.updateGameCreated(gameID, playerNum);     //TODO:capire cosa ci va qui, correlato al TODO in ServerController
           } catch (Exception e){
               //TODO: non so di preciso cosa ci sia da fare qui quindi lascio cosi', stessa cosa anche per tutti gli altri
           }
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
            try {
                client.updateGameStarted(firstTurnOrder, initialFood, newTopRow, newBottomRow, newBuildingTopRow, newBuildingBottomRow);
            } catch (Exception e) {}
        });
        commandHandlers.put(SocketHeaderNames.PLAYER_JOINED_GAME, parameters -> {
            String playerName = (String) parameters[0];
            try{
                client.updatePlayerConnected(playerName);
            } catch (Exception e){}
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
           try {
               client.successfullyJoinedGame(gameID, playerNum, playerNames, playerToColors);
           } catch (IOException e){}
        });
        commandHandlers.put(SocketHeaderNames.LEFT_GAME, parameters -> {
            String playerName = (String) parameters[0];
            try{
                client.updatePlayerLeftGame(playerName);
            } catch (IOException e){}
        });
        commandHandlers.put(SocketHeaderNames.NEW_HOST, parameters -> {
            try {
                client.updateNewHost();
            } catch (IOException e) {}
        });
        commandHandlers.put(SocketHeaderNames.GET_AVAILABLE_GAMES, parameters -> {
            String gamesString = gson.toJson(parameters[0]);
            Type type = new TypeToken<Map<Integer, GamePlayers>>(){}.getType();
            Map<Integer, GamePlayers> availableGames = gson.fromJson(gamesString, type) ;
            try {
                client.updateAvailableGames(availableGames);
            } catch(IOException e){}
        });
        commandHandlers.put(SocketHeaderNames.START_ROUND, parameters -> {
            String eventResultsString = gson.toJson(parameters[0]);
            Type eventResultsType = new TypeToken<Map<EventType, ArrayList<PlayerEventResults>>>(){}.getType();
            Map<EventType, ArrayList<PlayerEventResults>> eventResults = gson.fromJson(eventResultsString, eventResultsType);
            try {
                client.updateStartRound(eventResults);
            } catch (IOException e){}
        });
        commandHandlers.put(SocketHeaderNames.CHOSEN_TOTEM_COLOR, parameters -> {
            String playerName = (String) parameters[0];
            Color totemColor = Color.valueOf((String) parameters[1]) ;
            try{
                client.updateChosenTotemColor(playerName, totemColor);
            } catch (IOException e){}
        });
        commandHandlers.put(SocketHeaderNames.DRAWN_CARD, parameters -> {
            String playerName = (String) parameters[0];
            boolean fromTopRow = (boolean) parameters[1];
            boolean fromBuildings = (boolean) parameters[2];
            int index = ((Double) parameters[3]).intValue();
            try{
                client.updateDrawnCard(playerName, fromTopRow, fromBuildings, index);
            } catch (IOException e){}
        });
        commandHandlers.put(SocketHeaderNames.CHOSEN_OFFER_TILE, parameters -> {
            String playerName = (String) parameters[0];
            int index = ((Double) parameters[1]).intValue();
            try{
                client.updateChosenTile(playerName, index);
            } catch (IOException e) {}
        });

        //----------------CALLBACKS FROM GAME STATE (SERVER) UPDATES-------------------------
        commandHandlers.put(SocketHeaderNames.ADDED_FOOD, parameters -> {
            String playerName = (String) parameters[0];
            int food = ((Double) parameters[1]).intValue();
            try{
                client.updateFood(playerName, food);
            } catch (IOException e) {}
        });
        commandHandlers.put(SocketHeaderNames.ADDED_SHAMAN_STARS, parameters -> {
            String playerName = (String) parameters[0];
            int shamanStars = ((Double) parameters[1]).intValue();
            try{
                client.updateShamansStars(playerName, shamanStars);
            } catch (IOException e) {}
        });
        commandHandlers.put(SocketHeaderNames.ADDED_PRESTIGE_POINTS, parameters -> {
            String playerName = (String) parameters[0];
            int prestigePoints = ((Double) parameters[1]).intValue();
            try{
                client.updatePrestigePoints(playerName, prestigePoints);
            } catch (IOException e) {}
        });
        commandHandlers.put(SocketHeaderNames.UPDATED_TOP_ROW, parameters -> {
            String topRowString = gson.toJson(parameters[0]);
            Type type = new TypeToken<ArrayList<Card>>(){}.getType();
            ArrayList<Card> newTopRow = gson.fromJson(topRowString, type);
            try {
                client.updateTopRow(newTopRow);
            } catch (IOException e) {}
        });
        commandHandlers.put(SocketHeaderNames.UPDATED_TOP_BUILDINGS, parameters -> {
            String topBuildingsString = gson.toJson(parameters[0]);
            Type type = new TypeToken<ArrayList<BuildingCard>>(){}.getType();
            ArrayList<BuildingCard> newTopBuildings = gson.fromJson(topBuildingsString, type);
            try {
                client.updateTopBuildings(newTopBuildings);
            } catch (IOException e) {}
        });
        commandHandlers.put(SocketHeaderNames.UPDATED_BOTTOM_ROW, parameters -> {
            String bottomRowString = gson.toJson(parameters[0]);
            Type type = new TypeToken<ArrayList<Card>>(){}.getType();
            ArrayList<Card> newBottomRow = gson.fromJson(bottomRowString, type);
            try {
                client.updateBottomRow(newBottomRow);
            } catch (IOException e) {}
        });
        commandHandlers.put(SocketHeaderNames.UPDATED_BOTTOM_BUILDINGS, parameters -> {
            String bottomBuildingsString = gson.toJson(parameters[0]);
            Type type = new TypeToken<ArrayList<BuildingCard>>(){}.getType();
            ArrayList<BuildingCard> newBottomBuildings = gson.fromJson(bottomBuildingsString, type);
            try {
                client.updateBottomBuildings(newBottomBuildings);
            } catch (IOException e) {}
        });
//        commandHandlers.put(SocketHeaderNames.NEXT_PLAYER, parameters -> {
//           String playerName = (String) parameters[0];
//           try {
//               client.updateNextPlayer(playerName);
//           } catch (IOException e) {}
//        });
        commandHandlers.put(SocketHeaderNames.CHANGED_GAME_PHASE, parameters -> {
            GamePhase phase = GamePhase.valueOf((String) parameters[0]);
            try {
                client.updateGamePhase(phase);
            } catch (IOException e) {}
        });
        commandHandlers.put(SocketHeaderNames.CHANGED_ERA, parameters -> {
           int newEra = ((Double) parameters[0]).intValue();
           try {
               client.updateEra(newEra);
           } catch (IOException e) {}
        });
        commandHandlers.put(SocketHeaderNames.FORCE_QUIT, parameters -> {
            String disconnectedPlayer = (String) parameters[0];
            try {
                client.forceQuit(disconnectedPlayer);
            } catch (IOException e) {}
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
                    new Thread( () -> {
                        handler.accept(socketDTO.getParameters());
                    }).start();
                } else {
                    System.out.println("ERROR: " + socketHeader + " is not a valid command"); //chiaramente un placeholder, va messo qualcosa di meglio
                }

            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}
