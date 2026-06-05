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

/**
 * This class contains all the information about a player. It's connected to its {@link Tribe}.
 */
public class Player {

    /**
     * a reference to the {@link Game}'s instance.
     */
    private final Game game;

    /**
     * The name of this player.
     */
    private final String name;

    /**
     * The totem color chosen by this player.
     */
    private final Color totemColor;
    private final Tribe tribe;

    /**
     * the current Offer Tile this player is on.
     */
    private OfferTile currentOfferTile;

    /**
     * The remaining draws from the top row that this player has left to make during his turn in the drawing phase.
     */
    private int remainingAbove;

    /**
     * The remaining draws from the bottom row that this player has left to make during his turn in the drawing phase.
     */
    private int remainingBelow;

    /**
     * True if this player owns the DrawAdditionalCard building and if it can activate its effect at the end of the
     * drawing phase.
     */
    private boolean canDrawAdditional;

    public Player(Game gameInstance, String name, Color totemColor) {
        this.game = gameInstance;
        this.name = name;
        this.tribe = new Tribe(gameInstance, this);
        this.totemColor = totemColor;
        this.currentOfferTile = null;
        this.remainingAbove = 0;
        this.remainingBelow = 0;
        this.canDrawAdditional = false;
    }

    /*
    * Getters
     */
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
    public int getRemainingAbove() {
        return remainingAbove;
    }
    public int getRemainingBelow() {
        return remainingBelow;
    }
    public boolean getCanDrawAdditional() {
        return canDrawAdditional;
    }

    /*
    * Setters
     */

    public void setRemainingDraws(int above, int below) {
        this.remainingAbove = above;
        this.remainingBelow = below;
    }

    public void setCanDrawAdditional(boolean canDrawAdditional) {
        this.canDrawAdditional = canDrawAdditional;
    }

    // Functions

    /**
     * Sets the player current offer tile when he occupies one.
     * @param offerTile the offer tile he occupied.
     */
    public void setCurrentOfferTile(OfferTile offerTile) {
        this.currentOfferTile = offerTile;
        this.remainingAbove = offerTile.getCardsFromAbove();
        this.remainingBelow = offerTile.getCardsFromBelow();
    }

    /**
     * Frees the Offer Tile occupied by this player.
     */
    public void freeOfferTile(){
        currentOfferTile.free();
        currentOfferTile = null;
        this.remainingAbove = 0;
        this.remainingBelow = 0;
    }

    /**
     * Checks if the card that this player wants to draw is drawable.
     * @param fromTopRow true if it's from top row, false if from bottom.
     * @param fromBuilding true if the card is a Building card, false if otherwise.
     * @param index the index of the corresponding row.
     * @param offerTrack a reference to the {@link OfferTrack}.
     * @return true if building is affordable to player or if the card is a character, false otherwise.
     */
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

    /**
     * This method is called when a player tries to add one
     * of the cards on the OfferTrack to their tribe.
     * @param index position of the card in its specific array.
     * @param fromTopRow top or bottom row (it does not depend on buildings or characters).
     * @param fromBuildings true if the card to draw is in buildings arrays, false otherwise.
     * @return the drawn card, if the card can be drawn, returns null otherwise.
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
                if (drawnBuilding.getCardID().equals("E3_B_8"))
                    canDrawAdditional = true;
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