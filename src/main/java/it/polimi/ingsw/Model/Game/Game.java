package it.polimi.ingsw.Model.Game;

import java.util.*;
import java.util.stream.Collectors;

import it.polimi.ingsw.CustomException.*;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Deck.Deck;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Model.EventManagement.PlayerEventResults;
import it.polimi.ingsw.Model.Users.*;
import it.polimi.ingsw.Model.GameBoard.OfferTrack;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Controller.GameController;
import it.polimi.ingsw.Model.EventManagement.EventManager;

/**
 * This is the main class of the game, representing the model and containing the references to all other model
 * components. Its instance is stored in the corresponding {@link GameController}, which uses it to access and modify
 * the current state of the model. This class is instantiated in the Server Controller when a player creates a new game,
 * and it is passed to the Game Controller's constructor to associate it to this Game instance.
 */
public class Game {
    /**
     * This variable is set to true when al players have joined and are ready.
     */
    private boolean readyToStart;

    /**
     * True if the game has started, false if not.
     */
    private boolean isStarted;

    /**
     * The unique ID of this game, through which this game is recognized.
     */
    private final int gameID;

    /**
     * The number of players playing this match.
     */
    private final int numPlayers;

    /**
     * The list of {@link Player}'s instances of all the players playing this match.
     */
    private final ArrayList<Player> players;

    /**
     * The current {@link Player} in turn.
     */
    private Player currentPlayer;

    /**
     * Every player is mapped to the color they have chosen for their totem, if they have already chosen it.
     */
    private final Map<String, Color> totemColors;

    /**
     * The current era of the game, updated every time it changes.
     */
    private int era;

    /**
     * The current round of the game (goes from 1 to 10).
     */
    private int currentRound;

    /**
     * The current {@link GamePhase}.
     */
    private GamePhase currentPhase;

    private OfferTrack offerTrack;
    private BuildingManager buildingManager;
    private final EventManager eventManager;
    private Deck deck;

    /**
     * The path to the json used to get all the cards.
     */
    private static final String jsonCardsPath = "json/cards.json";

    /**
     * Instance to the {@link GameController} of this game.
     */
    private GameController controller;

    public Game(int gameID, int numPlayers) {
        readyToStart = false;
        isStarted = false;
        this.gameID = gameID;
        this.numPlayers = numPlayers;
        this.players = new ArrayList<>();
        eventManager = new EventManager();
        totemColors = new HashMap<>();
    }

    public void setController(GameController controller) {
        this.controller = controller;
    }

    public GameController getController() {
        return controller;
    }

    /*
    * Getters
     */
    public boolean isReadyToStart(){
        return readyToStart;
    }
    public boolean isStarted(){
        return isStarted;
    }
    public int getGameID(){
        return gameID;
    }
    public int getNumPlayer(){return numPlayers;}
    public Player getCurrentPlayer(){
        if(isStarted){
            return currentPlayer;
        }
        throw new GameNotStartedException();
    }
    public ArrayList<String> getPlayersNames(){
        ArrayList<String> names = new ArrayList<>();
        for (Player p: players){
            names.add(p.getName());
        }
        return names;
    }
    public ArrayList<Player> getPlayers(){
        return players;
    }
    public Player getPlayerByName(String playerName){
        return players.stream().filter(p -> p.getName().equals(playerName))
                .findFirst().orElse(null);
    }
    public Set<Color> getAvailableColors(){
        Set<Color> colors = EnumSet.allOf(Color.class);
        for(Color color: totemColors.values()){
            colors.remove(color);
        }
        return colors;
    }

    public Map<String, Color> getPlayersTotemColors(){
        return totemColors;
    }
    public int getEra(){
        if(isStarted){
            return era;
        }
        throw new GameNotStartedException();
    }
    public int getCurrentRound(){
        if(isStarted){
            return currentRound;
        }
        throw new GameNotStartedException();
    }
    public GamePhase getGamePhase(){
        if(isStarted){
            return currentPhase;
        }
        throw new GameNotStartedException();
    }
    public OfferTrack getOfferTrack(){
        if(isStarted){
            return offerTrack;
        }
        throw new GameNotStartedException();
    }
    public BuildingManager getBuildingManager(){
        if(isStarted){
            return buildingManager;
        }
        throw new GameNotStartedException();
    }
    public EventManager getEventManager(){return eventManager;}
    public Deck getDeck() {
        if(isStarted){
            return deck;
        }
        throw new GameNotStartedException();
    }

