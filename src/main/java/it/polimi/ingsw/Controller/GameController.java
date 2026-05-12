package it.polimi.ingsw.Controller;

import it.polimi.ingsw.CustomException.*;
import it.polimi.ingsw.CustomException.UIException.IllegalActionPhaseException;
import it.polimi.ingsw.CustomException.UIException.NotEnoughPlayersException;
import it.polimi.ingsw.CustomException.UIException.NotJoinableGameException;
import it.polimi.ingsw.CustomException.UIException.NotTheHostException;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.GameBoard.OfferTile;
import it.polimi.ingsw.Model.GameBoard.OfferTrack;
import it.polimi.ingsw.Model.Users.*;
import it.polimi.ingsw.Networking.Shared.ClientNotifier;
import it.polimi.ingsw.Networking.Shared.PlayerRecord;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Game controller of a single game instance, used to extract
 * information from the View, such as player inputs, and
 * to route them to the associated game model by the calls of its methods
 */
public class GameController {
    private final Game gameInstance;
    private final Map<String, ClientNotifier> connectedClients;
    private String hostPlayer;

    /**
     * GameController's constructor is called in the GameManager when a new game is added
     */
    public GameController(Game gameInstance) {
        this.gameInstance = gameInstance;
        this.connectedClients = new HashMap<>();
    }

    public Game getGameModel() {
        return gameInstance;
    }

    public ArrayList<String> getConnectedClients() {
        return new ArrayList<>(connectedClients.keySet());
    }

    /**
     * Adds player to the game creating the Player object and notifying other players.
     * If player is the first one, the method sets it as the host,
     * if player is the last one, the method updates current game phase.
     */
    public void addPlayer(String playerName, ClientNotifier newNotifier) {
        if(gameInstance.getPlayersNames().contains(playerName)) {
            throw new NotJoinableGameException("ERROR: You can't join this game, because this name is already used by a player in the game.");
        }
        if (gameInstance.getPlayers().size() == gameInstance.getNumPlayer()) {
            throw new NotJoinableGameException("ERROR: This lobby is already full, join another game or wait for someone to disconnect.");
        }
        gameInstance.addPlayer(playerName);
        if(hostPlayer == null) hostPlayer = playerName;
        else if (gameInstance.getPlayers().size() == gameInstance.getNumPlayer()){
            gameInstance.setReadyToStart();
        }
        for(ClientNotifier notifier : connectedClients.values()){
            try {
                notifier.notifyNewPlayerConnected(playerName);
                /*if(gameInstance.isReadyToStart()){
                    notifier.notifyGameReady();         da definire a tempo perso
                }*/
            } catch (StubException e){
                handleCriticalDisconnection();
            }
        }
        addClient(playerName, newNotifier);
    }

    /**
     * This method removes a player from the Game's players' list after he decided to leave the lobby
     */
    /*TODO: valutare quando usarlo e in caso se aggiungere controlli o chiamate ad altri metodi
       per esempio legate a handleCriticalDisconnection()*/
    public void removePlayer(String playerName) {
        if(gameInstance.getPlayersNames().contains(playerName)) {
            gameInstance.removePlayer(playerName);
            for(ClientNotifier notifier : connectedClients.values()){
                try {
                    notifier.notifyPlayerLeftGame(playerName);
                } catch(StubException e){
                    handleCriticalDisconnection();
                }
            }
            removeClient(playerName);
        }
    }

    /**
     * Adds a ClientNotifier to connectedClients. This is used to broadcast an update
     * to everyone, regardless of what networking protocol they are using
     * @param playerName
     * @param notifier
     */
    public void addClient(String playerName, ClientNotifier notifier) {
        connectedClients.put(playerName, notifier);
        try {
            notifier.notifySuccessfullyJoinedGame(gameInstance.getGameID(), gameInstance.getNumPlayer(),  gameInstance.getPlayersNames());
        } catch(StubException e){
            handleCriticalDisconnection();
        }
    }

    //forse è usato solo insieme a removePlayer, in quel caso fare merge: valutare alla fine
    public void removeClient(String playerName) {
        connectedClients.remove(playerName);
    }

    /** chooseTotemColor method is called by View, following player's input.
     * It verifies chosen color is available and then calls respective method
     * in Game class, that updates model. Finally, all clients are notified.     *
     */
    public synchronized void chooseTotemColor(PlayerRecord playerRecord, Color totemColor){
        if(!gameInstance.getAvailableColors().contains(totemColor)){
            throw new UnavailableColorException(totemColor);
        }
        gameInstance.chooseTotemColor(playerRecord.playerName(), totemColor);
        for(ClientNotifier notifier : connectedClients.values()) {
            try {
                notifier.notifyTotemColor(playerRecord.playerName(), totemColor);
            } catch (StubException e){
                handleCriticalDisconnection();
            }
        }
    }

