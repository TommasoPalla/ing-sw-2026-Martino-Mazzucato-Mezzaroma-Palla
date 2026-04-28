package it.polimi.ingsw.Model;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Model.Users.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/*
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
    private Map<Player, Color> totemColors;
    private Map<String, Integer> currentOfferTiles;
    private ArrayList<String> turnTile;
    private ArrayList<String> offerTiles;
    //lightTribe, qui non esiste player solo il suo id!!!!!!!!!!

    /*public ClientModel(Game realModel) {
        //this.player = player;
        this.otherFoodReserves = new HashMap<>();
        this.otherPrestigePoints = new HashMap<>();
        this.currentOfferTiles = new HashMap<>();
        for(Player otherPlayer : realModel.getPlayers()) {
            otherFoodReserves.put(otherPlayer, 0);
            otherPrestigePoints.put(otherPlayer, 0);
            currentOfferTiles.put(otherPlayer, null);
        }
        this.currentPhase = realModel.getGamePhase();
        this.currentRound = 0;
        this.currentPlayerTurn = realModel.getCurrentPlayer();
        this.offerTrack = realModel.getOfferTrack();
        this.foodReserve = 0;
        this.prestigePoints = 0;
        this.population = new HashMap<>();
        this.buildings = new ArrayList<>();
        this.topRow = realModel.getOfferTrack().getTopRow();
        this.bottomRow = realModel.getOfferTrack().getBottomRow();
        this.topBuildingCard = realModel.getOfferTrack().getTopBuildingCard();
        this.bottomBuildingCard = new ArrayList<>();
    }*/
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
        this.turnTile=new ArrayList<>();
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

    public boolean isOccupied(int index) { return offerTrack.getOfferTiles().get(index).isOccupied(); }

    /*
     * Update methods
     */
    public void setNextPlayer(Player nextPlayer) {
        currentPlayerTurn = nextPlayer;
    }
    public void setNextRound() {
        currentRound++;
    }
    public void addPlayer(String id){
        if(players.size()<=numPlayers){
            LightTribe lightTribe= new LightTribe(id);
            players.put(id, lightTribe);
        }
    }


    public void updateFoodReserve(int food, String id) {
        players.get(id).addFood(food);
    }
    public void updatePrestigePoints(int pp, String id) {
        players.get(id).addPrestigePoints(pp);
    }
    public void updateShamansStars(int stars, String id) {
        players.get(id).addShamansStars(stars);
    }
    public void updateOfferTile(int index, String id) {
        currentOfferTiles.put(id, index);
    }
    public void updateTurnTile(ArrayList<String> distantTurnTile){
        turnTile=distantTurnTile;
    }
    public void updateOfferTiles(ArrayList<String> distantOfferTile){
        offerTiles=distantOfferTile;
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
    public void chosenTotemColor(Player player, Color color){
        totemColors.put(player, color);
    }
    public void updateCharacterDrawn(Card card, String id) {
    }
    public void updateBuildingDrawn(BuildingCard building, String id){

    }
    public ArrayList<Card> getTopRow(){return topRow;};
    public ArrayList<Card> getBottomRow(){return bottomRow;};
    public ArrayList<BuildingCard> getTopRowBuildings(){return topBuildingCard;};
    public ArrayList<BuildingCard> getBottomRowBuildings(){return bottomBuildingCard;};


    public LightTribe getPlayerTribe(String id){return players.get(id);}
}