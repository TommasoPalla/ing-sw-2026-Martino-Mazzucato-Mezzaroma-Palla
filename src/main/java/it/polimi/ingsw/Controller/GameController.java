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
            System.out.println(e.getMessage());
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
                System.out.println(e.getMessage());
            }
        }
    }

    /**
     * It removes a client from the list of connected clients when he leaves the lobby.
     * @param playerName the name of the player.
     */
    public void removeClient(String playerName) {
        if(connectedClients.containsKey(playerName)) {
            if (gameInstance.isStarted()) {
                try {
                    notifyAll( n -> {
                        n.notifyForceQuit(playerName);
                    });
                    return;
                } catch (StubException e) {
                    System.out.println(e.getMessage());
                }
            }
            gameInstance.getPlayersTotemColors().remove(playerName);
            try {
                notifyAll( n -> {
                    n.notifyPlayerLeftGame(playerName);
                });
            } catch (StubException e) {
                System.out.println(e.getMessage());
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
            System.out.println(e.getMessage());
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
                    int bonus = (nextPlayer.getCurrentOfferTile() != null) ? nextPlayer.getCurrentOfferTile().getFoodBonus() : 0;
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

        try {
            int newRound = gameInstance.setNextRound();
            System.out.println("[GAME " + gameInstance.getGameID() + "] Round " + newRound + " started.");
        } catch (LastRoundException e) {
            System.out.println("[GAME " + gameInstance.getGameID() + "] Final round reached!");
            notifyAll(n -> {
                n.notifyStartRound(lastEventsResults, new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
            });

            calculateFinalPoints();
        }

        try {
            gameInstance.initOfferTrack();
        } catch(ChangeEraException e){
            gameInstance.changeEra();
            notifyAll(n -> {
                n.notifyEra(gameInstance.getEra());
            });
        }
        OfferTrack offerTrack = gameInstance.getOfferTrack();
        notifyAll( n -> {
            n.notifyStartRound(lastEventsResults, offerTrack.getTopRow(), offerTrack.getBottomRow(), offerTrack.getTopBuildingCard(), offerTrack.getBottomBuildingCard());
        });

        gameInstance.setFirstPlayer();
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
            String currentOccupant = gameInstance.getOfferTrack().getOfferTiles().get(index).getCurrentOccupant();
            throw new OccupiedTileException();
        }

        try{
            gameInstance.chooseOfferTile(player, index);
            notifyAll( n -> {
                n.notifyChosenTile(playerName, index);
            });
        }
        catch(StubException e){
            System.out.println(e.getMessage());
        }
        try {
            setNextPlayer();
        } catch (LastPlayerOfTurnException e) {
            gameInstance.setCurrentPhase(GamePhase.ON_DRAW);
            try {
                setNextPlayer();
            } catch (LastPlayerOfTurnException e1) {
                // All players have 0 draws (unlikely but theoretically possible with Tile A), end round
                //startRound();
            }
        }
    }

    /**
     * This method handles the player request to draw a card. If the draw is successful all players are notified and
     * if the player has drawn all his cards, his totem is moved back to the Turn Tile.
     * @param playerName the player requesting to draw.
     * @param fromTopRow true if the card drawn comes from the top row, false if it's of the bottom row.
     * @param fromBuilding true if the card drawn is a building, false if not.
     * @param index index of the array of the row.
     */
    public synchronized void handleDraw(String playerName, boolean fromTopRow, boolean fromBuilding, int index){
            Player currPlayer = gameInstance.getPlayerByName(playerName);
            OfferTrack offerTrack = gameInstance.getOfferTrack();
            int oldFoodReserve = currPlayer.getTribe().getFoodReserve();
            int oldPrestigePoints = currPlayer.getTribe().getPrestigePoints();

            Card drawn = currPlayer.drawCard(fromTopRow, fromBuilding, index, offerTrack);

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
                            int minCost = 20;
                            for (BuildingCard buildingCard : gameInstance.getOfferTrack().getTopBuildingCard()) {
                                if (buildingCard.getCost() < minCost) minCost = buildingCard.getCost();
                            }
                            if (minCost == 20 || tribe.getFoodReserve() < minCost-tribe.getBuildersDiscount())
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
                            int minCost = 20;
                            for (BuildingCard buildingCard : gameInstance.getOfferTrack().getBottomBuildingCard()) {
                                if (buildingCard.getCost() < minCost) minCost = buildingCard.getCost();
                            }
                            if (minCost == 20 || tribe.getFoodReserve() < minCost-tribe.getBuildersDiscount())
                                currPlayer.setRemainingDraws(currPlayer.getRemainingAbove(), 0);
                        }
                    }

                    // If player still has cards to draw...
                    if(currPlayer.getRemainingAbove() != 0 || currPlayer.getRemainingBelow() != 0){
                        int foodDelta = currPlayer.getTribe().getFoodReserve() - oldFoodReserve;
                        int prestigeDelta = currPlayer.getTribe().getPrestigePoints() - oldPrestigePoints;

                        notifyAll(n -> n.notifyNewFood(playerName, foodDelta));
                        notifyAll(n -> n.notifyNewPrestigePoints(playerName, prestigeDelta));
                    }
                    else {
                        if(gameInstance.getOfferTrack().getTurnTile().getTurnOrder().size() == gameInstance.getNumPlayer())
                            // Return to tile food bonus
                            gameInstance.getOfferTrack().getTurnTile().returnToStartingTile(currPlayer, gameInstance.getBuildingManager());
                        int foodDelta = currPlayer.getTribe().getFoodReserve() - oldFoodReserve;
                        int prestigeDelta = currPlayer.getTribe().getPrestigePoints() - oldPrestigePoints;

                        notifyAll(n -> n.notifyNewFood(playerName, foodDelta));
                        notifyAll(n -> n.notifyNewPrestigePoints(playerName, prestigeDelta));

                        endDrawingTurn();
                    }
                } catch (StubException e) {
                    System.out.println(e.getMessage());
                }
            }
    }

    /**
     * This method handles the request by the player to pass his turn if there are no more available character cards
     * left to draw and if he doesn't want to draw any building card.
     * @param playerName the name of the player requesting to pass his drawing turn.
     */
    public synchronized void handlePassTurn(String playerName) {
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

            if (gameInstance.getOfferTrack().getTurnTile().getTurnOrder().size() == gameInstance.getNumPlayer())
                // Return to tile food bonus
                gameInstance.getOfferTrack().getTurnTile().returnToStartingTile(currPlayer, gameInstance.getBuildingManager());
            int foodDelta = currPlayer.getTribe().getFoodReserve() - oldFoodReserve;
            int prestigeDelta = currPlayer.getTribe().getPrestigePoints() - oldPrestigePoints;
            notifyAll(n -> n.notifyNewFood(playerName, foodDelta));
            notifyAll(n -> n.notifyNewPrestigePoints(playerName, prestigeDelta));

            endDrawingTurn();
        } catch (StubException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * This internal method manages the end of a player's drawing turn where next player is set. It catches a
     * {@link LastPlayerOfTurnException} if it was the last player of the turn order. If it was
     * it checks if there's a
     * player who can draw an additional card applying the building effect, else the events are resolved and a new round
     * is started
     */
    private void endDrawingTurn() {
        Player additionalDrawPlayer = null;
        // If the turn order list is longer, it means that the owner of the DrawAdditionalCard building has used its
        // effect
        if (gameInstance.getOfferTrack().getTurnTile().getTurnOrder().size() > gameInstance.getNumPlayer()) {
            additionalDrawPlayer = gameInstance.getCurrentPlayer();
            gameInstance.getOfferTrack().getTurnTile().getTurnOrder().removeLast();
            gameInstance.setCurrentPlayer(gameInstance.getOfferTrack().getTurnTile().getTurnOrder().getLast());
        }

        try {
            setNextPlayer();
        } catch (LastPlayerOfTurnException e) {
            if (!gameInstance.checkAdditionalDraw()) {
                if (additionalDrawPlayer != null) {
                    additionalDrawPlayer.setCanDrawAdditional(true);
                }
                // EVENT RESOLUTION
                Map<EventType, ArrayList<PlayerEventResults>> eventsResults = gameInstance.resolveEvents();
                startRound(eventsResults);
            }
        } catch (StubException e) {
            System.out.println(e.getMessage());
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

    public void handleCriticalDisconnection(String disconnectedPlayer){
        System.err.println("[GAME " + gameInstance.getGameID() + "] Connection error detected. Forcing all clients to quit and clearing lobby.");
        notifyAll( n -> {
            try {
                n.notifyForceQuit(disconnectedPlayer);
            } catch (StubException e){
                System.out.println(e.getMessage());
            }
        });
        connectedClients.clear();
    }
}