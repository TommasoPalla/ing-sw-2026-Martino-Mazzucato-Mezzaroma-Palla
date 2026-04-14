package users;

import cards_and_deck.Card;
import cards_and_deck.EventCard;
import enums.Color;
import game_board.OfferTile;
import cards_and_deck.CharacterCard;
import cards_and_deck.BuildingCard;
import game_board.OfferTrack;

public class Player {
    private String name;
    private final Color totemColor;
    private final Tribe tribe;
    private OfferTile currentOfferTile;

    public Player(String name, Tribe tribe, Color totemColor) {
        this.name = name;
        this.tribe = tribe;
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
        if (card.getClass().equals(CharacterCard.class)) {

            CharacterCard characterPicked = offerTrack.pickCharacterFromTop(index);
            tribe.addCharacterToTribe(characterPicked);
        } else if (card.getClass().equals(EventCard.class)) {
            throw new Illegal_Draw_Exception();
        } else {
            BuildingCard buildingPurchased = offerTrack.pickBuildingFromTop(index);
            tribe.addBuildingToTribe(buildingPurchased);
        }
    }


    public void drawFromBottomRow(int index, OfferTrack offerTrack) throws Illegal_Draw_Exception {
        Card card = offerTrack.getTopRow().get(index);
        if (card.getClass().equals(CharacterCard.class)) {

           CharacterCard characterPicked = offerTrack.pickCharacterFromBottom(index);
           tribe.addCharacterToTribe(characterPicked);
        } else if (card.getClass().equals(EventCard.class)) {
            throw new Illegal_Draw_Exception();
        } else {
            BuildingCard buildingPurchased = offerTrack.pickBuildingFromBottom(index);
            tribe.addBuildingToTribe(buildingPurchased);
        }
    }
}
