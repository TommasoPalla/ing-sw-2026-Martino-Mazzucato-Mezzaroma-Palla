package users;

import cards_and_deck.*;
import enums.Color;
import game_board.OfferTile;
import game_board.OfferTrack;

import java.util.concurrent.atomic.AtomicBoolean;

public class Player {
    private final String name;
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

    //returns true if building is affordable to player or if the card is a character, returns false otherwise
    public boolean drawable(int index, OfferTrack offerTrack, int row)
            throws Illegal_Draw_Exception, Insufficient_Food_Exception{
        Card card;
        AtomicBoolean isDrawable = new AtomicBoolean(false);
        VisitorAdapter visitor = new VisitorAdapter() {
            @Override
            public void visitCard(BuildingCard building) {
                int discountedCost = building.getCost() - Player.this.getTribe().getGatherersDiscount();
                int foodReserve = Player.this.getTribe().getFoodReserve();  //verificare che Player.this vada bene
                if (foodReserve >= discountedCost) {
                    isDrawable.set(true);
                } else {
                    isDrawable.set(false);
                    throw new Insufficient_Food_Exception();
                }
            }
            @Override
            public void visitCard(CharacterCard character) {
                isDrawable.set(true);
            }

            @Override
            public void visitCard(EventCard event) {
               isDrawable.set(false);
               throw new Illegal_Draw_Exception();
            }
        };
        if (row == 0){//0 è toprow
            card = offerTrack.getTopRow().get(index);
        }else{
            card = offerTrack.getBottomRow().get(index);
        }
        card.accept(visitor);
        return isDrawable.get();
        /*  old implementation:
        if(card.getClass().equals(EventCard.class)){
            return false;
        }else if(this.getTribe().getFoodReserve()<((BuildingCard)card).getCost()){
            return false;
        }else{
            return true;
        }*/
    }

    public void drawFromTopRow(int index, OfferTrack offerTrack) throws Illegal_Draw_Exception, Insufficient_Food_Exception{
        Card card = offerTrack.getTopRow().get(index);
        VisitorAdapter visitor = new VisitorAdapter() {
            @Override
            public void visitCard(BuildingCard building) {
                    tribe.addBuildingToTribe(building);
            }

            @Override
            public void visitCard(CharacterCard character) {
                tribe.addCharacterToTribe(character);
            }
        };
        if(this.drawable(index, offerTrack, 0)) {
            card.accept(visitor);
        };
        /* old implementation:
        if (card.getClass().equals(CharacterCard.class)) {

            CharacterCard characterPicked = offerTrack.pickCharacterFromTop(index);
            tribe.addCharacterToTribe(characterPicked);
        } else if (card.getClass().equals(EventCard.class)) {
            throw new Illegal_Draw_Exception();
        } else if(this.getTribe().getFoodReserve()>=((BuildingCard)card).getCost()){
            BuildingCard buildingPurchased = offerTrack.pickBuildingFromTop(index);
            tribe.addBuildingToTribe(buildingPurchased);
        } else{
            throw new Insufficient_Food_Exception();
        }*/
    }


    public void drawFromBottomRow(int index, OfferTrack offerTrack) throws Illegal_Draw_Exception {
        Card card = offerTrack.getTopRow().get(index);
        //visitor implemented
        VisitorAdapter visitor = new VisitorAdapter() {
            @Override
            public void visitCard(BuildingCard building) {
                if(drawable(index, offerTrack, 0)) { //Player.this.drawable() sarebbe la stessa cosa (se c'è il problema non è per questo)
                    BuildingCard buildingPurchased = offerTrack.pickBuildingFromBottom(index);
                    tribe.addBuildingToTribe(buildingPurchased);
                } else {
                    throw new Insufficient_Food_Exception();
                }
            }

            @Override
            public void visitCard(CharacterCard character) {
                CharacterCard charPicked = offerTrack.pickCharacterFromBottom(index);
                tribe.addCharacterToTribe(charPicked);
            }

            @Override
            public void visitCard(EventCard event) {
                throw new Illegal_Draw_Exception();
            }
        };
        card.accept(visitor);

        /*if (card.getClass().equals(CharacterCard.class)) {

           CharacterCard characterPicked = offerTrack.pickCharacterFromBottom(index);
           tribe.addCharacterToTribe(characterPicked);
        } else if (card.getClass().equals(EventCard.class)) {
            throw new Illegal_Draw_Exception();
        } else {
            BuildingCard buildingPurchased = offerTrack.pickBuildingFromBottom(index);
            tribe.addBuildingToTribe(buildingPurchased);
        }*/
    }
}