    // -----------------------------------------------------------------------------------------------------------------
    // METHODS WHICH UPDATE THE MODEL STATUS ---------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------------------------

    // used for testing
    public void setEra(int era){
        this.era = era;
    }

    public void setReadyToStart(boolean readyToStart){
        this.readyToStart = readyToStart;
    }

    public void setCurrentPhase(GamePhase phase){
        currentPhase = phase;
    }

    public void chooseTotemColor(String playerName, Color totemColor){
        totemColors.put(playerName, totemColor);
    }

    public void addPlayer(String playerName) {
        Player newPlayer = new Player(this, playerName, totemColors.get(playerName));
        players.add(newPlayer);
    }

    // used for testing
    public void startGameUnshuffled(){
        isStarted = true;
        this.era = 1;
        this.currentRound = 0;
        this.currentPhase = GamePhase.START_GAME;
        this.deck = new Deck(this, jsonCardsPath);
        this.buildingManager = new BuildingManager(players);
        this.offerTrack = new OfferTrack(this, numPlayers);
        offerTrack.getTurnTile().initTurnOrderUnshuffled(players);
        giveInitialFood(numPlayers);
    }

    public Map<String,Integer> startGame(){
        isStarted = true;
        this.era = 1;
        this.era = 1;
        this.currentRound = 0;
        this.currentPhase = GamePhase.START_GAME;
        this.deck = new Deck(this, jsonCardsPath);
        this.buildingManager = new BuildingManager(players);
        this.offerTrack = new OfferTrack(this, numPlayers);
        offerTrack.getTurnTile().initTurnOrder(players);
        return new HashMap<>(giveInitialFood(numPlayers));
    }

    //actual functions
    public String setFirstPlayer(){
        currentPlayer = offerTrack.getTurnTile().getTurnOrder().getFirst();
        return currentPlayer.getName();
    }

    public Player setNextPlayer(){
        if(!isStarted){
            throw new GameNotStartedException();
        }
        ArrayList<Player> turnOrder = offerTrack.getTurnTile().getTurnOrder();

        if (currentPlayer == null) {
            this.currentPlayer = turnOrder.getFirst();
            return this.currentPlayer;
        }

        int currentPlayerIndex = turnOrder.indexOf(currentPlayer);
        if  (currentPlayerIndex < turnOrder.size()-1){
            this.currentPlayer =  turnOrder.get(currentPlayerIndex+1);
            return this.currentPlayer;
        }
        // se è finita la fase di piazzamento dei totem, si passa a pescare le carte
        else {
            if (currentPhase == GamePhase.START_TURN) {
                offerTrack.getTurnTile().updateTurnOrder();
            }
            this.currentPlayer = null;
            throw new LastPlayerOfTurnException();
        }
    }

    public void setCurrentPlayer(Player currentPlayer){
        this.currentPlayer = currentPlayer;
    }

    /**
     * Inits the rows of the offer track. If the era has changed it throws a {@link ChangeEraException}.
     */
    public void initOfferTrack() {
        if(currentRound == 1){
            offerTrack.initializeBottomRow();
            offerTrack.repopulateTopRow();
            offerTrack.repopulateTopBuildingCards();
        } else if(currentRound <= 10){
            offerTrack.moveCardsToBottom();
            offerTrack.repopulateTopRow();
        }
        for(Card card: offerTrack.getTopRow()){
            if(card.getEra() != era){
                throw new ChangeEraException();
            }
        }
    }

    /**
     * Called in "startRound()" in GameController, it returns the next round. It throws {@link LastRoundException} if
     * the last round has been played.
     * @return the next round to be played.
     */
    public int setNextRound() {
        if(currentRound == 10) {
            throw new LastRoundException("Fine del gioco raggiunta");
        }
        return currentRound++;
    }

