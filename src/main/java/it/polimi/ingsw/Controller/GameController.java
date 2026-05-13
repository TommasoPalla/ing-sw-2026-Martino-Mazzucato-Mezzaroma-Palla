package it.polimi.ingsw.Controller;

import it.polimi.ingsw.CustomException.*;
import it.polimi.ingsw.CustomException.IllegalActionPhaseException;
import it.polimi.ingsw.CustomException.UIException.NotEnoughPlayersException;
import it.polimi.ingsw.CustomException.UIException.NotJoinableGameException;
import it.polimi.ingsw.CustomException.UIException.NotTheHostException;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Game.Game;
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
    private String hostClient;

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
     */
    private void addPlayer(String playerName) {
        gameInstance.addPlayer(playerName);
//        for(ClientNotifier notifier : connectedClients.values()){
//            try {
//                notifier.notifyNewPlayerConnected(playerName);
//            } catch (StubException e){
//                handleCriticalDisconnection();
//            }
//        }
    }

    /**
     * This method removes a player from the Game's players' list after he decided to leave the lobby
     */
    /*TODO: valutare quando usarlo e in caso se aggiungere controlli o chiamate ad altri metodi
       per esempio legate a handleCriticalDisconnection()*/
    public void removePlayer(String playerName) {
        if(gameInstance.getPlayersNames().contains(playerName)) {
            gameInstance.removePlayer(playerName);
//            for(ClientNotifier notifier : connectedClients.values()){
//                try {
//                    notifier.notifyPlayerLeftGame(playerName);
//                } catch(StubException e){
//                    handleCriticalDisconnection();
//                }
//            }
        }
    }

    /**
     * Adds a ClientNotifier to connectedClients. This is used to broadcast an update
     * to everyone, regardless of what networking protocol they are using.
     * Moreover, connectedClients identifies players waiting in the lobby
     * for hostClient to start the game.
     * @param playerName a valid name, not already used by a player in this lobby
     * @param newNotifier relative to this specific playerName, to update them
     */
    public void addClient(String playerName, ClientNotifier newNotifier) {
        if(connectedClients.containsKey(playerName)) {
            throw new NotJoinableGameException("ERROR: You can't join this game, because this name is already used by a player in the game.");
        }
        if (connectedClients.size() == gameInstance.getNumPlayer()) {
            throw new NotJoinableGameException("ERROR: This lobby is already full, join another game or wait for someone to disconnect.");
        } else if (connectedClients.isEmpty()){
            hostClient = playerName;
        }
        // notifica gli altri giocatori nella lobby
        for(ClientNotifier notifier : connectedClients.values()){
            try {
                notifier.notifyNewPlayerConnected(playerName);
            } catch (StubException e){
                handleCriticalDisconnection();
            }
        }
        connectedClients.put(playerName, newNotifier);
        if (connectedClients.size() == gameInstance.getNumPlayer()){
            gameInstance.setReadyToStart();
        }
        // se è il primo player, vuol dire che ha creato il game, quindi non entra qui
        if (connectedClients.size() > 1) {
            ArrayList<String> clients = new ArrayList<>(connectedClients.keySet());
            try {
                newNotifier.notifySuccessfullyJoinedGame(gameInstance.getGameID(), gameInstance.getNumPlayer(), clients);
            /*if(gameInstance.isReadyToStart()){
                    notifier.notifyGameReady();         da definire
                }*/
            } catch(StubException e){
                handleCriticalDisconnection();
            }
        }

    }

    //forse è usato solo insieme a removePlayer, in quel caso fare merge: valutare alla fine
    public void removeClient(String playerName) {
        if(connectedClients.containsKey(playerName)) {
            for(ClientNotifier notifier : connectedClients.values()){
                try {
                    notifier.notifyPlayerLeftGame(playerName);
                } catch(StubException e){
                    handleCriticalDisconnection();
                }
            }
            connectedClients.remove(playerName);
        }
    }

    /** chooseTotemColor method is called by View, following player's input.
     * It verifies chosen color is available and then calls respective method
     * in Game class, that updates model. Finally, all clients are notified.     *
     */
    public synchronized void chooseTotemColor(String player, Color totemColor){
        if(!gameInstance.getAvailableColors().contains(totemColor)){
            throw new UnavailableColorException(totemColor);
        }
        gameInstance.chooseTotemColor(player, totemColor);
        for(ClientNotifier notifier : connectedClients.values()) {
            try {
                notifier.notifyTotemColor(player, totemColor);
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
        }
    }

    public synchronized void checkPlayer(Player player){
        if(player != gameInstance.getCurrentPlayer()){
            throw new IllegalActionTurnException();
        }
    }


    //da capire se serve
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
        if(!requestingPlayer.equals(hostClient)) {
            throw new NotTheHostException("ERROR: you can't start the game if you're not the host!");
        } else if (!gameInstance.isReadyToStart()) {
            throw new NotEnoughPlayersException();
        } else if(gameInstance.isStarted()){
            throw new IllegalActionPhaseException();
        }
        else {
            for(String player: connectedClients.keySet()){
                addPlayer(player);
            }
            gameInstance.startGame();
            //notifyAll(n -> n.notifyGameStarted());
            //notifyInitialFood;
            startTurn();
        }

    }

    /**
     * When a new round starts, after all the events are resolved and the rows are repopulated
     */
    public void startTurn() {
        gameInstance.setCurrentPhase(GamePhase.START_TURN);
        int newRound = gameInstance.setNextRound();

        //int oldEra = gameInstance.getEra();
        try {
            gameInstance.initOfferTrack();
        } catch(ChangeEraException e){
            gameInstance.changeEra();
        }

        String firstPlayer = gameInstance.setFirstPlayer();
        try {
            for (ClientNotifier client : connectedClients.values()) {
                //notifyAll(showUpdateOfferTrack();

                //if(oldEra != gameInstance.getEra()){
                //    notifyAll(changedEra); per farlo fancy si potrebbe mandare la carta della nuova era
                //changedEra dovrebbe mostrare interruzione di repopulateTopRow e cambio di edifici: più complicato
                //ma scritto nelle regole

                //inoltra chiamata a server controller per update round a tutti i player
                //client.updateCurrentPlayer(firstPlayer);TODO: funzione da fare in ClientNotifier
            }
        }
        catch (LastRoundException e) {
            //throw EndOfGame_Exception();
        }
        setNextPlayer();
    }

    /**
     * @deprecated
     */
    public void playTurn(Player player) {
        //????
    }

    /**Handles the player request to place the totem on a specific
     * offer tile. It checks whether it is player's turn and the correct phase of the game.
     * The method delegates the update of the model to Game class.
     *
     * @param playerName the player who requests to place the totem
     * @param index index of the offer tile, starting from 0
     */
    public synchronized void handleChooseOfferTile (String playerName, int index) {
        //throws to ServerController IllegalActionPhaseException
        checkPhase(GamePhase.START_TURN);
        Player player = gameInstance.getPlayerByName(playerName);
        //throws to ServerController IllegalActionTurnException
        checkPlayer(player);
        try {
            gameInstance.chooseOfferTile(player, index);
            for(ClientNotifier client : connectedClients.values()) {
                client.notifyChosenTile(playerName, index);
            }
        }
        catch (OccupiedTileException e) {   //viene catchata da serverController ma acknowledged qua, se ne può parlare
            throw new OccupiedTileException();
        }
        catch(StubException e){
            handleCriticalDisconnection();
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

    /*TODO: definire la fase di shutdown del game a seguito di un client disconnesso e gestire
       in socket la disconnessione*/
    public void handleCriticalDisconnection(){
        for(ClientNotifier notifier: connectedClients.values()){
            //notifier.notifyCriticalDisconnection();
            //removeClient();
            //come dico a serverController di togliere il game dalla lista di activeGames?
        }
    }
}