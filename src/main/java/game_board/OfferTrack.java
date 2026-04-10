package game_board;

import cards_and_deck.*;
import game.Game;

import java.util.ArrayList;

public class OfferTrack{
    private ArrayList<OfferTile> offerTiles;
    private ArrayList<Card> topRow;
    private ArrayList<Card> bottomRow;
    private ArrayList<BuildingCard> topBuildingCard;
    private ArrayList<BuildingCard> bottomBuildingCard;
    private int currentPlayerTileIdx;
    private int playerNumber;
    int[] availableBuildingsPerEra;

    public OfferTrack(int playerNumber){

        this.offerTiles = new ArrayList<OfferTile>();
        this.topRow = new ArrayList<Card>();
        this.bottomRow = new ArrayList<Card>();
        this.topBuildingCard = new ArrayList<BuildingCard>();
        this.bottomBuildingCard = new ArrayList<BuildingCard>();
        this.currentPlayerTileIdx = -1;
        this.playerNumber = playerNumber;

        switch (playerNumber){
            case 2:
                availableBuildingsPerEra = new int[]{1, 2, 3};
                break;
            case 3:
                availableBuildingsPerEra = new int[]{2, 2, 4};
                break;
            case 4:
                availableBuildingsPerEra = new int[]{2, 3, 4};
                break;
            case 5:
                availableBuildingsPerEra = new  int[]{2, 3, 5};
                break;
        }

        //throw an exception if the number of players is wrong, otherwise initialize the OfferTrack
        if(playerNumber < 2 || playerNumber > 5) throw new IllegalArgumentException("The number of player should be between 2 and 5.");

        offerTiles.add(new OfferTile("OT_B"));
        offerTiles.add(new OfferTile("OT_C"));
        offerTiles.add(new OfferTile("OT_E"));
        offerTiles.add(new OfferTile("OT_F"));
        if(playerNumber >= 3) offerTiles.add(new OfferTile("OT_D"));
        if(playerNumber >= 4) offerTiles.add(new OfferTile("OT_G"));
        if(playerNumber == 5) offerTiles.add(new OfferTile("OT_A"));
    }

    //getters
    public ArrayList<OfferTile> getOfferTiles() {return offerTiles;}
    public ArrayList<Card> getTopRow() {return topRow;}
    public ArrayList<Card> getBottomRow() {return bottomRow;}
    public ArrayList<BuildingCard> getTopBuildingCard() {return topBuildingCard;}
    public ArrayList<BuildingCard> getBottomBuildingCard() {return bottomBuildingCard;}
    public int getCurrentPlayerTileIdx() {return currentPlayerTileIdx;}

    //actual functions
    public Card pickCharacterFromTop(int index){
        Card indexedCard = topRow.get(index);
        if(indexedCard instanceof EventCard) throw new IllegalArgumentException("You can't draw an event, choose a character or a building if available.");
        else return indexedCard;
    }

    public Card pickCharacterFromBottom(int index) {
        Card indexedCard = topRow.get(index);
        if (indexedCard instanceof EventCard) throw new IllegalArgumentException("You can't draw an event, choose a character or a building if available");
        else return indexedCard;
    }

    //---------------------------FORSE DA CAPIRE SE LE EXCEPTION UCCIDONO L'INPUT DELL'UTENTE--------
    public BuildingCard pickBuildingFromTop(int index){
        if(index >= topBuildingCard.toArray().length) throw new ArrayIndexOutOfBoundsException("Can't pick the indexed card, empty row or wrong index");
        return topBuildingCard.get(index);
    }

    public BuildingCard pickBuildingFromBottom(int index){
        if(index >= bottomBuildingCard.toArray().length) throw new ArrayIndexOutOfBoundsException("Can't pick the indexed card, empty row or wrong index");
        return bottomBuildingCard.get(index);
    }
    //-----------------------------------------------------------------------------------------------
    public void moveCardsToBottom(){bottomRow = topRow;}
    public void moveBuildings(){bottomBuildingCard = topBuildingCard;}

    //per niente sicuro che tutto questo funzioni
    public void repopulateTopRow() {
        while(topRow.size() < playerNumber + 4) {
            topRow.add(Game.getDeck().drawCard());
        }
    }
    public void repopulateTopBuildingCards(){
        while(topBuildingCard.size() < availableBuildingsPerEra[Game.getInstance().getEra()]){
            topBuildingCard.add(Game.getDeck().drawBuilding());
        }
    }
    public void initializeBottomRow(){
        while(bottomRow.size() < playerNumber + 1){
            Card drawnCard = Game.getDeck().drawCard();
            if(drawnCard.getClass().equals(EventCard.class)) topRow.add(drawnCard);     //dovrebbe funzionare uguale a instanceof
            else bottomRow.add(drawnCard);
        }

    }
}
