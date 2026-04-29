package it.polimi.ingsw.Model.Users;

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

    public Player(Game gameInstance, String name, Color totemColor) {
        this.game = gameInstance;
        this.name = name;
        this.tribe = new Tribe(gameInstance);
        this.totemColor = totemColor;
        this.currentOfferTile = null;
        tribe.setOwner(this);
    }

    //getters
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
    public OfferTile chooseOfferTile(int index, OfferTrack offerTrack) throws Occupied_Tile_Exception {
        /*logica di input*/
        OfferTile chosen = offerTrack.getOfferTiles().get(index);
        System.out.println(chosen);
        if (chosen.isOccupied()) {
            throw new Occupied_Tile_Exception();
        } else {
            chosen.occupy(this);
            currentOfferTile = chosen;
            return chosen;
        }
    }

    //returns true if building is affordable to player or if the card is a character, returns false otherwise
    public boolean drawable(int index, int row, boolean isBuilding, OfferTrack offerTrack) {
        Card card;
        DrawableCardVisitor visitor = new DrawableCardVisitor();
        if(isBuilding){
            card = (row == 0) ? offerTrack.getTopBuildingCard().get(index)
                    : offerTrack.getBottomBuildingCard().get(index);
        }
        else {
            card = (row == 0) ? offerTrack.getTopRow().get(index)
                    : offerTrack.getBottomRow().get(index);
        }
        try {
            card.accept(visitor, this);
        } catch (Illegal_Draw_Exception | Insufficient_Food_Exception e){
            System.err.println("Can't draw this card: " + e.getMessage());

            //va gestito con le view, da capire dopo: eventualmente lanciare l'eccezione al metodo più esterno
            //che verosimilmente sarà del controller e sarà lui a mostrare l'errore

        }
        return visitor.isDrawable();
    }

    /**drawCard method is called when a player tries to add one
     * of the cards on the OfferTrack to their tribe.
     * @param index position of the card in its specific array
     * @param row top (0) or bottom (1)
     * @param isBuilding true if the card to draw is in buildings arrays, false otherwise
     * @return the drawn card, if the card can be drawn, returns null otherwise
     */
    public Card drawCard(int index, int row, boolean isBuilding, OfferTrack offerTrack){
        Card drawnCard = null;
        if(this.drawable(index, row, isBuilding, offerTrack)){
            if(isBuilding){
                if(row == 0){   //top row
                    drawnCard = offerTrack.getTopBuildingCard().remove(index);
                } else {
                    drawnCard = offerTrack.getBottomBuildingCard().remove(index);
                }
                getTribe().addBuildingToTribe((BuildingCard) drawnCard);
            }
            else {
                if(row == 0){
                    drawnCard = offerTrack.getTopRow().remove(index);
                }
                else {
                    drawnCard = offerTrack.getBottomRow().remove(index);
                }
                getTribe().addCharacterToTribe((CharacterCard) drawnCard);
            }
        }
        return drawnCard;
    }
}