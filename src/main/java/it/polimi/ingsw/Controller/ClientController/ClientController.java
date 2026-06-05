package it.polimi.ingsw.Controller.ClientController;

import it.polimi.ingsw.CustomException.*;
import it.polimi.ingsw.CustomException.UIException.*;
import it.polimi.ingsw.Enums.ClientState;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Model.EventManagement.PlayerEventResults;
import it.polimi.ingsw.Networking.Shared.ServerConnection;
import it.polimi.ingsw.View.ClientViewUpdate;
import it.polimi.ingsw.View.ViewInterface;
import it.polimi.ingsw.View.GamePlayers;

import java.util.*;

/**
 * This class is responsible for managing requests from the client to the server and notifies from the server to the
 * client. It represents the "thick client", where some logic (like the turn order management) is placed so the server
 * is not always needed.
 * It keeps a reference to a local model which is unique for every client and is used to extrapolate information
 * for the view. It also has an attribute for the view interface which contains the notification methods based on the
 * kind of UI (TUI or GUI).
 */
public class ClientController implements ClientViewUpdate {

    /**
     * Name of the player.
     */
    private String playerName;

    /**
     * The type of connection used by this client (RMI or Socket).
     */
    private ServerConnection connection;

    /**
     * A reference to this's client local model.
     */
    private ClientModel localModel;

    /**
     * A reference to the type of view the player is using (TUI or GUI).
     */
    private ViewInterface view;

    /**
     * The attribute representing the state of the client, deciding which actions
     * they can perform in that state.
     */
    private volatile ClientState clientState;

    /**
     * The list of available games, printed when a player wants to join a game. It is updated everytime the state of a
     * existing lobby is modified or a new game is created.
     */
    private Map<Integer, GamePlayers> availableGames;


    public ClientController() {
        clientState = ClientState.CONNECTING;
        playerName = "";
        availableGames = new HashMap<>();
    }

    // GETTERS
    public String getPlayerName(){
        return playerName;
    }
    public ServerConnection getConnection(){
        return connection;
    }
    public ClientModel getLocalModel() {
        return localModel;
    }
    public ViewInterface getView(){
        return view;
    }
    public ClientState getClientState(){
        return clientState;
    }


    /**
     * It's called to set the type of connection the player decides to use (RMI or Socket)
     * @param connection the interface with the methods used to send requests to the server.
     */
    public void bindConnection(ServerConnection connection){
        this.connection = connection;
    }

    /**
     * It's called to set the type of UI the player decides to used (TUI or GUI)
     * @param view the interface containing the methods of notification to the view.
     */
    public void bindView(ViewInterface view){
        this.view = view;
    }
    /**
     * called when server responds with a successful 'startGame' request by first player
     * or next players join the game via 'joinGame' method
     * actually called by onGameStarted().
     * */
    public void createLocalModel(int gameId, int numPlayers){
        this.localModel = new ClientModel(gameId, numPlayers);
    }

    /**
     * Set the client state of this client.
     * @param clientState the new client state.
     */
    public void setClientState(ClientState clientState){
        this.clientState = clientState;
    }

    /**
     * Called when the player inserts his nickname the first time or when he wants to change it while in the setup state.
     * Can only be performed before joining a game.
     * @param playerName the new nickname he wants to use.
     */
    public void setPlayerName(String playerName) {
        if(this.playerName.equals(playerName))
            throw new IllegalArgumentException("This is already your name!");
        if (clientState != ClientState.SETUP && clientState != ClientState.CONNECTING) {
            throw new IllegalClientStateActionException("ERROR: You can no longer change your name!");
        }
        this.playerName = playerName;
        view.showNameSet(playerName);
    }

    //-----------------METHODS CALLED FROM PLAYERS' ACTIONS-------------------------------------------------------------

    /**
     * This method forwards the request through the network to the Server Controller, which will add the
     * game to the list of active games.
     * @param numPlayers the number of players chosen by the client who creates the game.
     */
    public void createGame(int numPlayers){
        //con partite multiple non serve verificare che ci sia una partita già inizializzata
        if (clientState != ClientState.SETUP) {
            throw new IllegalClientStateActionException("ERROR: You cannot do that right now!");
        }
        if(numPlayers < 2 || numPlayers > 5){
            throw new IllegalArgumentException();
        }
        connection.createGame(playerName, numPlayers);
        clientState = ClientState.IN_LOBBY;
        //da notificare il player della creazione del game in modo che stampi le possibili azioni da fare mentre in lobby
    }

