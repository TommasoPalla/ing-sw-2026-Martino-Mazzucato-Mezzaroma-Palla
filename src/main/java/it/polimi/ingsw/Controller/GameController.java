package it.polimi.ingsw.Controller;

import it.polimi.ingsw.CustomException.*;
import it.polimi.ingsw.CustomException.IllegalActionPhaseException;
import it.polimi.ingsw.CustomException.UIException.NotEnoughPlayersException;
import it.polimi.ingsw.CustomException.UIException.NotJoinableGameException;
import it.polimi.ingsw.CustomException.UIException.NotTheHostException;
import it.polimi.ingsw.CustomException.UIException.TotemColorNotChosen;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Model.EventManagement.PlayerEventResults;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.GameBoard.OfferTrack;
import it.polimi.ingsw.Model.Users.*;
import it.polimi.ingsw.Networking.Shared.ClientNotifier;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * The Game controller manages a single game instance. It's used to extract
 * information from the View, such as player inputs, and
 * to route them to the associated game model by the calls of its methods. The instance of this game controller is
 * stored in the unique server controller, by which its methods are called.
 */
public class GameController {

    /**
     * The {@link Game} instance of the game associated to this controller, used to call its methods.
     */
    private final Game gameInstance;

    /**
     * A map containing all the names of the clients connected to this game, mapped to their {@link ClientNotifier}.
     * It represents the current players in the lobby game and the list of all players playing this game.
     */
    private final Map<String, ClientNotifier> connectedClients;

    /**
     * The host client of this game. By default, it's the creator of the lobby. If the host leaves, the player who joined
     * after him becomes the host. the host is the only player allowed to start the game.
     */
    private String hostClient;

    private final ExecutorService notificationThreads = Executors.newCachedThreadPool();

    /**
     * The map where the final ranking will be stored at the end of the game.
     */
    Map<String, Integer> finalRanking;

    /**
     * GameController's constructor is called in the {@link it.polimi.ingsw.Networking.Shared.ServerController}
     * when a new game is added.
     */
    public GameController(Game gameInstance) {
        this.gameInstance = gameInstance;
        this.connectedClients = new LinkedHashMap<>();
        this.finalRanking = new LinkedHashMap<>();
    }

    public Game getGameModel() {
        return gameInstance;
    }

    public ArrayList<String> getConnectedClients() {
        return new ArrayList<>(connectedClients.keySet());
    }

    public Map<String, ClientNotifier> getConnectedClientsNotifiers() {
        return connectedClients;
    }

    public Map<String, Integer> getFinalRanking() {
        return finalRanking;
    }

