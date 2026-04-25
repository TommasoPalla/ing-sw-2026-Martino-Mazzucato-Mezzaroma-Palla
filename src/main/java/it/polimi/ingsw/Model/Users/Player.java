package it.polimi.ingsw.Model.Users;

import it.polimi.ingsw.Model.Cards.*;
import it.polimi.ingsw.Enums.Color;
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
    public boolean drawable(int index, OfferTrack offerTrack, int row) {
        Card card;
        DrawableCardVisitor visitor = new DrawableCardVisitor();
        if (row == 0) {//0 è toprow
            card = offerTrack.getTopRow().get(index);
        } else {
            card = offerTrack.getBottomRow().get(index);
        }
        try {
            card.accept(visitor, this);
        } catch (Illegal_Draw_Exception | Insufficient_Food_Exception e){
            System.err.println("Mossa non consentita: " + e.getMessage());

            //va gestito con le view, da capire dopo: eventualmente lanciare l'eccezione al metodo più esterno
            //che verosimilmente sarà del controller e sarà lui a mostrare l'errore

        }
        return visitor.isDrawable();
    }

    public void drawFromTopRow(int index, OfferTrack offerTrack) throws Illegal_Draw_Exception, Insufficient_Food_Exception{
        Card card = offerTrack.getTopRow().get(index);
        AddCardVisitor visitor = new AddCardVisitor();
        if(this.drawable(index, offerTrack, 0)){

            /*accept method calls VisitCard method that depending on the card's type does the following:
            - add the building to the player's tribe, if BuildingCard
            - add the character to the player's tribe, if CharacterCard
            - nothing, if EventCard; in this case 'then branch' is not executed.
             */
            card.accept(visitor, this);
            offerTrack.getTopRow().remove(index);
        }
    }

    public void drawFromBottomRow(int index, OfferTrack offerTrack) throws Illegal_Draw_Exception {
        Card card = offerTrack.getTopRow().get(index);
        AddCardVisitor visitor = new AddCardVisitor();
        if(this.drawable(index, offerTrack, 1)){

            /*accept method calls VisitCard method that depending on the card's type does the following:
            - add the building to the player's tribe, if BuildingCard
            - add the character to the player's tribe, if CharacterCard
            - nothing, if EventCard; in this case 'then branch' is not executed.
             */
            card.accept(visitor, this);
            offerTrack.getBottomRow().remove(index);
        }
    }
}