package game_board;

import cards_and_deck.BuildingCard;
import cards_and_deck.Card;
import cards_and_deck.EventCard;

import java.util.ArrayList;

public class OfferTrack{
    private ArrayList<OfferTile> offerTiles;
    private ArrayList<Card> topRow;
    private ArrayList<Card> bottomRow;
    private ArrayList<BuildingCard> topBuildingCard;
    private ArrayList<BuildingCard> bottomBuildingCard;
    private int currentPlayerTileIdx;

    OfferTrack(int playerNumber){

        this.offerTiles = new ArrayList<OfferTile>();
        this.topRow = new ArrayList<Card>();
        this.bottomRow = new ArrayList<Card>();
        this.topBuildingCard = new ArrayList<BuildingCard>();
        this.bottomBuildingCard = new ArrayList<BuildingCard>();
        this.currentPlayerTileIdx = -1;

        if(playerNumber >= 2){
            offerTiles.add(new OfferTile("OT_B"));
            offerTiles.add(new OfferTile("OT_C"));
            offerTiles.add(new OfferTile("OT_E"));
            offerTiles.add(new OfferTile("OT_F"));
        }
        if(playerNumber >= 3) offerTiles.add(new OfferTile("OT_D"));
        if(playerNumber >= 4) offerTiles.add(new OfferTile("OT_G"));
        if(playerNumber == 5) offerTiles.add(new OfferTile("OT_A"));
        if(playerNumber < 2 || playerNumber > 5) System.out.println("invalid number of players"); //maybe throw exception
    }

    //getters
    public ArrayList<OfferTile> getOfferTiles() {return offerTiles;}
    public ArrayList<Card> getTopRow() {return topRow;}
    public ArrayList<Card> getBottomRow() {return bottomRow;}
    public ArrayList<BuildingCard> getTopBuildingCard() {return topBuildingCard;}
    public ArrayList<BuildingCard> getBottomBuildingCard() {return bottomBuildingCard;}
    public int getCurrentPlayerTileIdx() {return currentPlayerTileIdx;}

    //actual functions
    public Card removeCardFromTop(int index){
        Card indexedCard = topRow.get(index);
        if(indexedCard instanceof EventCard) {
            //throw exception and choose again
            System.out.println("You can't draw an event, choose a character or a building if available.");
            return null;
        }
        else return indexedCard;
    }

    public Card removeCardFromBottom(int index) {
        Card indexedCard = topRow.get(index);
        if (indexedCard instanceof EventCard) {
            //throw exception and choose again
            System.out.println("You can't draw an event, choose a character or a building if available");
            return null;
        }
        else return indexedCard;
    }

    public void moveCardsToBottom(){
        bottomRow = topRow;
    }
    /* waiting for deck implementation
    public void repopulateTopRow(){
        topRow.add(deck.draw());
    }
    */
    public void moveBuildings(){
        bottomBuildingCard = topBuildingCard;
    }

    /* waiting for deck implementation
    public void repopulateTopBuildingCards(){
    }
    */

}