    /**
     * checks if the action is done during the right game phase
     */

    //serve?

    public synchronized void checkPhase(GamePhase phase) {
        if(phase != gameInstance.getGamePhase()){
            throw new IllegalActionPhaseException();
        };
    }

    public synchronized Player setNextPlayer() {
        try {
            Player nextPlayer =  gameInstance.setNextPlayer();
            for (ClientNotifier notifier : connectedClients.values()) {
                //client.updateCurrentPlayer(nextPlayer.getName()); @Deprecated
                //client.notifyCurrentPlayer(...)   TODO: funzione da fare in ClientNotifier
            }
            return nextPlayer;
        }
        catch (LastPlayerOfTurnException e) {
            return null;
        }
    }

    // chiamata da parte client quando il player vuole startare il game.
    // throwa l'eccezione se cerca di far partire il game senza che tutti i giocatori siano entrati
    public void startGame(String requestingPlayer) {
        if(!requestingPlayer.equals(hostPlayer)) {
            throw new NotTheHostException("ERROR: you can't start the game if you're not the host!");
        } else if (!gameInstance.isReadyToStart()) {
            throw new NotEnoughPlayersException();
        } else if(gameInstance.isStarted()){
            throw new IllegalActionPhaseException();
        }
        else {
            gameInstance.startGame();
            //notifyAll(n -> n.notifyGameStarted());
        }

    }

    /**
     * When a new round starts, after all the events are resolved and the rows are repopulated
     * @param turnOrder: the current order for placing totems
     */
    // ANCORA IN BOZZA. NO PLAYGAME() IN GAME MA FLOW DEL GAME DA ATTURARE TRAMITE CHIAMATE DI METODI NEL GAME CONTROLLER
    public void startTurn(ArrayList<Player> turnOrder) {
        try {
            int newRound = gameInstance.getNextRound();
            for (ClientNotifier client : connectedClients.values()) {
                //inoltra chiamata a server controller per update round a tutti i player
                //client.updateCurrentPlayer(turnOrder.getFirst().getName()); @Deprecated
                //client.notifyNewCurrentPlayer(); TODO: funzione da fare in ClientNotifier
            }
            this.playTurn(turnOrder.getFirst());
        }
        catch (LastRoundException e) {
            //throw EndOfGame_Exception();
        }
    }

    public void playTurn(Player player) {
        //????
    }

    public synchronized void handleChooseOfferTile (PlayerRecord playerRecord, int index) {
        Player player = gameInstance.getPlayerByName(playerRecord.playerName());
        OfferTrack offerTrack = gameInstance.getOfferTrack();
        try {
            OfferTile chosen = player.chooseOfferTile(index, offerTrack);
            for(ClientNotifier client : connectedClients.values()) {
                //client.updateCurrentOfferTile(player.getName(), index); @Deprecated
                //client.notifyChosenOfferTile(...); TODO: funzione da fare in ClientNotifier
            }
        }
        catch (OccupiedTileException e) {
            throw new OccupiedTileException();
        }
    }

    public synchronized void handleDraw(PlayerRecord playerRecord, boolean fromTopRow, boolean fromBuilding, int index){
            String playerName = playerRecord.playerName();
            Player currPlayer = gameInstance.getPlayerByName(playerName);
            OfferTrack offerTrack = gameInstance.getOfferTrack();
            boolean cardIsDrawable = currPlayer.drawable(fromTopRow, fromBuilding, index, offerTrack);

            if(cardIsDrawable){
                currPlayer.drawCard(fromTopRow, fromBuilding, index, this.getGameModel().getOfferTrack());
            }
    }

    public synchronized void repopulateTopRow (Player player) {
        // Sarebbe sensato fare un try catch con un eccezione per quando finisce il gioco?
        ArrayList<Card> newTopRow = gameInstance.getOfferTrack().repopulateTopRow();
        for(ClientNotifier client : connectedClients.values()) {
            //client.updateTopRow(newTopRow);   @Deprecated
            //client.notifyNewTopRow(...)   TODO: funzione da fare in ClientNotifier
        }
    }

    /*TODO: definire la fase di shutdown del game a seguito di un client disconnesso e gestire
     in socket la disconnessione*/
    public void handleCriticalDisconnection(){

    }
}