    public Map<Integer, GamePlayers> getAvailableGames(){
//        if(clientState != ClientState.SETUP){
//            throw new IllegalClientStateActionException("You cannot do that right now!");
//        }
        return availableGames;
    }

    public void joinGame(int gameID){
        if (clientState != ClientState.SETUP) {
            throw new IllegalClientStateActionException("ERROR: You cannot do that right now!");
        }
        try {
            connection.joinGame(this.playerName, gameID);
        } catch (NotJoinableGameException e) {
            throw new NotJoinableGameException(e.getMessage());
        }
    }

    /**
     * This method forwards the request by the player to leave the lobby he is in. Can only be performed if the client
     * state is "IN_LOBBY".
     */
    public void leaveGame(){
        if(clientState == ClientState.SETUP){
            throw new IllegalClientStateActionException("ERROR: You can't leave a game if you're not in one!");
        }
        if(clientState != ClientState.IN_LOBBY){
            throw new IllegalClientStateActionException("ERROR: You can't leave the game now!");
        }
        connection.leaveGame(playerName, localModel.getGameId());
        setClientState(ClientState.SETUP);
    }

    /**
     * This method asks the server to start an existing game. It must be run by the host player.
     * @throws NotTheHostException Throw an exception if the player is not the host.
     * @throws NotEnoughPlayersException Throws an exception if tried to start the game with
     * an insufficient number of players.
     */
    public void startGame() {
        if(clientState != ClientState.IN_LOBBY){
            throw new IllegalClientStateActionException("ERROR: You cannot start a game if you're not in one!");
        }
        try {
            connection.startGame(this.playerName, localModel.getGameId());
        } catch (NotTheHostException e) {
            throw new NotTheHostException(e.getMessage());
        } catch (TotemColorNotChosen e) {
            throw new IllegalArgumentException("ERROR: The game cannot be started, because not all players have chosen their totem color.");
        } catch (NotEnoughPlayersException e) {
            throw new IllegalArgumentException("ERROR: " + (localModel.getNumPlayers() - localModel.getTotemColors().size()) + " player more needed to start the game!");
        }
    }

    /**
     * This method forwards the request by the player to choose a totem color. Can only be performed while in a lobby.
     * @param color the color the player wants to choose for his totem. It has to be available.
     */
    public void chooseTotemColor(Color color){
        if (this.clientState != ClientState.IN_LOBBY) { //non sembra funzionare dopo una forceQuit non so perche'
            throw new IllegalClientStateActionException("ERROR: You cannot choose a totem color right now.");
        }
        if (localModel.getTotemColors().containsKey(playerName)) {
            throw new AlreadyChosenTotemException();
        }
        try {
            connection.chooseTotem(color);
        }catch (UnavailableColorException e){
            throw new UnavailableColorException(color);
        }
    }

    /**
     * This method forwards the request by the player to place his totem on a free offer tile during the totem placing
     * game phase. It can only be performed while in the PLACE_TOTEM client state.
     * @param index the index of the offer tile the player wants to place his totem on.
     */
    public void chooseOfferTile(int index) {
        if (clientState == ClientState.DRAW_CARD)
            throw new IllegalClientStateActionException("ERROR: You have to place your totem right now!");
        if (clientState == ClientState.NOT_IN_TURN)
            throw new IllegalClientStateActionException("ERROR: Wait for your turn!");
        if (clientState != ClientState.PLACE_TOTEM)
            throw new IllegalClientStateActionException("You cannot do that right now!");
        try {
            if (localModel.isOccupied(index)) throw new OccupiedTileException();
            connection.chooseOfferTile(index);
        }catch(OccupiedTileException e){
            throw new OccupiedTileException();
        }
    }

    /*GESTIONE EXCEPTION ESEMPIO!
    Il drawable può lanciare InsufficientFood o IllegalDraw
    che viene catchato qui perché lasciarlo arrivare alla view (che è un altro thread)
    causerebbe la morte del thread che si occupa di questa classe.
    Lancia una nuova eccezione già formattata in un formato user-friendly per la view
    con messaggio personalizzato e 'cause', ovvero l'eccezione originale.
    Questa informazione non viene persa e può essere usata per stampare informazioni aggiuntive
    o per mantenere informazioni di log.
    * */