    /**
     * This method is called when a notification has to be broadcasted to all the players of the game. A new thread is created
     * for each single notification. It uses a {@link Consumer} pattern to manage the notification forwarding.
     * @param action the type of notification sent.
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
     * @param playerName a valid name, not already used by a player in this lobby.
     * @param newNotifier {@link ClientNotifier} relative to this specific playerName, to update them.
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
            gameInstance.setReadyToStart(true);
        }
        // se è il primo player, vuol dire che ha creato il game, quindi non entra qui
        if (connectedClients.size() > 1) {
            ArrayList<String> clients = new ArrayList<>(connectedClients.keySet());
            try {
                newNotifier.notifySuccessfullyJoinedGame(gameInstance.getGameID(), gameInstance.getNumPlayer(), clients, gameInstance.getPlayersTotemColors());
            } catch(StubException e){
                //handleCriticalDisconnection();
            }
        }
    }

    /**
     * It removes a client from the list of connected clients when he leaves the lobby.
     * @param playerName the name of the player.
     */
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
            gameInstance.setReadyToStart(false);
        }
    }

    /** chooseTotemColor method is called by View, following player's input.
     * It verifies chosen color is available and then calls respective method
     * in Game class, that updates model. Finally, all clients are notified.
     * @param playerName the name of the player choosing the totem color.
     * @param totemColor the {@link Color} value of the totem chosen by the player.
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

    public synchronized void checkPlayer(Player player){
        if(player != gameInstance.getCurrentPlayer()){
            throw new IllegalActionTurnException();
        }
    }

    /**
     * This method is used to set the next player of the game. If the {@link GamePhase} is ON_DRAW and the next player
     * has no draws (tile A case) it immediately moves it back to the respective slot of the starting tile and gives him
     * the food bonus, it notifies the new food bonus, and it passes to the next player.
     * @return the reference to the next {@link Player}.
     */
    public synchronized Player setNextPlayer() {
        try {
            Player nextPlayer =  gameInstance.setNextPlayer();

            //skip players with 0 draws if in ON_DRAW phase (tile A)
            if (gameInstance.getGamePhase() == GamePhase.ON_DRAW) {
                int oldFoodReserve = nextPlayer.getTribe().getFoodReserve();

                if (nextPlayer.getRemainingAbove() == 0 && nextPlayer.getRemainingBelow() == 0) {
                    System.out.println("[GAME " + gameInstance.getGameID() + "] Player '" + nextPlayer.getName() + "' has 0 draws, returning to tile.");
                    int bonus = nextPlayer.getCurrentOfferTile().getFoodBonus();
                    gameInstance.getOfferTrack().getTurnTile().returnToStartingTile(nextPlayer, gameInstance.getBuildingManager());
                    if (bonus > 0) {
                        nextPlayer.getTribe().modifyFood(bonus);
                        notifyAll(n -> n.notifyNewFood(nextPlayer.getName(), nextPlayer.getTribe().getFoodReserve()-oldFoodReserve));
                    }
                    System.out.println("[GAME " + gameInstance.getGameID() + "] Turn changed: current player is now '" + nextPlayer.getName() + "'");
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

    /**
     * This method starts the game when the host client launches the command. It creates the {@link Player} instances
     * for every connected client, calculates the initial food to give to every player, it shuffles the first turn order,
     * it sets the {@link GamePhase} to START_TURN and inits the offer track with the first rows. Finally, it notifies
     * all players with the information needed.
     * @param requestingPlayer the player requesting to start the game.
     * @throws NotTheHostException if the player requesting to start the game is not the host.
     * @throws NotEnoughPlayersException if there are not enough players to start the game.
     * @throws TotemColorNotChosen if there are still some player left who have not chosen their totem color.
     */
    public synchronized void startGame(String requestingPlayer) throws NotTheHostException, NotEnoughPlayersException, TotemColorNotChosen {
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
     * This method is called when the drawing phase comes to an end and the last round's bottom events are resolved.
     * It sets the {@link GamePhase} to START_TURN and notifies the players about the start of the round and about the
     * results of the last events. It updates the rows of the offer track and updates the era if necessary. Finally,
     * it notifies all the players with the new offer track status. It catches a {@link LastRoundException} if the
     * previous round was the last one. If it was so, it calculates the final points to set the final ranking
     * @param lastEventsResults the map containing all information about the results of the last resolved events.
     */
    public void startRound(Map<EventType, ArrayList<PlayerEventResults>> lastEventsResults) {
        gameInstance.setCurrentPhase(GamePhase.START_TURN);
        boolean isLastRound = false;
        notifyAll(n -> {
            n.notifyStartRound(lastEventsResults);
        });
        try {
            int newRound = gameInstance.setNextRound();
            System.out.println("[GAME " + gameInstance.getGameID() + "] Round " + newRound + " started.");
        } catch (LastRoundException e) {
            System.out.println("[GAME " + gameInstance.getGameID() + "] Final round reached!");
            isLastRound = true;

//        if (isLastRound) {
//            // In the last round, we also resolve top row events that were left there
//            ArrayList<EventCard> topEvents = gameInstance.getOfferTrack().getTopEvents();
//            if (!topEvents.isEmpty()) {
//                Map<EventType, ArrayList<PlayerEventResults>> topEventsResults = gameInstance.getEventManager().resolve(topEvents, gameInstance.getPlayers(), gameInstance.getBuildingManager());
//
//                // Combine bottom and top events results
//                Map<EventType, ArrayList<PlayerEventResults>> allEventsResults = new HashMap<>(lastEventsResults);
//                for (EventType type : topEventsResults.keySet()) {
//                    if (allEventsResults.containsKey(type)) {
//                        allEventsResults.get(type).addAll(topEventsResults.get(type));
//                    } else {
//                        allEventsResults.put(type, topEventsResults.get(type));
//                    }
//                }
//
//                notifyAll(n -> {
//                    n.notifyStartRound(allEventsResults);
//                });
//            } else {
//                notifyAll(n -> {
//                    n.notifyStartRound(lastEventsResults);
//                });
//            }

            calculateFinalPoints();
        }

//        notifyAll(n -> {
//            n.notifyStartRound(lastEventsResults);
//        });
        try {
            gameInstance.initOfferTrack();
        } catch(ChangeEraException e){
            gameInstance.changeEra();
            //TODO: verificare che arrivi e che venga mostrato al player. In teoria non serve il parametro perché il player
            // sa già l'era
            notifyAll(n -> {
                n.notifyEra(gameInstance.getEra());
            });
        }
        notifyAll( n -> {
            n.notifyTopRow(gameInstance.getOfferTrack().getTopRow());
            n.notifyBottomRow(gameInstance.getOfferTrack().getBottomRow());
            n.notifyTopBuildings(gameInstance.getOfferTrack().getTopBuildingCard());
            n.notifyBottomBuildings(gameInstance.getOfferTrack().getBottomBuildingCard());
        });

        String firstPlayer = gameInstance.setFirstPlayer();
    }

    /**
     * Handles the player request to place the totem on a specific
     * offer tile. It checks whether it is player's turn and the correct phase of the game.
     * The method delegates the update of the model to Game class.
     * @param playerName the player who requests to place the totem
     * @param index index of the offer tile, starting from 0.
     * @throws OccupiedTileException if the tile is already occupied by another player.
     */
    public synchronized void handleChooseOfferTile (String playerName, int index) throws OccupiedTileException{
        System.out.println("[GAME " + gameInstance.getGameID() + "] ACTION: '" + playerName + "' placing totem on tile " + index);
        Player player = gameInstance.getPlayerByName(playerName);
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
        catch (OccupiedTileException e) {
            throw new OccupiedTileException();
        }
        catch(StubException e){
            //handleCriticalDisconnection();
        }
        try {
            setNextPlayer();
        } catch (LastPlayerOfTurnException e) {
            gameInstance.setCurrentPhase(GamePhase.ON_DRAW);
            try {
                setNextPlayer();
            } catch (LastPlayerOfTurnException e1) {
                // If all players have 0 draws (unlikely but theoretically possible with Tile A), end round
                //startRound();
            }
        }
    }

    /**
     * This method handles the player requests to draw a card. If the draw is successful all players are notified and
     * if the player has drawn all his cards the next player is set. It catches a {@link LastPlayerOfTurnException} if
     * this player was the last player of the drawing {@link GamePhase}.
     * If it was, the events are resolved, the game phase is set to START_TURN and a new round is started.
     * When all the players have finished drawing their cards, the bottom row events are resolved
     * and the new round is started.
     * @param playerName the player requesting to draw
     * @param fromTopRow true if the card drawn comes from the top row, false if it's of the bottom row
     * @param fromBuilding true if the card drawn is a building, false if not
     * @param index index of the array of the row.
     */
    public synchronized void handleDraw(String playerName, boolean fromTopRow, boolean fromBuilding, int index){
            Player currPlayer = gameInstance.getPlayerByName(playerName);
            OfferTrack offerTrack = gameInstance.getOfferTrack();
            int oldFoodReserve = currPlayer.getTribe().getFoodReserve();
            int oldPrestigePoints = currPlayer.getTribe().getPrestigePoints();
            System.out.println("[DEBUG] " + playerName + " prima del pescaggio della carta: F:" + oldFoodReserve + ", PP: " + oldPrestigePoints);

            Card drawn = currPlayer.drawCard(fromTopRow, fromBuilding, index, offerTrack);
            System.out.println("[DEBUG] Cibo e pp del player " + playerName + " dopo il pescaggio della carta: F:" + currPlayer.getTribe().getFoodReserve() + ", PP: " + currPlayer.getTribe().getPrestigePoints());

            if(drawn != null){
                try{
                    notifyAll( n -> {
                        n.notifyDrawnCard(playerName, fromTopRow, fromBuilding, index);
                    });

                    // If the player still has cards to draw from the top row, but the row is empty, and he cannot buy any
                    // building from it, then it sets its remaining draws from above to 0
                    boolean found = false;
                    Tribe tribe = currPlayer.getTribe();
                    if (currPlayer.getRemainingAbove() > 0) {
                        for (Card card : gameInstance.getOfferTrack().getTopRow()) {
                            if (!(card instanceof EventCard)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            int minCost = 0;
                            for (BuildingCard buildingCard : gameInstance.getOfferTrack().getTopBuildingCard()) {
                                if (buildingCard.getCost() < minCost) minCost = buildingCard.getCost();
                            }
                            if (minCost == 0 || tribe.getFoodReserve() < minCost-tribe.getBuildersDiscount())
                                currPlayer.setRemainingDraws(0, currPlayer.getRemainingBelow());
                        }
                    }

                    // If the player still has cards to draw from the bottom row, but the row is empty, and he cannot buy any
                    // building from it, then it sets its remaining draws from below to 0
                    if (currPlayer.getRemainingBelow() > 0) {
                        found = false;
                        for (Card card : gameInstance.getOfferTrack().getBottomRow()) {
                            if (!(card instanceof EventCard)) {
                                found = true;
                                break;
                            }
                        }
                        if (!found) {
                            int minCost = 0;
                            for (BuildingCard buildingCard : gameInstance.getOfferTrack().getBottomBuildingCard()) {
                                if (buildingCard.getCost() < minCost) minCost = buildingCard.getCost();
                            }
                            if (minCost == 0 || tribe.getFoodReserve() < minCost-tribe.getBuildersDiscount())
                                currPlayer.setRemainingDraws(currPlayer.getRemainingAbove(), 0);
                        }
                    }

                    // If player still has cards to draw...
                    if(currPlayer.getRemainingAbove() != 0 || currPlayer.getRemainingBelow() != 0){
                        int foodDelta = currPlayer.getTribe().getFoodReserve() - oldFoodReserve;
                        int prestigeDelta = currPlayer.getTribe().getPrestigePoints() - oldPrestigePoints;

                        System.out.println("[DEBUG] Cibo e pp notificati al player " + playerName + ": F:" + (foodDelta) + ", PP: " + (prestigeDelta));
                        notifyAll(n -> n.notifyNewFood(playerName, foodDelta));
                        notifyAll(n -> n.notifyNewPrestigePoints(playerName, prestigeDelta));
                    }
                    else {
                        // Return to tile food bonus
                        System.out.println("[DEBUG] Cibo e pp del player " + playerName + " prima del ritorno alla turn tile: F:" + currPlayer.getTribe().getFoodReserve() + ", PP: " + currPlayer.getTribe().getPrestigePoints());
                        gameInstance.getOfferTrack().getTurnTile().returnToStartingTile(currPlayer, gameInstance.getBuildingManager());
                        System.out.println("[DEBUG] Cibo e pp del player " + playerName + " dopo il ritorno alla turn tile: F:" + currPlayer.getTribe().getFoodReserve() + ", PP: " + currPlayer.getTribe().getPrestigePoints());
                        int foodDelta = currPlayer.getTribe().getFoodReserve() - oldFoodReserve;
                        int prestigeDelta = currPlayer.getTribe().getPrestigePoints() - oldPrestigePoints;

                        System.out.println("[DEBUG] Cibo e pp notificati al player " + playerName + ": F:" + (foodDelta) + ", PP: " + (prestigeDelta));
                        notifyAll(n -> n.notifyNewFood(playerName, foodDelta));
                        notifyAll(n -> n.notifyNewPrestigePoints(playerName, prestigeDelta));

                        try {
                            setNextPlayer();
                        } catch (LastPlayerOfTurnException e) {
                            // EVENT RESOLUTION
                            gameInstance.setCurrentPhase(GamePhase.ON_EVENT);

                            ArrayList<EventCard> events = gameInstance.getOfferTrack().getBottomEvents();
                            // if the current round is the last one, it also resolves the top row's events
                            if (gameInstance.getCurrentRound() == 10)
                                events.addAll(gameInstance.getOfferTrack().getTopEvents());
                            Map<EventType, ArrayList<PlayerEventResults>> eventsResults = gameInstance.getEventManager().resolve(events, gameInstance.getPlayers(), gameInstance.getBuildingManager());
                            System.out.println(eventsResults);
                            for (EventType eventType : eventsResults.keySet()) {
                                for (PlayerEventResults playerEventResults : eventsResults.get(eventType)) {
                                    System.out.println("cibo e pp modificati dall'evento " + eventType + " per " + playerEventResults.player() + " : " + playerEventResults.foodAndPP()[0] + " , " + playerEventResults.foodAndPP()[1]);
                                }
                            }
                            startRound(eventsResults);
                        }
                    }
                } catch (StubException e) {
                   //handleCriticalDisconnection();
                }
            }
    }

    /**
     * This method handles the request by the player to pass his turn if there are no more available character cards
     * left to draw and if he doesn't want to draw any building card. After that, it sets the next player. It catches a
     * {@link LastPlayerOfTurnException} if this player was the last player of the drawing {@link GamePhase}.
     * If it was, the events are resolved, the game phase is set to START_TURN and a new round is started.
     * @param playerName the name of the player requesting to pass his drawing turn.
     * @throws IllegalClientStateActionException if the player can still draw cards.
     */
    public synchronized void handlePassTurn(String playerName) throws IllegalClientStateActionException {
        Player currPlayer = gameInstance.getPlayerByName(playerName);
        OfferTrack offerTrack = gameInstance.getOfferTrack();
        int oldFoodReserve = currPlayer.getTribe().getFoodReserve();
        int oldPrestigePoints = currPlayer.getTribe().getPrestigePoints();

        try {
            if (currPlayer.getRemainingAbove() > 0) {
                for (Card card : offerTrack.getTopRow()) {
                    if (!(card instanceof EventCard)) {
                        throw new IllegalClientStateActionException("You can not pass your turn if you can draw cards!");
                    }
                }
            }

            if (currPlayer.getRemainingBelow() > 0) {
                for (Card card : offerTrack.getBottomRow()) {
                    if (!(card instanceof EventCard)) {
                        throw new IllegalClientStateActionException("You can not pass your turn if you can draw cards!");
                    }
                }
            }
            currPlayer.setRemainingDraws(0,0);

            notifyAll( n -> n.notifyPassedTurn(playerName));

            // Return to tile food bonus
            gameInstance.getOfferTrack().getTurnTile().returnToStartingTile(currPlayer, gameInstance.getBuildingManager());

            int foodDelta = currPlayer.getTribe().getFoodReserve() - oldFoodReserve;
            int prestigeDelta = currPlayer.getTribe().getPrestigePoints() - oldPrestigePoints;
            notifyAll(n -> n.notifyNewFood(playerName, foodDelta));
            notifyAll(n -> n.notifyNewPrestigePoints(playerName, prestigeDelta));

            try {
                setNextPlayer();
            } catch (LastPlayerOfTurnException e) {
                // EVENT RESOLUTION
                gameInstance.setCurrentPhase(GamePhase.ON_EVENT);

                ArrayList<EventCard> events = gameInstance.getOfferTrack().getBottomEvents();
                if (gameInstance.getCurrentRound() == 10)
                    events.addAll(gameInstance.getOfferTrack().getTopEvents());
                Map<EventType, ArrayList<PlayerEventResults>> eventsResults = gameInstance.getEventManager().resolve(events, gameInstance.getPlayers(), gameInstance.getBuildingManager());
                startRound(eventsResults);
            }
        } catch (StubException e) {
        //handleCriticalDisconnection();
        }
    }

    /**
     * This method is called at the end of the last round, when all the events are resolved. It calls the building whose
     * effect applies at the end of the game and calls the model's method to create the final ranking who will be notified
     * to the players.
     * @throws EndOfGameException to notify the server controller that this game has ended.
     */
    public void calculateFinalPoints() throws EndOfGameException {
        Map<String,Integer> finalPoints = new LinkedHashMap<>();
        for (Player player : gameInstance.getPlayers()) {
            Tribe tribe = player.getTribe();
            gameInstance.getBuildingManager().useBuilding(GamePhase.END_GAME, player);

            int finalPrestigePoints = tribe.calculatePlayerFinalPoints();

            finalPoints.put(player.getName(), finalPrestigePoints);
        }
        Map<String, Integer> finalRanking = finalPoints.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new
                ));
        this.finalRanking = finalRanking;
        notifyAll(n -> {
            n.notifyEndGame(finalRanking);
        });
        throw new EndOfGameException();
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