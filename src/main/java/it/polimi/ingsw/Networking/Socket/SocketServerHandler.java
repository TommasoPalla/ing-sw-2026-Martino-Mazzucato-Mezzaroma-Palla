package it.polimi.ingsw.Networking.Socket;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.SocketHeaderNames;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.google.gson.Gson;

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
        //aggiornamenti dati da azioni dei player
        commandHandlers.put(SocketHeaderNames.CHOSEN_TOTEM_COLOR, parameters -> {
            String playerName = (String) parameters[0];
            Color totemColor = Color.valueOf((String) parameters[1]) ;
            try{
                client.updateChosenTotemColor(playerName, totemColor);
            } catch (IOException e){
                //TODO: non so di preciso cosa ci sia da fare qui quindi lascio cosi', stessa cosa anche per tutti gli altri
            }
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
        //TODO: FINIRE DI AGGIUNGERE TUTTI GLI ALTRI VALORI DELL'ENUM

        //aggiornamenti dati da modifiche fatte dalla logica di gioco / dal server
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
        //TODO: da fare tutti gli altri put() per far si che tutti i valori dell'enum siano chiavi della mappa e mettere relative lambda expr.
    }

    //TODO: don't call client.method() anymore, remove RMIClient and directly call ClientController from here, maybe??
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
