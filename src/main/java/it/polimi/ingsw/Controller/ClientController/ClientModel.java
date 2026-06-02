package it.polimi.ingsw.Controller.ClientController;

import it.polimi.ingsw.CustomException.IllegalDrawException;
import it.polimi.ingsw.CustomException.LastPlayerOfTurnException;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.GameBoard.OfferTile;
import it.polimi.ingsw.Model.Users.DrawableCardVisitor;

import java.util.*;

/**
 * This is a light model for the client to be accessed. It is updated when the model state is modified. Update methods
 * are called by the Client controllers.
 * It keeps trace of everything a player may want to know, like cards rows, players' tribes, current round and era,
 * player in turn, etc.
 */
public class ClientModel {
    private final int gameId;
    private final int numPlayers;
    private final Map<String, LightTribe> players;
    private volatile GamePhase currentPhase;
    private int currentRound;
    private volatile String currentPlayer;
    private volatile int era;
    private ArrayList<Card> topRow;
    private ArrayList<Card> bottomRow;
    private ArrayList<BuildingCard> topBuildings;
    private ArrayList<BuildingCard> bottomBuildings;
    private final Map<String, Color> totemColors;
    List<String> turnOrder;
    Map<Integer,String> turnTileStatus;
    private int[] tileModifier;
    private ArrayList<OfferTile> offerTiles;
    private List<String> dbLeaderboard;

    ClientModel(int gameId, int numPlayers){
        this.gameId = gameId;
        this.numPlayers = numPlayers;
        this.players = new HashMap<>();
        this.currentPhase = GamePhase.START_GAME;
        this.currentRound = 0;
        this.currentPlayer = "";
        this.turnOrder = new ArrayList<>();
        this.topRow = new ArrayList<>();
        this.bottomRow = new ArrayList<>();
        this.topBuildings = new ArrayList<>();
        this.bottomBuildings = new ArrayList<>();
        this.totemColors = new HashMap<>();
        turnTileStatus = new HashMap<>();
        turnOrder = new ArrayList<>();
        dbLeaderboard = new ArrayList<>();
        switch (numPlayers){
            case 2:
                tileModifier = new int[]{1, -1};
                turnTileStatus.put(0, null);
                turnTileStatus.put(1, null);
                break;
            case 3:
                tileModifier = new int[]{2, 0, -1};
                turnTileStatus.put(2, null);
                break;
            case 4:
                tileModifier = new int[]{2, 1, 0, -1};
                turnTileStatus.put(3, null);
                break;
            case 5:
                tileModifier = new int[]{3, 1, 0, 0, -1};
                turnTileStatus.put(4, null);
        }
        this.offerTiles = new ArrayList<>();
        if(numPlayers == 5) offerTiles.add(new OfferTile('A'));
        offerTiles.add(new OfferTile('B'));
        offerTiles.add(new OfferTile('C'));
        if(numPlayers >= 3) offerTiles.add(new OfferTile('D'));
        offerTiles.add(new OfferTile('E'));
        offerTiles.add(new OfferTile('F'));
        if(numPlayers >= 4) offerTiles.add(new OfferTile('G'));
    }

    /*drawable è void in quanto la gestione del caso negativo (la carta non è pescabile)
    avviene attraverso le exception, lanciate dal metodo accept(visitor)
    solo se l'edificio non è alla portata del player o la carta è un evento.
    se finsce il metodo implicitamente non ha lanciato eccezioni e il metodo
    drawCard di clientController può procedere senza problemi.
     */