    /**
     * This method forwards the request by the player to draw a card (character or building) from the top or bottom row.
     * It can only be performed while in the DRAW_CARD client state.
     * @param fromTopRow true if from top row, false if from bottom one.
     * @param fromBuildings true if it's from the building rows, false otherwise.
     * @param index the index of the row chosen.
     */
    public void drawCard(boolean fromTopRow, boolean fromBuildings, int index){
        if (clientState == ClientState.PLACE_TOTEM)
            throw new IllegalClientStateActionException("ERROR: You have to draw right now!");
        if (clientState == ClientState.NOT_IN_TURN)
            throw new IllegalClientStateActionException("ERROR: Wait for your turn!");
        if (clientState != ClientState.DRAW_CARD)
            throw new IllegalClientStateActionException("You cannot do that right now!");
        String currentPlayer = localModel.getCurrentPlayer();
        if(!currentPlayer.isEmpty() && currentPlayer.equals(playerName)){
            try {
                localModel.drawable(fromTopRow, fromBuildings, index);
                connection.drawCard(fromTopRow, fromBuildings, index);
            } catch (InsufficientFoodException | IllegalDrawException e){
                throw new InvalidSelectionException("Can't draw this card: ", e);
            }
        } else {
            throw new IllegalActionPhaseException();
        }
    }

    /**
     * This method forwards the request by the player to pass his turn when he still has to draw cards but there are no
     * character cards available. It can only be performed while in the DRAW_CARD client state.
     */
    public void passTurn() {
        if (clientState != ClientState.DRAW_CARD) {
            throw new IllegalClientStateActionException("ERROR: You cannot do that right now!");
        }

        LightTribe tribe = localModel.getPlayerTribe(this.playerName);
        if (tribe.getRemainingAbove() > 0) {
            for (Card card : localModel.getTopRow()) {
                if (!(card instanceof EventCard)) {
                    throw new IllegalClientStateActionException("You can not pass your turn if you can draw cards!");
                }
            }
        }

        if (tribe.getRemainingBelow() > 0) {
            for (Card card : localModel.getBottomRow()) {
                if (!(card instanceof EventCard)) {
                    throw new IllegalClientStateActionException("You can not pass your turn if you can draw cards!");
                }
            }
        }

        connection.passTurn();
    }

    public void leave() {
        if (clientState != ClientState.END_GAME) {
            throw new IllegalClientStateActionException("ERROR: Invalid command, please try again...");
        }
        clientState = ClientState.SETUP;
        System.out.println("Client " + playerName + " has left the game!");
    }


    //-----------------CALLBACKS FROM SERVER UPDATES--------------------------------------------------------------------
    /* Before making a call to the game controller methods, the client controller checks
     * if the player's action is legal by checking the client light model
     */

    /**
     * It updates with the new available games. The players in the setup state are updated every time the state of
     * an existing game is modified.
     * @param availableGames the map containing the gameID of the game mapped to a record containing the number of
     *                       players needed to start the game and the players in lobby.
     */
    public void updateAvailableGames(Map<Integer, GamePlayers> availableGames){
      this.availableGames = availableGames;
      if(clientState == ClientState.SETUP){
            view.showNewAvailableGames();
        }
    }

    /**
     * Update method for the client controller: it confirms the successful creation of a new game instance.
     * @param gameID the ID given to the new game.
     * @param numPlayers the number of players requested to start the game.
     */
    @Override
    public void updateGameCreated(int gameID, int numPlayers){
        createLocalModel(gameID, numPlayers);
        this.clientState = ClientState.IN_LOBBY;
        view.showGameCreated(gameID);
    }

    /**
     * It notifies the players of the start of the game. It regroups all the infos from the shuffled first turn order,
     * to the initial food for every player to the initial top and bottom rows. It sets the game phase to START_TURN.
     * It updates the local model of every player. From now on, the turn order is locally managed by the clients.
     * @param firstTurnOrder an array which determines the first turn order of the game
     * @param initialFood the initial food every player gets at the start of the game based on the first turn order.
     * @param firstTopRow the first top row of the game.
     * @param firstBottomRow the first bottom row of the game.
     * @param buildingsTopRow the first building top row of the game.
     * @param buildingsBottomRow the first building bottom row of the game (the first round is empty).
     */
    @Override
    public void updateGameStarted(List<String> firstTurnOrder, Map<String,Integer> initialFood, ArrayList<Card> firstTopRow, ArrayList<Card> firstBottomRow, ArrayList<BuildingCard> buildingsTopRow, ArrayList<BuildingCard> buildingsBottomRow) {
        localModel.updateGamePhase(GamePhase.START_TURN);
        localModel.updateTopRow(firstTopRow);
        localModel.updateBottomRow(firstBottomRow);
        localModel.updateTopRowBuildings(buildingsTopRow);
        localModel.updateBottomRowBuildings(buildingsBottomRow);
        localModel.setTurnOrder(firstTurnOrder);
        for (int i=0; i<firstTurnOrder.size(); i++){
            localModel.getTurnTileStatus().put(i, firstTurnOrder.get(i));
        }
        localModel.addPlayersTribes(firstTurnOrder);
        view.showGameStarted();
        updateInitialFood(initialFood);
    }
    /**
     * When a new player enters the lobby, the client controller updates his local model's list of players.
     * @param player the player who entered the lobby.
     */
    @Override
    public void updatePlayerConnected(String player) {
        localModel.addPlayer(player);
        view.showPlayerJoinedLobby(player);
    }

