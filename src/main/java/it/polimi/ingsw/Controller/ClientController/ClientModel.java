package it.polimi.ingsw.Controller.ClientController;

import it.polimi.ingsw.CustomException.IllegalDrawException;
import it.polimi.ingsw.CustomException.LastPlayerOfTurnException;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.GameBoard.OfferTile;
import it.polimi.ingsw.Model.Users.DrawableCardVisitor;

import java.util.*;

/**
 * Light model for the client to be accessed. Updated when the model state is modified. Update methods
 * are called by the Client controllers
 */
public class ClientModel {
    private final int gameId;
    private final int numPlayers;
    private final Map<String, LightTribe> players;
    private GamePhase currentPhase;
    private int currentRound;
    private String currentPlayer;
    private int era;
    private ArrayList<Card> topRow;
    private ArrayList<Card> bottomRow;
    private ArrayList<BuildingCard> topBuildings;
    private ArrayList<BuildingCard> bottomBuildings;
    private final Map<String, Color> totemColors;
    //private TurnTile turnTile;
    List<String> turnOrder;
    Map<Integer,String> turnTileStatus;
    private int[] tileModifier;
    private ArrayList<OfferTile> offerTiles;
    //lightTribe, qui non esiste player solo il suo id!!!!!!!!!!

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
        //this.turnTile = new TurnTile(numPlayers);
        turnTileStatus = new HashMap<>();
        turnOrder = new ArrayList<>();
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
    public void drawable(boolean fromTopRow, boolean fromBuildings, int index) {
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

    public boolean isOccupied(int index) { return offerTiles.get(index).isOccupied(); }

    public void freeOfferTile(String playerName) {
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

    public String setNextPlayer() {
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

    void setNextRound() {
        currentRound++;
    }
    void addPlayer(String playerName){
        if(players.size() <= numPlayers){
            LightTribe lightTribe = new LightTribe(playerName);
            players.put(playerName, lightTribe);
            //currentOfferTiles.put(playerName, null);
        }
    }
    void addPlayersTribes(List<String> newPlayers){
        for (String player : newPlayers) {
            players.put(player, new LightTribe(player));
        }
    }
    void updatePlayerLeft(String playerName){
        totemColors.remove(playerName);
        players.remove(playerName);
    }


    void updateFoodReserve(String playerName, int food) {
        players.get(playerName).setFoodReserve(food);
    }
    void updatePrestigePoints(String playerName, int pp) {
        players.get(playerName).setPrestigePoints(pp);
    }
    void updateShamansStars(String playerName, int stars) {
        players.get(playerName).addShamansStars(stars);
    }

    //aggiungere metodo clearOfferTile(), in player c'è freeOfferTile e in OfferTile c'è free, decidere cosa fare
    /*
    void updateOfferTile(String playerName, Character index) {
        currentOfferTiles.put(playerName, index);
    }
     */
//    void updateTurnTile(TurnTile remoteTurnTile){
//        turnTile = remoteTurnTile;
//    }
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
    void updateCharacterDrawn(CharacterCard character, String playerName) {
        players.get(playerName).addCharacter(character);
    }
    void chosenOfferTile(String playerName, int index){
        offerTiles.get(index).occupy(playerName);
        int playerIdx = this.turnOrder.indexOf(playerName);
        if (playerIdx != -1) {
            turnTileStatus.put(playerIdx, "");
        }
    }
    void updateBuildingDrawn(BuildingCard building, String playerName){
        players.get(playerName).addBuilding(building);
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


    //getters
    public int getGameId(){return gameId;}
    public int getNumPlayers(){return numPlayers;}
    public LightTribe getPlayerTribe(String playerName){return players.get(playerName);}
    public Set<String> getPlayersNames(){
        return players.keySet();
    }
    public boolean checkNameAvailable(String name){
        return players.containsKey(name);
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
//    public TurnTile getTurnTile(){return turnTile;}
    public ArrayList<OfferTile> getOfferTilesNumber(){return offerTiles;}
}