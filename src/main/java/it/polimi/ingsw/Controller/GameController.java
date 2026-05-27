package it.polimi.ingsw.Controller;

import it.polimi.ingsw.CustomException.*;
import it.polimi.ingsw.CustomException.IllegalActionPhaseException;
import it.polimi.ingsw.CustomException.UIException.NotEnoughPlayersException;
import it.polimi.ingsw.CustomException.UIException.NotJoinableGameException;
import it.polimi.ingsw.CustomException.UIException.NotTheHostException;
import it.polimi.ingsw.CustomException.UIException.TotemColorNotChosen;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.GameBoard.OfferTrack;
import it.polimi.ingsw.Model.Users.*;
import it.polimi.ingsw.Networking.Shared.ClientNotifier;
import it.polimi.ingsw.Networking.Shared.PlayerRecord;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Game controller of a single game instance, used to extract
 * information from the View, such as player inputs, and
 * to route them to the associated game model by the calls of its methods
 */
public class GameController {
    private final Game gameInstance;
    private final Map<String, ClientNotifier> connectedClients;
    private String hostClient;
    private final ExecutorService notificationThreads = Executors.newCachedThreadPool();

    /**
     * GameController's constructor is called in the GameManager when a new game is added
     */
    public GameController(Game gameInstance) {
        this.gameInstance = gameInstance;
        this.connectedClients = new LinkedHashMap<>();
    }

    public Game getGameModel() {
        return gameInstance;
    }

    public ArrayList<String> getConnectedClients() {
        return new ArrayList<>(connectedClients.keySet());
    }

    /**
     * This method calls the respective method in Game to add a player while in Lobby State
     */

