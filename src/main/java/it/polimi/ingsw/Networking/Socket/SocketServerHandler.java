package it.polimi.ingsw.Networking.Socket;

import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Enums.SocketHeaderNames;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.google.gson.Gson;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.View.GamePlayers;

public class SocketServerHandler implements Runnable{

    private final Map<SocketHeaderNames, Consumer<Object[]>> commandHandlers = new HashMap<>();
    private final BufferedReader inStream;
    private final Gson gson = new Gson();
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
           int gameID = (int) parameters[0];
           int playerNum = (int) parameters[1];
           try{
               client.updateGameCreated(gameID, playerNum);     //TODO:capire cosa ci va qui, correlato al TODO in ServerController
           } catch (Exception e){
               //TODO: non so di preciso cosa ci sia da fare qui quindi lascio cosi', stessa cosa anche per tutti gli altri
           }
        });
        commandHandlers.put(SocketHeaderNames.PLAYER_JOINED_GAME, parameters -> {
            String playerName = (String) parameters[0];
            try{
                client.updatePlayerConnected(playerName);
            } catch (Exception e){}
        });
        commandHandlers.put(SocketHeaderNames.SUCCESSFULLY_JOINED, parameters -> {
           int gameID = (int) parameters[0];
           int playerNum = (int) parameters[1];
           Type type = new TypeToken<ArrayList<String>>(){}.getType();
           ArrayList<String> playerNames = gson.fromJson(gson.toJson(parameters[2]), type);
           try {
               client.successfullyJoinedGame(gameID, playerNum, playerNames);
           } catch (IOException e){}
        });
        commandHandlers.put(SocketHeaderNames.GET_AVAILABLE_GAMES, parameters -> {
            Type type = new TypeToken<Map<Integer, GamePlayers>>(){}.getType();
            Map<Integer, GamePlayers> availableGames = gson.fromJson(gson.toJson(parameters[0]), type) ;
            try {
                client.updateAvailableGames(availableGames);
            } catch(IOException e){}
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
            int index = (int) parameters[3];
            try{
                client.updateDrawnCard(playerName, fromTopRow, fromBuildings, index);
            } catch (IOException e){}
        });
        commandHandlers.put(SocketHeaderNames.CHOSEN_OFFER_TILE, parameters -> {
            String playerName = (String) parameters[0];
            int index = (int) parameters[1];
            try{
                client.updateChosenTile(playerName, index);
            } catch (IOException e) {}
        });

        //----------------CALLBACKS FROM GAME STATE (SERVER) UPDATES-------------------------
        commandHandlers.put(SocketHeaderNames.ADDED_FOOD, parameters -> {
            String playerName = (String) parameters[0];
            int food = (int) parameters[1];
            try{
                client.updateFood(playerName, food);
            } catch (IOException e) {}
        });
        commandHandlers.put(SocketHeaderNames.ADDED_SHAMAN_STARS, parameters -> {
            String playerName = (String) parameters[0];
            int shamanStars = (int) parameters[1];
            try{
                client.updateShamansStars(playerName, shamanStars);
            } catch (IOException e) {}
        });
        commandHandlers.put(SocketHeaderNames.ADDED_PRESTIGE_POINTS, parameters -> {
            String playerName = (String) parameters[0];
            int prestigePoints = (int) parameters[1];
            try{
                client.updateShamansStars(playerName, prestigePoints);
            } catch (IOException e) {}
        });
        commandHandlers.put(SocketHeaderNames.UPDATED_TOP_ROW, parameters -> {
            Type type = new TypeToken<ArrayList<Card>>(){}.getType();
            ArrayList<Card> newTopRow = gson.fromJson(gson.toJson(parameters[0]), type);
            client.updateTopRow(newTopRow);
        });
        commandHandlers.put(SocketHeaderNames.UPDATED_TOP_BUILDINGS, parameters -> {
            Type type = new TypeToken<ArrayList<BuildingCard>>(){}.getType();
            ArrayList<BuildingCard> newTopBuildings = gson.fromJson(gson.toJson(parameters[0]), type);
            client.updateTopBuildings(newTopBuildings);
        });
        commandHandlers.put(SocketHeaderNames.UPDATED_BOTTOM_ROW, parameters -> {
            Type type = new TypeToken<ArrayList<Card>>(){}.getType();
            ArrayList<Card> newBottomRow = gson.fromJson(gson.toJson(parameters[0]), type);
            client.updateBottomRow(newBottomRow);
        });
        commandHandlers.put(SocketHeaderNames.UPDATED_BOTTOM_BUILDINGS, parameters -> {
            Type type = new TypeToken<ArrayList<BuildingCard>>(){}.getType();
            ArrayList<BuildingCard> newBottomBuildings = gson.fromJson(gson.toJson(parameters[0]), type);
            client.updateTopBuildings(newBottomBuildings);
        });
        commandHandlers.put(SocketHeaderNames.NEXT_PLAYER, parameters -> {
           String playerName = (String) parameters[0];
           client.updateNextPlayer(playerName);
        });
        commandHandlers.put(SocketHeaderNames.CHANGED_GAME_PHASE, parameters -> {
            GamePhase phase = GamePhase.valueOf((String) parameters[0]);
            client.updateGamePhase(phase);
        });
        commandHandlers.put(SocketHeaderNames.CHANGED_ERA, parameters -> {
           int newEra = (int) parameters[0];
           client.updateEra(newEra);
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
                    handler.accept(socketDTO.getParameters());
                } else {
                    System.out.println("ERROR: " + socketHeader + " is not a valid command"); //chiaramente un placeholder, va messo qualcosa di meglio
                }

            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }
}