    /**
     * It checks if the card chosen by the player is drawable or not.
     * @param fromTopRow true if it's from top row, false if it's from bottom.
     * @param fromBuildings true if it's from the buildings' row, false if otherwise.
     * @param index the index of the array of cards chosen.
     * @throws IllegalDrawException if the card chosen is not drawable, if the row chosen is empty or if it catches
     * a {@link IndexOutOfBoundsException} because the index inserted is invalid.
     */
    public void drawable(boolean fromTopRow, boolean fromBuildings, int index) throws IllegalDrawException {
        LightTribe tribe = players.get(currentPlayer);
        if (fromTopRow && tribe.getRemainingAbove() <= 0) {
            throw new IllegalDrawException("No more draws allowed from top row for " + totemColors.get(currentPlayer).colorize(currentPlayer));
        }
        if (!fromTopRow && tribe.getRemainingBelow() <= 0) {
            throw new IllegalDrawException("No more draws allowed from bottom row for " + totemColors.get(currentPlayer).colorize(currentPlayer));
        }

        Card card;
        DrawableCardVisitor visitor = new DrawableCardVisitor(this);
        try {
            if(fromBuildings){
                card = fromTopRow ? getTopBuildings().get(index)
                        : getBottomBuildings().get(index);
            }
            else {
                card = fromTopRow ? getTopRow().get(index)
                        : getBottomRow().get(index);
            }
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalDrawException("Index " + index + " is out of bounds for the selected row.");
        }
        card.accept(visitor);   //throws IllegalDraw and InsufficientFood
    }

    /**
     * Checks if the offer tile is occupied by a player.
     * @param index the index of the offer tile on the offer track.
     * @return true if it's occupied, false if not.
     */
    public boolean isOccupied(int index) { return offerTiles.get(index).isOccupied(); }

    /**
     * It moves a player totem from the offer tile back to the respective slot of the turn tile, freeing the offer tile.
     * @param playerName the name of the player.
     */
    public void moveTotemToTurnTile(String playerName) {
        for (OfferTile tile : offerTiles) {
            if (playerName.equals(tile.getCurrentOccupant())) {
                tile.free();
                break;
            }
        }
        //also put them back on the turn tile
        int playerIdx = turnOrder.indexOf(playerName);
        if (playerIdx != -1) {
            turnTileStatus.put(playerIdx, playerName);
        }
    }

    /**
     * It computes the new turn order after the end of the totem placing game phase, based on the location of the
     * totems on the Offer Track.
     */
    public void computeNewTurnOrder() {
        this.turnOrder.clear();
        for (OfferTile offerTile : offerTiles) {
            if(offerTile.isOccupied())  {
                String occupant = offerTile.getCurrentOccupant();
                this.turnOrder.add(occupant);
                players.get(occupant).setRemainingDraws(offerTile.getCardsFromAbove(), offerTile.getCardsFromBelow());
            }
        }
    }


    public void setTurnOrder(List<String> turnOrder) {
        this.turnOrder.clear();
        this.turnOrder.addAll(turnOrder);
    }

    public void setCurrentPlayer(String playerName) {
        this.currentPlayer = (playerName == null) ? "" : playerName;
    }

    /**
     * It sets the next player of the current turn order.
     * @return the name of the next player.
     * @throws LastPlayerOfTurnException if the previous player was the last of that turn order-
     */
    public String setNextPlayer() throws LastPlayerOfTurnException {
        if (turnOrder == null || turnOrder.isEmpty()) {
            this.currentPlayer = "";
            throw new LastPlayerOfTurnException();
        }

        int nextIndex;
        if (currentPlayer == null || currentPlayer.isEmpty()) {
            nextIndex = 0;
        } else {
            int currentIndex = turnOrder.indexOf(currentPlayer);
            if (currentIndex == -1 || currentIndex >= turnOrder.size() - 1) {
                this.currentPlayer = "";
                throw new LastPlayerOfTurnException();
            }
            nextIndex = currentIndex + 1;
        }

        this.currentPlayer = turnOrder.get(nextIndex);
        return this.currentPlayer;
    }

    /**
     * It adds a player to the local model when they join the lobby, creating an instance of their light tribe.
     * @param playerName the name of the player.
     */
    void addPlayer(String playerName){
        if(players.size() <= numPlayers){
            LightTribe lightTribe = new LightTribe(playerName);
            players.put(playerName, lightTribe);
        }
    }

