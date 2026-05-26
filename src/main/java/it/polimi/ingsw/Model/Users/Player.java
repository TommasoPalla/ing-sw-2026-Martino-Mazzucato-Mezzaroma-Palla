package it.polimi.ingsw.Model.Users;

import it.polimi.ingsw.CustomException.IllegalDrawException;
import it.polimi.ingsw.CustomException.InsufficientFoodException;
import it.polimi.ingsw.CustomException.OccupiedTileException;
import it.polimi.ingsw.Model.Cards.*;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.GameBoard.OfferTile;
import it.polimi.ingsw.Model.GameBoard.OfferTrack;


public class Player {
    private final Game game;
    private final String name;
    private final Color totemColor;
    private final Tribe tribe;
    private OfferTile currentOfferTile;
    private int remainingAbove;
    private int remainingBelow;

    public Player(Game gameInstance, String name, Color totemColor) {
        this.game = gameInstance;
        this.name = name;
        this.tribe = new Tribe(gameInstance, this);
        this.totemColor = totemColor;
        this.currentOfferTile = null;
        this.remainingAbove = 0;
        this.remainingBelow = 0;
    }

    //getters
    public Game getGame(){
        return game;
    }
    public String getName() {
        return name;
    }
    public Color getTotemColor() {
        return totemColor;
    }
    public Tribe getTribe() {
        return tribe;
    }
    public OfferTile getCurrentOfferTile() {
        return currentOfferTile;
    }
    public int getFinalPoints() {
        return tribe.calculateFinalPoints();
    }

    public int getRemainingAbove() {
        return remainingAbove;
    }

    public int getRemainingBelow() {
        return remainingBelow;
    }

    //functions

    /**
     * Sets the player current offer tile when he occupies one.
     * @param offerTile the offer tile he occupied.
     */
    public void setCurrentOfferTile(OfferTile offerTile) {
        this.currentOfferTile = offerTile;
        this.remainingAbove = offerTile.getCardsFromAbove();
        this.remainingBelow = offerTile.getCardsFromBelow();
    }

    public void freeOfferTile(){
        currentOfferTile.free();
        currentOfferTile = null;
        this.remainingAbove = 0;
        this.remainingBelow = 0;
    }

    //returns true if building is affordable to player or if the card is a character, returns false otherwise
    public boolean drawable(boolean fromTopRow, boolean fromBuilding, int index, OfferTrack offerTrack) {
        if (fromTopRow && remainingAbove <= 0) return false;
        if (!fromTopRow && remainingBelow <= 0) return false;

        Card card;
        DrawableCardVisitor visitor = new DrawableCardVisitor(this);
        try {
            if(fromBuilding){
                card = fromTopRow ? offerTrack.getTopBuildingCard().get(index)
                        : offerTrack.getBottomBuildingCard().get(index);
            }
            else {
                card = fromTopRow ? offerTrack.getTopRow().get(index)
                        : offerTrack.getBottomRow().get(index);
            }
            card.accept(visitor);
        } catch (Exception e){
            System.err.println("Can't draw this building: " + e.getMessage());
            return false;
        }
        return true;
    }

    /**drawCard method is called when a player tries to add one
     * of the cards on the OfferTrack to their tribe.
     * @param index position of the card in its specific array
     * @param fromTopRow top or bottom row (it does not depend on buildings or characters)
     * @param fromBuildings true if the card to draw is in buildings arrays, false otherwise
     * @return the drawn card, if the card can be drawn, returns null otherwise
     */
    public Card drawCard(boolean fromTopRow, boolean fromBuildings, int index, OfferTrack offerTrack){
        if(this.drawable(fromTopRow, fromBuildings, index, offerTrack)){
            if(fromBuildings){
                BuildingCard drawnBuilding;
                if(fromTopRow){
                    drawnBuilding = offerTrack.getTopBuildingCard().remove(index);
                    remainingAbove--;
                } else {
                    drawnBuilding = offerTrack.getBottomBuildingCard().remove(index);
                    remainingBelow--;
                }
                int discountedCost = drawnBuilding.getCost() - getTribe().getBuildersDiscount();
                if(discountedCost < 0) discountedCost = 0;
                getTribe().modifyFood(-discountedCost);

                getTribe().addBuildingToTribe(drawnBuilding);
                return drawnBuilding;
            }
            else {
                CharacterCard drawnCharacter;
                if(fromTopRow){
                    drawnCharacter = (CharacterCard) offerTrack.getTopRow().remove(index);
                    remainingAbove--;
                }
                else {
                    drawnCharacter = (CharacterCard) offerTrack.getBottomRow().remove(index);
                    remainingBelow--;
                }
                getTribe().addCharacterToTribe(drawnCharacter);
                return drawnCharacter;
            }
        }
        return null;
    }
}