    /**
     * Updates the player about the successful joining of the game. It sets the client state to IN_LOBBY.
     * It also updates the game model with some infos.
     * @param gameID the ID of the game joined.
     * @param numPlayers the number of players needed to start the game.
     * @param players the array of players in lobby (considering the new player too).
     * @param totemColors the map of totem colors. It maps from the player name to its color.
     */
    @Override
    public void updateSuccessfullyJoinedGame(int gameID, int numPlayers, ArrayList<String> players, Map<String,Color> totemColors) {
        createLocalModel(gameID, numPlayers);
        for (String playerName : totemColors.keySet()) {
            localModel.addPlayer(playerName);
            localModel.chosenTotemColor(playerName, totemColors.get(playerName));
        }
        clientState = ClientState.IN_LOBBY;
        view.showSuccessfullyJoinedGame(gameID, players, totemColors);
    }

    /**
     * Updates every player when a player leaves the game lobby. If it's this player it sets its local model to null
     * and sets its client state to SETUP, otherwise it removes it from its local model's list of players.
     * @param player the player who left the lobby.
     */
    @Override
    public void updatePlayerLeftGame(String player) {
        if(this.playerName.equals(player)){
            clientState = ClientState.SETUP;
            view.showPlayerLeftLobby(player,null);
            localModel = null;
        }
        else {
            // rimuove il player dalla mappa di colori del model
            Color oldColor = localModel.getTotemColors().get(player);
            localModel.updatePlayerLeft(player);
            view.showPlayerLeftLobby(player, oldColor);
        }
    }

    /**
     * Notifies the view when the host leaves and the player is chosen to be the new host.
     */
    @Override
    public void updateNewHost() {
        view.showNewHost();
    }