    /**
     * It adds the players to the local model and instantiates a light tribe for each of them.
     * @param newPlayers the list of the game's players' names.
     */
    void addPlayersTribes(List<String> newPlayers){
        for (String player : newPlayers) {
            players.put(player, new LightTribe(player));
        }
    }

    /**
     * It removes the player from the list of players and from the colors map.
     * @param playerName
     */
    void updatePlayerLeft(String playerName){
        totemColors.remove(playerName);
        players.remove(playerName);
    }


    /*
    * Methods that update values of a player's tribe.
     */
    void updateFoodReserve(String playerName, int food) {
        players.get(playerName).modifyFood(food);
    }
    void updatePrestigePoints(String playerName, int pp) {
        players.get(playerName).modifyPrestigePoints(pp);
    }
    void updateShamansStars(String playerName, int stars) {
        players.get(playerName).setShamansStars(stars);
    }
    void updateBuildersDiscount(String playerName, int discount) {
        players.get(playerName).setBuilderDiscount(discount);
    }
    void updateGatherersDiscount(String playerName, int discount) {
        players.get(playerName).setGathererDiscount(discount);
    }

    //aggiungere metodo clearOfferTile(), in player c'è moveTotemToTurnTile e in OfferTile c'è free, decidere cosa fare
    void updateOfferTiles(ArrayList<OfferTile> remoteOfferTile){
        offerTiles = remoteOfferTile;
    }
    void updateTopRow(ArrayList<Card> newTopRow) {
        this.topRow = newTopRow;
    }
    void updateBottomRow(ArrayList<Card> newBottomRow) {
        this.bottomRow = newBottomRow;
    }
    void updateTopRowBuildings(ArrayList<BuildingCard> newTopRowBuildings) {
        this.topBuildings = newTopRowBuildings;
    }
    void updateBottomRowBuildings (ArrayList<BuildingCard> newBottomRowBuildings) {
        this.bottomBuildings = newBottomRowBuildings;
    }
    void chosenTotemColor(String playerName, Color color){
        totemColors.put(playerName, color);
    }

    void chosenOfferTile(String playerName, int index){
        offerTiles.get(index).occupy(playerName);
        int playerIdx = this.turnOrder.indexOf(playerName);
        if (playerIdx != -1) {
            turnTileStatus.put(playerIdx, "");
        }
    }

    void updateCurrentRound(int newCurrentRound){
        currentRound = newCurrentRound;
    }
    void updateEra(int newEra){
        era = newEra;
    }
    void updateGamePhase(GamePhase phase){
        currentPhase = phase;
    }

    /*
    * Getters
     */
    public int getGameId(){return gameId;}
    public int getNumPlayers(){return numPlayers;}
    public LightTribe getPlayerTribe(String playerName){return players.get(playerName);}
    public Set<String> getPlayersNames(){
        return players.keySet();
    }
    public List<String> getTurnOrder(){
        return turnOrder;
    }
    public GamePhase getCurrentPhase(){
        return currentPhase;
    }
    public int getCurrentRound(){return currentRound;}
    public String getCurrentPlayer(){return currentPlayer;}
    public int getEra(){return era;}
    public ArrayList<Card> getTopRow(){return topRow;}
    public ArrayList<Card> getBottomRow(){return bottomRow;}
    public ArrayList<BuildingCard> getTopBuildings(){return topBuildings;};
    public ArrayList<BuildingCard> getBottomBuildings(){return bottomBuildings;};
    public Color getColors(String playerName){return totemColors.get(playerName);}
    public boolean isColorAvailable(Color color){
        return !totemColors.containsValue(color);
    }
    public Map<String,Color> getTotemColors(){return totemColors;}
    public int[] getTileModifier() {return tileModifier;}
    public Map<Integer, String> getTurnTileStatus() {return turnTileStatus;}
    public ArrayList<OfferTile> getOfferTiles(){return offerTiles;}
    public List<String> getDbLeaderboard(){return dbLeaderboard;}
}