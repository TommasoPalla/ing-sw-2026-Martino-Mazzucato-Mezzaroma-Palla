package it.polimi.ingsw.Model;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.Buildings.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.GameBoard.OfferTile;
import it.polimi.ingsw.Model.GameBoard.OfferTrack;
import it.polimi.ingsw.Model.Users.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/*
* Light model for the client to be accessed. Updated when the model state is modified. Update methods
* are called by the Client controllers
 */
public class ClientModel {
    private final Player player;
    private GamePhase currentPhase;
    private Player currentPlayerTurn;
    private OfferTrack offerTrack;
    private int prestigePoints;
    private int foodReserve;
    private Map<Player, Integer> otherFoodReserves;
    private Map<Player, Integer> otherPrestigePoints;
    private Map<CharacterRole, ArrayList<CharacterCard>> population;
    private Map<Player, Map<CharacterRole, ArrayList<CharacterCard>>> otherPopulation;
    private ArrayList<BuildingCard> buildings;
    private OfferTile currentOfferTile;
    private Map<Player, OfferTile> currentOfferTiles;
    private ArrayList<Card> topRow;
    public ArrayList<Card> bottomRow;
    private ArrayList<BuildingCard> topBuildingCard;
    private ArrayList<BuildingCard> bottomBuildingCard;

    public ClientModel(Player player, Game realModel) {
        this.player = player;
        this.otherFoodReserves = new HashMap<>();
        this.otherPrestigePoints = new HashMap<>();
        this.currentOfferTiles = new HashMap<>();
        for(Player otherPlayer : realModel.getPlayers()) {
            otherFoodReserves.put(otherPlayer, 0);
            otherPrestigePoints.put(otherPlayer, 0);
            currentOfferTiles.put(otherPlayer, null);
        }
        this.currentPhase = realModel.getGamePhase();
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
    }

    public boolean drawable(int index, int row) {

    }

    public boolean isOccupied(int index) { return offerTrack.getOfferTiles().get(index).isOccupied(); }

    /*
     * Update methods
     */
    public void updateFoodReserve(int food) {
        if(player == currentPlayerTurn) foodReserve += food;
        else otherFoodReserves.put(currentPlayerTurn, otherFoodReserves.get(player) + food);

    }
    public void updatePrestigePoints(int pp) {
        if(player == currentPlayerTurn) prestigePoints += pp;
        else otherPrestigePoints.put(currentPlayerTurn, otherPrestigePoints.get(player) + pp);
    }

    public void updateCurrentOfferTile(OfferTile offerTile) {
        if(player == currentPlayerTurn) currentOfferTile = offerTile;
        else currentOfferTiles.put(currentPlayerTurn, offerTile);
    }

    public void updateCardDrawn(CharacterCard card) {
        if(player == currentPlayerTurn) population.get(card.getRole()).add(card);
        else otherPopulation.get(currentPlayerTurn).get(card.getRole()).add(card);
    }
    public void updateTopRow(ArrayList<Card> newTopRow) {
        this.topRow = newTopRow;
    }
    public void updateBottomRow(ArrayList<Card> newBottomRow) {
        this.bottomRow = newBottomRow;
    }
}