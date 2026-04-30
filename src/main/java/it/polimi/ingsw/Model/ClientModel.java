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
    private String gameId;
    private int numPlayers;
    private Map<String, LightTribe> players;
    private GamePhase currentPhase;
    private int currentRound;
    private Player currentPlayerTurn;
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
        this.offerTiles= new ArrayList<>();
    }

    public boolean drawable(int index, int row) {
        switch (row){
            case 0:
                if(topRow.get(index).getCardID().equals(EventCard.class)) return false;
                return true;
            case 1:
                if(bottomRow.get(index).getCardID().equals(EventCard.class)) return false;
                return true;
            default:
                System.out.println("Invalid row exception");
                return false;
        }
    }

    public boolean isOccupied(int index) { return offerTiles.get(index).isOccupied(); }

    //update methods
    public void setNextPlayer(Player nextPlayer) {
        currentPlayerTurn = nextPlayer;
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
    public ArrayList<Card> getTopRow(){return topRow;};
    public ArrayList<Card> getBottomRow(){return bottomRow;};
    public ArrayList<BuildingCard> getTopRowBuildings(){return topBuildingCard;};
    public ArrayList<BuildingCard> getBottomRowBuildings(){return bottomBuildingCard;};
    public Map<String, Integer> getCurrentOfferTiles(){
        return currentOfferTiles;
    }
    public ArrayList<OfferTile> getOfferTiles(){
        return offerTiles;
    }


    public LightTribe getPlayerTribe(String playerName){return players.get(playerName);}
}