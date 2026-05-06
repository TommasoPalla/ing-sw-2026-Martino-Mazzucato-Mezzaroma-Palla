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
    private Color totemColor;
    private final Tribe tribe;
    private OfferTile currentOfferTile;

    public Player(Game gameInstance, String name) {
        this.game = gameInstance;
        this.name = name;
        this.tribe = new Tribe(gameInstance);
        this.currentOfferTile = null;
        tribe.setOwner(this);
    }

    public void setTotemColor(Color totemColor){this.totemColor = totemColor;}

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

    //functions
    /*funzione che dipende da controller anche che è ancora da implementare, qui bozza sbagliata ma circa completa*/
    public OfferTile chooseOfferTile(int index, OfferTrack offerTrack) throws OccupiedTileException {
        /*logica di input*/
        OfferTile chosen = offerTrack.getOfferTiles().get(index);
        System.out.println(chosen);
        if (chosen.isOccupied()) {
            throw new OccupiedTileException();
        } else {
            chosen.occupy(this);
            currentOfferTile = chosen;
            return chosen;
        }
    }

    //returns true if building is affordable to player or if the card is a character, returns false otherwise
    public boolean drawable(boolean fromTopRow, boolean fromBuilding, int index, OfferTrack offerTrack) {
        Card card;
        DrawableCardVisitor visitor = new DrawableCardVisitor(this);
        if(fromBuilding){
            card = fromTopRow ? offerTrack.getTopBuildingCard().get(index)
                    : offerTrack.getBottomBuildingCard().get(index);
        }
        else {
            card = fromTopRow ? offerTrack.getTopRow().get(index)
                    : offerTrack.getBottomRow().get(index);
        }
        try {
            card.accept(visitor);
        } catch (IllegalDrawException | InsufficientFoodException e){
            System.err.println("Can't draw this card: " + e.getMessage());

            //va gestito con le view, da capire dopo: eventualmente lanciare l'eccezione al metodo più esterno
            //che verosimilmente sarà del controller e sarà lui a mostrare l'errore

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
                } else {
                    drawnBuilding = offerTrack.getBottomBuildingCard().remove(index);
                }
                int discountedCost = drawnBuilding.getCost() - getTribe().getBuilderDiscount();
                if(discountedCost < 0) discountedCost = 0;
                getTribe().modifyFood(-discountedCost);

                getTribe().addBuildingToTribe(drawnBuilding);
                return drawnBuilding;
            }
            else {
                CharacterCard drawnCharacter;
                if(fromTopRow){
                    drawnCharacter = (CharacterCard) offerTrack.getTopRow().remove(index);
                }
                else {
                    drawnCharacter = (CharacterCard) offerTrack.getBottomRow().remove(index);
                }
                getTribe().addCharacterToTribe(drawnCharacter);
                return drawnCharacter;
            }
        }
        return null;
    }
}