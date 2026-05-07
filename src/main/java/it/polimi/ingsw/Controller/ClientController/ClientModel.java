package it.polimi.ingsw.Controller.ClientController;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.GameBoard.OfferTile;
import it.polimi.ingsw.Model.GameBoard.TurnTile;
import it.polimi.ingsw.Model.Users.DrawableCardVisitor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
    private final Map<String, Character> currentOfferTiles;
    private TurnTile turnTile;
    private ArrayList<OfferTile> offerTiles;
    //lightTribe, qui non esiste player solo il suo id!!!!!!!!!!

    ClientModel(int gameId, int numPlayers){
        this.gameId = gameId;
        this.numPlayers = numPlayers;
        this.players = new HashMap<>();
        this.currentPhase = GamePhase.START_GAME;
        this.currentRound = 0;
        this.currentOfferTiles = new HashMap<>();
        this.topRow = new ArrayList<>();
        this.bottomRow = new ArrayList<>();
        this.topBuildings = new ArrayList<>();
        this.bottomBuildings = new ArrayList<>();
        this.totemColors = new HashMap<>();
        this.turnTile = new TurnTile(numPlayers);
        this.offerTiles = new ArrayList<>();
    }

    /*drawable è void in quanto la gestione del caso negativo (la carta non è pescabile)
    avviene attraverso le exception, lanciate dal metodo accept(visitor)
    solo se l'edificio non è alla portata del player o la carta è un evento.
    se finsce il metodo implicitamente non ha lanciato eccezioni e il metodo
    drawCard di clientController può procedere senza problemi.
     */
    public void drawable(boolean fromTopRow, boolean fromBuildings, int index) {
        Card card;
        DrawableCardVisitor visitor = new DrawableCardVisitor(this);
        if(fromBuildings){
            card = fromTopRow ? getTopBuildings().get(index)
                    : getBottomBuildings().get(index);
        }
        else {
            card = fromTopRow ? getTopRow().get(index)
                    : getBottomRow().get(index);
        }
        card.accept(visitor);   //throws IllegalDraw and InsufficientFood
    }

    public boolean isOccupied(int index) { return offerTiles.get(index).isOccupied(); }

    //update methods
    void setNextPlayer(String playerName) {
        currentPlayer = playerName;
    }
    void setNextRound() {
        currentRound++;
    }
    void addPlayer(String playerName){
        if(players.size()<=numPlayers){
            LightTribe lightTribe = new LightTribe(playerName);
            players.put(playerName, lightTribe);
            currentOfferTiles.put(playerName, null);
        }
    }
    void removePlayer(String name){
        players.remove(name);
    }


    void updateFoodReserve(String playerName, int food) {
        players.get(playerName).addFood(food);
    }
    void updatePrestigePoints(String playerName, int pp) {
        players.get(playerName).addPrestigePoints(pp);
    }
    void updateShamansStars(String playerName, int stars) {
        players.get(playerName).addShamansStars(stars);
    }

    //aggiungere metodo clearOfferTile(), in player c'è freeOfferTile e in OfferTile c'è free, decidere cosa fare
    void updateOfferTile(String playerName, Character index) {
        currentOfferTiles.put(playerName, index);
    }
    void updateTurnTile(TurnTile remoteTurnTile){
        turnTile = remoteTurnTile;
    }
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
        players.get(playerName).addToPopulation(character);
    }
    void chosenOfferTile(String playerName, int index){
        offerTiles.get(index).occupy(playerName);
    }
    void updateBuildingDrawn(BuildingCard building, String playerName){
        //players.get(playerName).addToBuildings(building);
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
        return totemColors.containsValue(color);
    }
    public Map<String,Color> getTotemColors(){return totemColors;}
    public int getOfferTiles(String playerName){return currentOfferTiles.get(playerName);}
    public TurnTile getTurnTile(){return turnTile;}
    public ArrayList<OfferTile> getOfferTilesNumber(){return offerTiles;}
}