    /**
     * It calculates the amount of Food tokens to give to every player at the start of the game based on their position
     * in the first turn order and on the number of players.
     * @param numPlayers the number of players.
     * @return a map: for every player, the amount of Food tokens they get.
     */
    private Map<String,Integer> giveInitialFood(int numPlayers){
        ArrayList<Player> turnOrder = offerTrack.getTurnTile().getTurnOrder();
        Map<String,Integer> initialFood = new HashMap<>();
        turnOrder.get(0).getTribe().modifyFood(2);
        initialFood.put(turnOrder.get(0).getName(), 2);
        turnOrder.get(1).getTribe().modifyFood(3);
        initialFood.put(turnOrder.get(1).getName(), 3);

        if(numPlayers >= 3) {
            turnOrder.get(2).getTribe().modifyFood(3);
            initialFood.put(turnOrder.get(2).getName(), 3);
        }
        if(numPlayers >= 4) {
            turnOrder.get(3).getTribe().modifyFood(4);
            initialFood.put(turnOrder.get(3).getName(), 4);
        }
        if(numPlayers >= 5) {
            turnOrder.get(4).getTribe().modifyFood(4);
            initialFood.put(turnOrder.get(4).getName(), 4);
        }
        return initialFood;
    }

    /**
     * It increases the era and repopulates the top and bottom building rows.
     */
    public void changeEra() {
        era++;
        offerTrack.moveBuildings();
        offerTrack.repopulateTopBuildingCards();
    }

    /**
     * This method is called at the end of every drawing {@link GamePhase} after the last player has returned his totem
     * back to the Turn Tile and before resolving the events. It checks if there's a player who can draw an additional
     * card from the top row by activating their DrawAdditionalCard building.
     * @return true if there's a player with that building who has not activated it yet and who has drawable cards from
     * the top row. False otherwise.
     */
    public boolean checkAdditionalDraw() {
        Player additionalDrawPlayer = null;
        for (Player player : getPlayers()) {
            if (player.getCanDrawAdditional()) {
                additionalDrawPlayer = player;
                break;
            }
        }
        if (additionalDrawPlayer != null) {
            Tribe tribe = additionalDrawPlayer.getTribe();

            // checks if the player who can draw an additional card has any Character card to draw left on the top row
            // or if there is a Building card on the top row that he can purchase.
            boolean found = false;
            int minCost = 20;
            for (Card card : getOfferTrack().getTopRow()) {
                if (!(card instanceof EventCard)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                for (BuildingCard buildingCard : getOfferTrack().getTopBuildingCard()) {
                    if (buildingCard.getCost() < minCost) minCost = buildingCard.getCost();
                }
            }
            if (found || (minCost != 20 && tribe.getFoodReserve() >= minCost-tribe.getBuildersDiscount())) {
                additionalDrawPlayer.setCanDrawAdditional(false);
                getOfferTrack().getTurnTile().getTurnOrder().add(additionalDrawPlayer);
                additionalDrawPlayer.setRemainingDraws(1, 0);
                currentPlayer = additionalDrawPlayer;
                return true;
            }
        }
        return false;
    }

    /**
     * This method is called at the end of every drawing {@link GamePhase}, to resolve the bottom Events. If the
     * current round is the last one, it also resolves the top final Events.
     * @return a map containing an array of {@link PlayerEventResults} for every Event resolved at the end of the round.
     */
    public Map<EventType, ArrayList<PlayerEventResults>> resolveEvents() {
        currentPhase = GamePhase.ON_EVENT;
        ArrayList<EventCard> events = getOfferTrack().getBottomEvents();
        // if the current round is the last one, it also resolves the top row's Events
        if (currentRound == 10)
            events.addAll(getOfferTrack().getTopEvents());
        Map<EventType, ArrayList<PlayerEventResults>> eventsResults = eventManager.resolve(events, players, buildingManager);
        System.out.println(eventsResults);
        for (EventType eventType : eventsResults.keySet()) {
            for (PlayerEventResults playerEventResults : eventsResults.get(eventType)) {
                System.out.println("cibo e pp modificati dall'evento " + eventType + " per " + playerEventResults.player() + " : " + playerEventResults.foodAndPP()[0] + " , " + playerEventResults.foodAndPP()[1]);
            }
        }
        return eventsResults;
    }

    /**
     * It occupies an Offer Tile of the {@link OfferTrack} with a new player during the totem placing {@link GamePhase}.
     * It then sets it as the current Offer Tile of the player.
     * @param player the name of the player occupying the tile.
     * @param index the index of the Offer Tile on the Offer Track.
     */
    public void chooseOfferTile(Player player, int index){
        offerTrack.getOfferTiles().get(index).occupy(player.getName());
        player.setCurrentOfferTile(offerTrack.getOfferTiles().get(index));
    }
}