    /**
     * It updates every player about the card drawn by a player. It updates the respective row removing the card and
     * decrements the available draws of the player. If the player has no more available draws, it moves his totem back
     * to the respective slot of the turn tile and updates the next player. It catches a {@link LastPlayerOfTurnException}
     * if this player was the last of this game phase. If it was, it checks if there's some player who can draw
     *      * an additional card from the top row.
     * @param fromTopRow true if the card drawn is from the top row, false if it's from bottom.
     * @param fromBuilding true if the card drawn comes from the buildings' row, false if otherwise.
     * @param index the index of the array of the respective row.
     * @param playerName the name of the player who drew the card.
     */
    @Override
    public void updateCardDrawn(boolean fromTopRow, boolean fromBuilding, int index, String playerName){
        Card drawn;
        LightTribe tribe = localModel.getPlayerTribe(playerName);
        if(fromTopRow){
            tribe.decrementRemainingAbove();
            if(fromBuilding){
                drawn = localModel.getTopBuildings().remove(index);
                localModel.getPlayerTribe(playerName).addBuilding((BuildingCard) drawn);
            }else{
                drawn = localModel.getTopRow().remove(index);
                localModel.getPlayerTribe(playerName).addCharacter((CharacterCard) drawn);
            }
        }else{
            tribe.decrementRemainingBelow();
            if(fromBuilding){
                drawn = localModel.getBottomBuildings().remove(index);
                localModel.getPlayerTribe(playerName).addBuilding((BuildingCard) drawn);
            }else{
                drawn = localModel.getBottomRow().remove(index);
                localModel.getPlayerTribe(playerName).addCharacter((CharacterCard) drawn);
            }
        }
        view.showCardDrawn(playerName, drawn, fromTopRow, fromBuilding);

        // If the player still has cards to draw from the top row, but the row is empty, and he cannot buy any
        // building from it, then it sets its remaining draws from above to 0
        boolean found = false;
        if (tribe.getRemainingAbove() > 0) {
            for (Card card : localModel.getTopRow()) {
                if (!(card instanceof EventCard)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                int minCost = 20;
                for (BuildingCard buildingCard : localModel.getTopBuildings()){
                    if (buildingCard.getCost() < minCost) minCost = buildingCard.getCost();
                }
                if (minCost == 20 || tribe.getFoodReserve() < minCost-tribe.getBuildersDiscount())
                    tribe.setRemainingDraws(0, tribe.getRemainingBelow());
            }
        }

        // If the player still has cards to draw from the bottom row, but the row is empty, and he cannot buy any
        // building from it, then it sets its remaining draws from below to 0
        if (tribe.getRemainingBelow() > 0) {
            found = false;
            for (Card card : localModel.getBottomRow()) {
                if (!(card instanceof EventCard)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                int minCost = 20;
                for (BuildingCard buildingCard : localModel.getBottomBuildings()){
                    if (buildingCard.getCost() < minCost) minCost = buildingCard.getCost();
                }
                if (minCost == 20 || tribe.getFoodReserve() < minCost-tribe.getBuildersDiscount())
                    tribe.setRemainingDraws(tribe.getRemainingAbove(), 0);
            }
        }

        //System.out.println("DEBUG: Card drawn by " + playerName + ". Remaining draws: " + tribe.getRemainingAbove() + "/" + tribe.getRemainingBelow());
        if (tribe.getRemainingAbove() == 0 && tribe.getRemainingBelow() == 0) {
            //System.out.println("DEBUG: Player " + playerName + " finished draws. Moving to turn tile.");
            if (localModel.getTurnOrder().size() == localModel.getNumPlayers())
                localModel.moveTotemToTurnTile(playerName);

            String additionalDrawPlayer = null;
            if (localModel.getTurnOrder().size() > localModel.getNumPlayers()) {
                additionalDrawPlayer = localModel.getCurrentPlayer();
                localModel.getTurnOrder().removeLast();
                localModel.setCurrentPlayer(localModel.getTurnOrder().getLast());
            }

            try {
                updateCurrentPlayer();
            } catch (LastPlayerOfTurnException e) {
                if (!checkAdditionalDraw()) {
                    if (additionalDrawPlayer != null)
                        localModel.getPlayerTribe(additionalDrawPlayer).setCanDrawAdditional(true);
                    localModel.setCurrentPlayer("");
                }
            }
        }
    }

    /**
     * Updates the players when a player decided to pass his turn because he couldn't draw any more cards during his
     * turn in the drawing game phase. It then updates the next player. It catches a {@link LastPlayerOfTurnException}
     * if this player was the last of this game phase. If it was, it checks if there's some player who can draw
     * an additional card from the top row.
     * @param playerName the name of the player who passed his turn.
     */
    @Override
    public void updateTurnPassed(String playerName){

        localModel.getPlayerTribe(playerName).setRemainingDraws(0, 0);
        if (localModel.getTurnOrder().size() == localModel.getNumPlayers())
            localModel.moveTotemToTurnTile(playerName);

        view.showTurnPassed(playerName, localModel.getCurrentPlayer());

        String additionalDrawPlayer = null;
        if (localModel.getTurnOrder().size() > localModel.getNumPlayers()) {
            additionalDrawPlayer = localModel.getCurrentPlayer();
            localModel.getTurnOrder().removeLast();
            localModel.setCurrentPlayer(localModel.getTurnOrder().getLast());
        }

        try {
            updateCurrentPlayer();
        } catch (LastPlayerOfTurnException e) {
            if (!checkAdditionalDraw()) {
                if (additionalDrawPlayer != null)
                    localModel.getPlayerTribe(additionalDrawPlayer).setCanDrawAdditional(true);
                localModel.setCurrentPlayer("");
            }
        }
    }

    /**
     * This internal method checks if, after all players drew their card, someone can draw an additional card applying
     * the {@link it.polimi.ingsw.Model.BuildingsManagement.Buildings.DrawAdditionalCard} building. If there is and if
     * there are still cards they can draw from the top row, they are set as the current player and they're notified.
     * @return true if the building's effect can be applied, false if not.
     */
    private boolean checkAdditionalDraw() {
        String additionalDrawPlayer = null;

        for (String player : localModel.getTurnOrder()) {
            if (localModel.getPlayerTribe(player).getCanDrawAdditional()) {
                additionalDrawPlayer = player;
                break;
            }
        }
        if (additionalDrawPlayer != null) {
            LightTribe tribe = localModel.getPlayerTribe(additionalDrawPlayer);
            // If the player still has cards to draw from the top row, but the row is empty, and he cannot buy any
            // building from it, then it sets its remaining draws from above to 0
            boolean found = false;
            int minCost = 20;
            for (Card card : localModel.getTopRow()) {
                if (!(card instanceof EventCard)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                for (BuildingCard buildingCard : localModel.getTopBuildings()){
                    if (buildingCard.getCost() < minCost) minCost = buildingCard.getCost();
                }
            }
            if (found || (minCost != 0 && tribe.getFoodReserve() >= minCost - tribe.getBuildersDiscount())) {
                tribe.setCanDrawAdditional(false);
                localModel.getTurnOrder().add(additionalDrawPlayer);
                localModel.getPlayerTribe(additionalDrawPlayer).setRemainingDraws(1,0);
                localModel.setCurrentPlayer(additionalDrawPlayer);
                syncClientState();
                view.showNewCurrentPlayer(additionalDrawPlayer,this.clientState);
                return true;
            }
        }
        return false;
    }

    /**
     * The client controller updates the local model adding the choice of the totem color by a player.
     * @param playerName the name of the player who chose the totem color.
     * @param totemColor the {@link Color} enum value of the totem color chosen.
     */
    @Override
    public void updateTotemColor(String playerName, Color totemColor) {
        if (clientState == ClientState.IN_LOBBY) {
            localModel.chosenTotemColor(playerName, totemColor);
            view.showChosenTotemColor(playerName, totemColor);
        }
    }

    /**
     * Internal method of the client controller. It's called by the gameStartedUpdate method.
     * It updates the local model with the food received by every player at the start of the game. It then calls the
     * update method to start the first round.
     * @param initialFood It maps from the player name to the food received.
     */
    private void updateInitialFood(Map<String, Integer> initialFood) {
        for (String player : initialFood.keySet()) {
            localModel.getPlayerTribe(player).modifyFood(initialFood.get(player));
        }
        view.showInitialFood(initialFood);
        updateStartRound(Collections.emptyMap());
    }

    /**
     * It updates the players when a new round is starting. It sets the game phase to START_TURN and increase the current
     * round value. Then, it notifies the view about the results of the events who occurred during the end of the previous
     * round, if there were any. If the previous round wasn't the last one, it notifies the view about the starting of
     * the new round, then it updates the next player who will be the first to place its totem at the start of this round.
     * @param lastEventsResults a map containing the result of the events resolved the previous round. It maps from the
     *                          {@link EventType} to an array of {@link PlayerEventResults}, containing the name of the
     *                          player and the food taken and prestige points gained or lost during that event.
     */
    @Override
    public void updateStartRound(Map<EventType, ArrayList<PlayerEventResults>> lastEventsResults) {
        localModel.updateGamePhase(GamePhase.START_TURN);
        localModel.updateCurrentRound(localModel.getCurrentRound() + 1);
        localModel.setCurrentPlayer("");

        // UPDATING EVENTS RESULTS using deltas
        if (!lastEventsResults.isEmpty()) {
            System.out.println(lastEventsResults);
            for (EventType eventType : lastEventsResults.keySet()) {
                for(PlayerEventResults playerResults : lastEventsResults.get(eventType)){
                    LightTribe playersTribe = localModel.getPlayerTribe(playerResults.player());
                    int foodDelta = playerResults.foodAndPP()[0];
                    int ppDelta = playerResults.foodAndPP()[1];

                    // player's notification (only for the local player's view)
                    if (playerResults.player().equals(this.playerName)) {
                        view.showEvent(eventType, foodDelta, ppDelta);
                    }

                    // tribe's updating by ADDING deltas
                    playersTribe.modifyFood(foodDelta);
                    playersTribe.modifyPrestigePoints(ppDelta);
                    System.out.println("[DEBUG] Player " + playerResults.player() + "was added " + foodDelta + " food and " +  ppDelta + "pp because of event " + eventType.toString() + ". Now has " + localModel.getPlayerTribe(playerResults.player()).getFoodReserve() + " food and " + localModel.getPlayerTribe(playerResults.player()).getPrestigePoints() + " prestige points");
                }
            }
        }

        //ensures all players are on the turn tile at the start of the round
        for (int i = 0; i < localModel.getTurnOrder().size(); i++) {
            localModel.getTurnTileStatus().put(i, localModel.getTurnOrder().get(i));
        }

        if (localModel.getCurrentRound() <= 10) {
            view.showStartRound(localModel.getCurrentRound());
            updateCurrentPlayer();
        }
    }

    /**
     * It updates every player when a player has placed his totem on a free offer tile. It notifies the view and then
     * updates the next player. It catches a {@link LastPlayerOfTurnException} if this was the last player of this turn.
     * If it was, it computes the new turn order based on the players' totems positions, it updates the game phase, and
     * notifies the view about the new game phase.
     * @param playerName the name of the player who placed it totem.
     * @param index the index of the offer tile where the totem has been placed.
     */
    @Override
    public void updateCurrentOfferTile(String playerName, int index) {
        localModel.chosenOfferTile(playerName, index);
        view.showTileChosen(playerName, index);
        try {
            updateCurrentPlayer();
        } catch (LastPlayerOfTurnException e) {
            localModel.computeNewTurnOrder();
            localModel.updateGamePhase(GamePhase.ON_DRAW);
            view.showNewGamePhase(localModel.getCurrentPhase());

            localModel.setCurrentPlayer(""); //reset
            try {
                updateCurrentPlayer(); //skip tile A
            } catch (LastPlayerOfTurnException e1) {
                localModel.setCurrentPlayer("");
            }
        }
    }

    /**
     * It updates the players' local models with the food tokens gained or lost by a player.
     * @param playerName the name of the player.
     * @param food the amount of food tokens gained or lost.
     */
    @Override
    public void updateFoodReserve(String playerName, int food) {
        localModel.updateFoodReserve(playerName, food);
        System.out.println("[DEBUG] updateFoodReserve for player " + playerName + ", +" + food + " food. Now has " + localModel.getPlayerTribe(playerName).getFoodReserve());
    }

    /**
     * It updates the players' local models with the stars gained by a player when drawing a shaman card.
     * @param playerName the name of the player.
     * @param stars the amount of stars gained.
     */
    @Override
    public void updateShamansStars(String playerName, int stars) {
        localModel.updateShamansStars(playerName, stars);
    }

    /**
     * It updates the players' local models with the prestige points gained or lost by a player.
     * @param playerName the name of the player.
     * @param pp the amount of prestige points gained or lost.
     */
    @Override
    public void updatePrestigePoints(String playerName, int pp) {
        localModel.updatePrestigePoints(playerName, pp);
        System.out.println("[DEBUG] updatePrestigePoints for player " + playerName + ", +"  + pp + " prestige points. Now has " +  localModel.getPlayerTribe(playerName).getPrestigePoints());
    }

    /**
     * It updates the players' local models with the builder's discount gained when drawing a builder card.
     * @param playerName the name of the player.
     * @param discount the amount of food discount gained.
     */
    @Override
    public void updateBuildersDiscount(String playerName, int discount) {
        localModel.updateBuildersDiscount(playerName, discount);
    }

    /**
     * It updates the players' local models with the gatherer's discount gained when drawing a gatherer card.
     * @param playerName the name of the player.
     * @param discount the amount of food discount gained.
     */
    @Override
    public void updateGatherersDiscount(String playerName, int discount) {
        localModel.updateGatherersDiscount(playerName, discount);
    }

    /**
     * It updates the players' local models with the new top row when a new round has started.
     * @param newTopRow the array containing the cards (characters or events) of the new top row.
     */
    @Override
    public void updateTopRow(ArrayList<Card> newTopRow) {
        localModel.updateTopRow(newTopRow);
    }

    /**
     * It updates the players' local models with the new top building row when a new round has started.
     * @param newTopBuildings the array containing the building of the new top building row.
     */
    @Override
    public void updateTopBuildings(ArrayList<BuildingCard> newTopBuildings) {
        localModel.updateTopRowBuildings(newTopBuildings);
    }

    /**
     * It updates the players' local models with the new bottom row when a new round has started.
     * @param newBottomRow the array containing the cards (characters or events) of the new bottom row.
     */
    @Override
    public void updateBottomRow(ArrayList<Card> newBottomRow) {
        localModel.updateBottomRow(newBottomRow);
    }

    /**
     * It updates the players' local models with the new bottom building row when a new round has started.
     * @param newBottomBuildings the array containing the building of the new bottom building row.
     */
    @Override
    public void updateBottomBuildings(ArrayList<BuildingCard> newBottomBuildings) {
        localModel.updateBottomRowBuildings(newBottomBuildings);
    }

    /**
     * This method manages the turning logic, deciding which player has to play next. If the game is in the drawing
     * phase, it also manages the player who placed his totem on tile A, giving him the good bonus and immediately
     * returning his totem to the Turn Tile. It then tries calling seyNextPlayer of the local model. If the last player
     * was the last one of the turn order, it catches a {@link LastPlayerOfTurnException}, rethrowing it immediately
     * to updateDrawCard or updateCurrentOfferTile, depending on the game phase.
     * After setting the next player, it calls the method to sync this player client state. Lastly, it notifies the view
     * about the new current player.
     */
    public void updateCurrentPlayer() {
        String nextPlayer = "";
        boolean playerFound = false;

        while (!playerFound) {
            try {
                nextPlayer = localModel.setNextPlayer();

                //skip for TILE A ON_DRAW
                if (localModel.getCurrentPhase() == GamePhase.ON_DRAW) {
                    LightTribe tribe = localModel.getPlayerTribe(nextPlayer);
                    if (tribe != null && tribe.getRemainingAbove() == 0 && tribe.getRemainingBelow() == 0) {
                        int foodBonus = localModel.getOfferTiles().get(localModel.getTurnOrder().indexOf(nextPlayer)).getFoodBonus();
                        localModel.moveTotemToTurnTile(nextPlayer);
                        view.showFoodBonusTile(nextPlayer, foodBonus);
                        continue; //find NEXT player
                    }
                }
                playerFound = true;
            } catch (LastPlayerOfTurnException e) {
                //localModel.setCurrentPlayer("");
                throw new LastPlayerOfTurnException();
            }
        }

        syncClientState();
        if (!nextPlayer.isEmpty())
            view.showNewCurrentPlayer(nextPlayer, this.clientState);
    }

    /**
     * It updates the local model with the new game phase. It then notifies the view.
     * @param phase the {@link GamePhase} value of the new game phase.
     */
    @Override
    public void updateGamePhase(GamePhase phase) {
        localModel.updateGamePhase(phase);
        syncClientState();
        view.showNewGamePhase(phase);
    }

    /**
     * It updates the local model about the new era when it changes.
     * @param era the number indicating the new era.
     */
    @Override
    public void updateCurrentEra(int era) {
        localModel.updateEra(era);
        view.showEraChanged(era);
    }

    /**
     * It updates the players when the game comes to its end. It updates the final prestige points of every player.
     * After this, it notifies the view with the final ranking and all the players are brought back to the setup state.
     * @param finalRanking a map containing all the players' names sorted by decrescent amount of prestige points.
     */
    @Override
    public void updateEndGame(Map<String, Integer> finalRanking) {
        localModel.updateGamePhase(GamePhase.END_GAME);
        this.clientState = ClientState.END_GAME;
        for (String playerName : finalRanking.keySet()) {
            localModel.updatePrestigePoints(playerName, finalRanking.get(playerName));
        }
        view.showEndGame(finalRanking);
    }

    /**
     * Notifies the player about their position in the general leaderboard for games with the same number of players
     * as this one.
     * @param leaderboard a list of Strings representing the leaderboard with the final scores of all the game played
     *                    with the same number of players.
     * @param playerPosition the player's position in the leaderboard.
     */
    @Override
    public void updateLeaderboardInfo(List<String> leaderboard, int playerPosition) {
        localModel.getDbLeaderboard().addAll(leaderboard);
        view.showLeaderboardInfo(playerPosition);
    }


    /**
     * This method is used to sync the {@link ClientState} of the player based on the {@link GamePhase} and on the current
     * player. If the player is not the next player to play, his client state is set to NOT_IN_TURN, else it's set
     * either to PLACE_TOTEM or DRAW_CARD based on the current game phase.
     */
    private void syncClientState() {
        //System.out.println("DEBUG: current phase: " +  localModel.getCurrentPhase());
        String currentPlayer = localModel.getCurrentPlayer();
        if (currentPlayer == null || currentPlayer.isEmpty() || !this.playerName.equals(currentPlayer)) {
            setClientState(ClientState.NOT_IN_TURN);
            return;
        }

        switch (localModel.getCurrentPhase()){
            case START_GAME:
                break;
            case START_TURN:
                setClientState(ClientState.PLACE_TOTEM);
                break;
            default:
                setClientState(ClientState.DRAW_CARD);
                break;
        }
    }

    /**
     * This method handles the disconnection of a player, either because of a general server disconnection or because of
     * the lost connection of a specific player. If the player disconnected while in lobby, the player is removed from
     * the local model, else the game is interrupted and all the players are brought back to the setup state.
     * It then notifies the view about the disconnection.
     * @param disconnectedPlayer the name of the player who disconnected.
     */
    public void handleServerDisconnection(String disconnectedPlayer){
        //If still in lobby and player disconnects
        Color totemColor = localModel.getTotemColors().get(disconnectedPlayer);
        if(this.clientState == ClientState.IN_LOBBY){
            localModel.getTotemColors().remove(disconnectedPlayer);
        }
        else{
            setClientState(ClientState.SETUP);
            localModel = null;
        }
        view.showForceQuit(disconnectedPlayer, totemColor);
    }
}