package it.polimi.ingsw.Model;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Model.GameBoard.OfferTile;
import it.polimi.ingsw.Model.GameBoard.TurnTile;
import it.polimi.ingsw.Model.Users.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/**
 * Light model for the client to be accessed. Updated when the model state is modified. Update methods
 * are called by the Client controllers
 */
public class ClientModel {
    private final String gameId;
    private final int numPlayers;
    private Map<String, LightTribe> players;
    private GamePhase currentPhase;
    private int currentRound;
    private String currentPlayer;
    private int era;
    private ArrayList<Card> topRow;
    private ArrayList<Card> bottomRow;
    private ArrayList<BuildingCard> topBuildingCard;
    private ArrayList<BuildingCard> bottomBuildingCard;
    private Map<String, Color> totemColors;
    private Map<String, Integer> currentOfferTiles;
    private TurnTile turnTile;
    private ArrayList<OfferTile> offerTiles;
    //lightTribe, qui non esiste player solo il suo id!!!!!!!!!!

    public ClientModel(String gameId, int numPlayers){
        this.gameId=gameId;
        this.numPlayers=numPlayers;
        this.currentPhase = GamePhase.START_GAME;
        this.currentRound = 0;
        this.currentOfferTiles = new HashMap<>();
        this.topRow = new ArrayList<>();
        this.bottomRow = new ArrayList<>();
        this.topBuildingCard = new ArrayList<>();
        this.bottomBuildingCard = new ArrayList<>();
        this.turnTile = new TurnTile(numPlayers);
        this.offerTiles = new ArrayList<>();
    }

    //da implementare con visitor pattern
    public boolean drawable(int index, int row) {
        return switch (row) {
            case 0 -> !(topRow.get(index).getCardID().equals(EventCard.class));
            case 1 -> !(bottomRow.get(index).getCardID().equals(EventCard.class));
            default -> {
                System.out.println("Invalid row exception");
                yield false;
            }
        };
    }

    public boolean isOccupied(int index) { return offerTiles.get(index).isOccupied(); }

    //update methods
    public void setNextPlayer(String playerName) {
        currentPlayer = playerName;
    }
    public void setNextRound() {
        currentRound++;
    }
    public void addPlayer(String playerName){
        if(players.size()<=numPlayers){
            LightTribe lightTribe= new LightTribe(playerName);
            players.put(playerName, lightTribe);
        }
    }


    public void updateFoodReserve(String playerName, int food) {
        players.get(playerName).addFood(food);
    }
    public void updatePrestigePoints(String playerName, int pp) {
        players.get(playerName).addPrestigePoints(pp);
    }
    public void updateShamansStars(String playerName, int stars) {
        players.get(playerName).addShamansStars(stars);
    }

    //aggiungere metodo clearOfferTile()
    public void updateOfferTile(String playerName, int index) {
        currentOfferTiles.put(playerName, index);
    }
    public void updateTurnTile(TurnTile remoteTurnTile){
        turnTile = remoteTurnTile;
    }
    public void updateOfferTiles(ArrayList<OfferTile> remoteOfferTile){
        offerTiles = remoteOfferTile;
    }
    public void updateTopRow(ArrayList<Card> newTopRow) {
        this.topRow = newTopRow;
    }
    public void updateBottomRow(ArrayList<Card> newBottomRow) {
        this.bottomRow = newBottomRow;
    }
    public void updateTopRowBuildings(ArrayList<BuildingCard> newTopRowBuildings) {
        this.topBuildingCard = newTopRowBuildings;
    }
    public void updateBottomRowBuildings (ArrayList<BuildingCard> newBottomRowBuildings) {
        this.bottomBuildingCard = newBottomRowBuildings;
    }
    public void chosenTotemColor(String playerName, Color color){
        totemColors.put(playerName, color);
    }
    public void updateCharacterDrawn(CharacterCard character, String playerName) {
        players.get(playerName).addToPopulation(character);
    }
    public void updateBuildingDrawn(BuildingCard building, String playerName){
        //players.get(playerName).addToBuildings(building);
    }
    public void updateCurrentRound(int newCurrentRound){
        currentRound = newCurrentRound;
    }
    public void updateEra(int newEra){
        era = newEra;
    }

    public void updateGamePhase(GamePhase phase){
        currentPhase = phase;
    }


    //getters
    public String getGameId(){return gameId;}
    public int getNumPlayers(){return numPlayers;}
    public LightTribe getPlayerTribe(String playerName){return players.get(playerName);}
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
    public ArrayList<BuildingCard> getTopRowBuildings(){return topBuildingCard;};
    public ArrayList<BuildingCard> getBottomRowBuildings(){return bottomBuildingCard;};
    public Color getColors(String playerName){return totemColors.get(playerName);}
    public boolean isColorAvailable(Color color){
        return totemColors.containsValue(color);
    }
    public int getOfferTiles(String playerName){return currentOfferTiles.get(playerName);}
    public TurnTile getTurnTile(){return turnTile;}
    public ArrayList<OfferTile> getOfferTilesNumber(){return offerTiles;}


}
