package users;

import cards_and_deck.Card;
import cards_and_deck.EventCard;
import enums.Color;
import enums.InventorType;
import game_board.OfferTile;
import cards_and_deck.CharacterCard;
import cards_and_deck.BuildingCard;
import enums.GamePhase;
import game_board.OfferTrack;

public class Player {
    private String name;
    private Color totemColor;
    private Tribe tribe;
    private OfferTile currentOfferTile;

    public Player(String name, Tribe tribe, Color totemColor) {
        this.name = name;
        this.tribe = tribe;
        this.totemColor = totemColor;
        this.currentOfferTile = null;
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
    public void chooseOfferTile(int index, OfferTrack offerTrack) throws Occupied_Tile_Exception {
        /*logica di input*/
        OfferTile chosen = offerTrack.getOfferTiles().get(index);
        if (chosen.isOccupied()) {
            throw new Occupied_Tile_Exception();
        } else {
            chosen.occupy(this);
        }
    }

    public void drawFromTopRow(int index, OfferTrack offerTrack) throws Illegal_Draw_Exception {
        Card card = offerTrack.getTopRow().get(index);
        if (card instanceof CharacterCard) {
            //cast per usare i metodi di character CharacterCard character = (CharacterCard) card;
            //        // usa character
            offerTrack.pickCharacterFromTop(index);
        } else if (card instanceof EventCard) {
            throw new Illegal_Draw_Exception();

        } else {
            offerTrack.pickBuildingFromTop(index);

        }

    }


    public void drawFromBottomRow(int index, OfferTrack offerTrack) throws Illegal_Draw_Exception {
        Card card = offerTrack.getTopRow().get(index);
        if (card instanceof CharacterCard) {
            //cast per usare i metodi di character CharacterCard character = (CharacterCard) card;
            //        // usa character
            offerTrack.pickCharacterFromBottom(index);
        } else if (card instanceof EventCard) {
            throw new Illegal_Draw_Exception();

        } else {
            offerTrack.pickBuildingFromBottom(index);

        }

    }
}