    public synchronized void notifyAll(Consumer<ClientNotifier> action){
        for(ClientNotifier notifier : connectedClients.values()){
            notificationThreads.submit( () -> {
                try {
                    action.accept(notifier);
                } catch (Exception e) {/*exception della notifica fallita*/}
            });
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
        try {
            notifyAll( n -> {
                n.notifyNewPlayerConnected(playerName);
            });
        } catch (StubException e) {
            //handleCriticalDisconnection();
        }

        connectedClients.put(playerName, newNotifier);
        if (connectedClients.size() == gameInstance.getNumPlayer()){
            gameInstance.setReadyToStart();
        }
        // se è il primo player, vuol dire che ha creato il game, quindi non entra qui
        if (connectedClients.size() > 1) {
            ArrayList<String> clients = new ArrayList<>(connectedClients.keySet());
            try {
                newNotifier.notifySuccessfullyJoinedGame(gameInstance.getGameID(), gameInstance.getNumPlayer(), clients, gameInstance.getPlayersTotemColors());
            /*if(gameInstance.isReadyToStart()){
                    notifier.notifyGameReady();         da definire
                }*/
            } catch(StubException e){
                //handleCriticalDisconnection();
            }
        }
    }

    //forse è usato solo insieme a removePlayer, in quel caso fare merge: valutare alla fine
    public void removeClient(String playerName) {
        if(connectedClients.containsKey(playerName)) {
            gameInstance.getPlayersTotemColors().remove(playerName);
            try {
                notifyAll( n -> {
                    n.notifyPlayerLeftGame(playerName);
                });
            } catch (StubException e) {
                //handleCriticalDisconnection();
            }
            connectedClients.remove(playerName);
            // se il player che è stato rimosso era l'host, il secondo a essere entrato (se esiste) diventa il nuovo host
            if (playerName.equals(hostClient) && !connectedClients.isEmpty()) {
                String newHost = connectedClients.keySet().iterator().next();
                hostClient = newHost;
                connectedClients.get(newHost).notifyNewHost();
            }
            connectedClients.remove(playerName);
        }
    }

    /** chooseTotemColor method is called by View, following player's input.
     * It verifies chosen color is available and then calls respective method
     * in Game class, that updates model. Finally, all clients are notified.     *
     */
    public synchronized void chooseTotemColor(String playerName, Color totemColor){
        if(!gameInstance.getAvailableColors().contains(totemColor)){
            throw new UnavailableColorException(totemColor);
        }
        gameInstance.chooseTotemColor(playerName, totemColor);
        try {
            notifyAll( n -> {
                n.notifyTotemColor(playerName, totemColor);
            });
        } catch (StubException e) {
            //handleCriticalDisconnection();
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

            //skip players with 0 draws if in ON_DRAW phase (tile A)
            if (gameInstance.getGamePhase() == GamePhase.ON_DRAW) {
                if (nextPlayer.getRemainingAbove() == 0 && nextPlayer.getRemainingBelow() == 0) {
                    System.out.println("[GAME " + gameInstance.getGameID() + "] Player '" + nextPlayer.getName() + "' has 0 draws, returning to tile.");
                    gameInstance.getOfferTrack().getTurnTile().returnToStartingTile(nextPlayer, gameInstance.getBuildingManager());
                    //still need to notify food bonus
                    notifyAll(n -> n.notifyFoodToAdd(nextPlayer.getName(), gameInstance.getOfferTrack().getTurnTile().getTileModifier()[gameInstance.getOfferTrack().getTurnTile().getTurnOrder().indexOf(nextPlayer)]));
                    return setNextPlayer();
                }
            }

            System.out.println("[GAME " + gameInstance.getGameID() + "] Turn changed: current player is now '" + nextPlayer.getName() + "'");
            return nextPlayer;
        }
        catch (LastPlayerOfTurnException e) {
            throw new LastPlayerOfTurnException();
        }
    }

    // chiamata da parte client quando il player vuole startare il game.
    // throwa l'eccezione se cerca di far partire il game senza che tutti i giocatori siano entrati
    public void startGame(String requestingPlayer) {
        if(!requestingPlayer.equals(hostClient)) {
            throw new NotTheHostException("ERROR: you can't start the game if you're not the host!");
        }
        else if (!gameInstance.isReadyToStart()) {
            throw new NotEnoughPlayersException();
        }
        else if (gameInstance.getPlayersTotemColors().size() < gameInstance.getNumPlayer()) {
            throw new TotemColorNotChosen();
        }
        else if(gameInstance.isStarted()){
            throw new IllegalActionPhaseException();
        }
        else {
            System.out.println("[GAME " + gameInstance.getGameID() + "] Starting game...");
            for(String player: connectedClients.keySet()){
                gameInstance.addPlayer(player);
            }
            Map<String,Integer> initialFood = new HashMap<>(gameInstance.startGame());
            List<String> shuffledFirstPlayingOrder = gameInstance.getOfferTrack().getTurnTile().getTurnOrder().stream()
                    .map(Player::getName)
                    .toList();
            System.out.println("[GAME " + gameInstance.getGameID() + "] Turn order: " + shuffledFirstPlayingOrder);
            gameInstance.setCurrentPhase(GamePhase.START_TURN);
            gameInstance.setNextRound();
            setNextPlayer();
            gameInstance.initOfferTrack();
            //TODO: notify game started da fare in socket e chiamare qui
            notifyAll(n -> n.notifyGameStarted(shuffledFirstPlayingOrder, initialFood, gameInstance.getOfferTrack().getTopRow(),
                    gameInstance.getOfferTrack().getBottomRow(), gameInstance.getOfferTrack().getTopBuildingCard(),
                    gameInstance.getOfferTrack().getBottomBuildingCard()));
        }
    }

    /**
     * When a new round starts, after all the events are resolved and the rows are repopulated
     */
    public void startRound() {
        gameInstance.setCurrentPhase(GamePhase.START_TURN);
        try {
            int newRound = gameInstance.setNextRound();
            System.out.println("[GAME " + gameInstance.getGameID() + "] Round " + newRound + " started.");
        } catch (LastRoundException e) {
            System.out.println("[GAME " + gameInstance.getGameID() + "] Final round reached!");
            // chiama i metodi per il calcolo finale dei punti e notifica i players
        }
        notifyAll(ClientNotifier::notifyStartRound);
        //int oldEra = gameInstance.getEra();
        try {
            gameInstance.initOfferTrack();
            notifyAll( n -> {
                n.notifyTopRow(gameInstance.getOfferTrack().getTopRow());
                n.notifyBottomRow(gameInstance.getOfferTrack().getBottomRow());
                n.notifyTopBuildings(gameInstance.getOfferTrack().getTopBuildingCard());
                n.notifyBottomBuildings(gameInstance.getOfferTrack().getBottomBuildingCard());
            });
        } catch(ChangeEraException e){
            gameInstance.changeEra();
        }

        String firstPlayer = gameInstance.setFirstPlayer();
        try {
//            notifyAll(n -> {
//                    n.notifyNextPlayer(firstPlayer);
//            });
            // ????? che roba e' questa qua sotto?
            /*
            for (ClientNotifier client : connectedClients.values()) {
                //notifyAll(showUpdateOfferTrack();

                //if(oldEra != gameInstance.getEra()){
                //    notifyAll(changedEra); per farlo fancy si potrebbe mandare la carta della nuova era
                //changedEra dovrebbe mostrare interruzione di repopulateTopRow e cambio di edifici: più complicato
                //ma scritto nelle regole

                //inoltra chiamata a server controller per update round a tutti i player
                //client.updateCurrentPlayer(turnOrder.getFirst().getName()); @Deprecated
                //client.notifyNewCurrentPlayer();
            }*/
        }
        catch (LastRoundException e) {
            //throw EndOfGame_Exception();
        } catch (StubException e) {
            //handleCriticalDisconnection();
        }
        //setNextPlayer();
    }

    /**Handles the player request to place the totem on a specific
     * offer tile. It checks whether it is player's turn and the correct phase of the game.
     * The method delegates the update of the model to Game class.
     *
     * @param playerName the player who requests to place the totem
     * @param index index of the offer tile, starting from 0
     */
    //TODO: da fare che se tutti hanno scelto si passa alla fase di pesca delle carte
    public synchronized void handleChooseOfferTile (String playerName, int index) {
        System.out.println("[GAME " + gameInstance.getGameID() + "] Action: '" + playerName + "' placing totem on tile " + index);
        //throws to ServerController IllegalActionPhaseException
        checkPhase(GamePhase.START_TURN);
        Player player = gameInstance.getPlayerByName(playerName);
        //throws to ServerController IllegalActionTurnException
        checkPlayer(player);
        if(gameInstance.getOfferTrack().getOfferTiles().get(index).isOccupied()) {
            throw new OccupiedTileException();
        }
        try {
            gameInstance.chooseOfferTile(player, index);
            notifyAll( n -> {
                n.notifyChosenTile(playerName, index);
            });
        }
        catch (OccupiedTileException e) {   //viene catchata da serverController ma acknowledged qua, se ne può parlare
            throw new OccupiedTileException();
        }
        catch(StubException e){
            //handleCriticalDisconnection();
        }
        try {
            setNextPlayer();
        } catch (LastPlayerOfTurnException e) {
            gameInstance.setCurrentPhase(GamePhase.ON_DRAW);
            
//            // Notify phase change and all card rows
//            notifyAll(n -> {
//                n.notifyGamePhase(GamePhase.ON_DRAW);
//                n.notifyTopRow(gameInstance.getOfferTrack().getTopRow());
//                n.notifyBottomRow(gameInstance.getOfferTrack().getBottomRow());
//                n.notifyTopBuildings(gameInstance.getOfferTrack().getTopBuildingCard());
//                n.notifyBottomBuildings(gameInstance.getOfferTrack().getBottomBuildingCard());
//            });

            //distribution of food bonuses from offer tiles (tile A)
            for (Player p : gameInstance.getPlayers()) {
                int bonus = p.getCurrentOfferTile().getFoodBonus();
                if (bonus > 0) {
                    p.getTribe().modifyFood(bonus);
                    notifyAll(n -> n.notifyFoodToAdd(p.getName(), bonus));
                }
            }

            try {
                setNextPlayer();
            } catch (LastPlayerOfTurnException e1) {
                // If all players have 0 draws (unlikely but theoretically possible with Tile A), end round
                startRound();
            }
        }
    }

    public synchronized void handleDraw(PlayerRecord playerRecord, boolean fromTopRow, boolean fromBuilding, int index){
            String playerName = playerRecord.playerName();
            Player currPlayer = gameInstance.getPlayerByName(playerName);
            OfferTrack offerTrack = gameInstance.getOfferTrack();

            Card drawn = currPlayer.drawCard(fromTopRow, fromBuilding, index, offerTrack);

            if(drawn != null){
                try{
                    notifyAll( n -> {
                        n.notifyDrawnCard(playerName, fromTopRow, fromBuilding, index);
                    });
                    
                    if (currPlayer.getRemainingAbove() == 0 && currPlayer.getRemainingBelow() == 0) {
                        // Return to tile food bonus
                        int playersFood = currPlayer.getTribe().getFoodReserve();
                        int foodModifier = gameInstance.getOfferTrack().getTurnTile().getTileModifier()[gameInstance.getOfferTrack().getTurnTile().getTurnOrder().indexOf(currPlayer)];
                        gameInstance.getOfferTrack().getTurnTile().returnToStartingTile(currPlayer, gameInstance.getBuildingManager());
                        if (foodModifier > 0)
                            notifyAll(n -> n.notifyFoodToAdd(playerName, foodModifier));
                        else if (foodModifier < 0) {
                            if (playersFood == 0)
                                notifyAll(n -> n.notifyPrestigePointsToAdd(playerName, -2));
                        }

                        try {
                            setNextPlayer();
                        } catch (LastPlayerOfTurnException e) {
                            //phase ends => resolve events and start next round

                            //non funziona non so perche' TODO fixare
                            gameInstance.getEventManager().resolve(gameInstance.getOfferTrack().getBottomEvents(), this.gameInstance.getPlayers(), gameInstance.getBuildingManager());
                            startRound();
                        }
                    }
                } catch (StubException e) {
                   //handleCriticalDisconnection();
                }
            }
    }

    /*TODO: definire la fase di shutdown del game a seguito di un client disconnesso e gestire
       in socket la disconnessione*/
    public void handleCriticalDisconnection(String disconnectedPlayer){
        System.err.println("[GAME " + gameInstance.getGameID() + "] CRITICAL DISCONNECTION detected. Forcing all clients to quit and clearing lobby.");
        notifyAll( n -> {
            try {
                n.notifyForceQuit(disconnectedPlayer);
            } catch (Exception e){}
        });
        connectedClients.clear();
    }
}