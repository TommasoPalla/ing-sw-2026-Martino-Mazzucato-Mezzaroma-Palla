package it.polimi.ingsw.Model.GameBoard;

import it.polimi.ingsw.Model.Cards.*;
import it.polimi.ingsw.Model.Cards.Buildings.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Game.Game;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class OfferTrack{
    private ArrayList<OfferTile> offerTiles;
    private ArrayList<Card> topRow;
    public ArrayList<Card> bottomRow;
    private ArrayList<BuildingCard> topBuildingCard;
    private ArrayList<BuildingCard> bottomBuildingCard;
    private ArrayList<EventCard> topEventCards;
    private ArrayList<EventCard> bottomEventCards;
    private TurnTile turnTile;
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
        this.turnTile = new TurnTile(playerNumber);

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

        offerTiles.add(new OfferTile('B'));
        offerTiles.add(new OfferTile('C'));
        offerTiles.add(new OfferTile('E'));
        offerTiles.add(new OfferTile('F'));
        if(playerNumber >= 3) offerTiles.add(new OfferTile('D'));
        if(playerNumber >= 4) offerTiles.add(new OfferTile('G'));
        if(playerNumber == 5) offerTiles.add(new OfferTile('A'));
    }


    //getters
    public ArrayList<OfferTile> getOfferTiles() {return offerTiles;}
    public ArrayList<Card> getTopRow() {return topRow;}
    public ArrayList<Card> getBottomRow() {return bottomRow;}
    public ArrayList<BuildingCard> getTopBuildingCard() {return topBuildingCard;}
    public ArrayList<BuildingCard> getBottomBuildingCard() {return bottomBuildingCard;}
    public int getCurrentPlayerTileIdx() {return currentPlayerTileIdx;}
    public TurnTile getTurnTile() {return turnTile;}

    /**
     * Here visitor pattern is used to identify only the Event Cards
     * among cards in the bottom row of the offer track.
     * Once found, event cards are added to bottomEventCards list.
     */
    public ArrayList<Card> getBottomEvents(){
        bottomEventCards = new ArrayList<>();
        EventsVisitor visitor = new EventsVisitor();
        for(Card card : bottomRow){
            card.accept(visitor, bottomEventCards);
        }
        return bottomEventCards;
    }

    public ArrayList<Card> getTopEvents(){
        ArrayList<Card> topEventCards = new ArrayList<>();
        EventsVisitor visitor = new EventsVisitor();
        for(Card card : topRow){
            card.accept(visitor, topEventCards);
        }
        return topEventCards;
    }


    //actual functions
    public CharacterCard pickCharacterFromTop(int index){
        CharacterCard cardPicked = (CharacterCard) topRow.get(index);
        topRow.remove(index);
        return cardPicked;
    }

    public CharacterCard pickCharacterFromBottom(int index) {
        CharacterCard cardPicked = (CharacterCard) bottomRow.get(index);
        topRow.remove(index);
        return cardPicked;
    }

    //---------------------------FORSE DA CAPIRE SE LE EXCEPTION UCCIDONO L'INPUT DELL'UTENTE--------
    public BuildingCard pickBuildingFromTop(int index){
        if(index >= topBuildingCard.toArray().length) throw new ArrayIndexOutOfBoundsException("Can't pick the indexed card, empty row or wrong index");
        BuildingCard cardPicked = topBuildingCard.get(index);
        topBuildingCard.remove(index);
        return cardPicked;
    }

    public BuildingCard pickBuildingFromBottom(int index){
        if(index >= bottomBuildingCard.toArray().length) throw new ArrayIndexOutOfBoundsException("Can't pick the indexed card, empty row or wrong index");
        BuildingCard cardPicked = bottomBuildingCard.get(index);
        bottomBuildingCard.remove(index);
        return cardPicked;
    }
    //-----------------------------------------------------------------------------------------------
    public void moveCardsToBottom(){bottomRow = topRow;}
    public void moveBuildings(){bottomBuildingCard = topBuildingCard;}

    //per niente sicuro che tutto questo funzioni
    //!!!!!NON FUNZIONA!!!!
    public void repopulateTopRow() {
        while(topRow.size() < playerNumber + 4) {
            //
            topRow.add(Game.getDeck().drawCard());
        }
    }
    //forse si può fare meglio, così però non serve drawBuilding
    public void repopulateTopBuildingCards(){
        topBuildingCard = new ArrayList<BuildingCard>();    //sennò size può avere un valore variabile
        ArrayDeque<BuildingCard> tempBuildings = Game.getDeck().getBuildingsDeck();
        int era = Game.getInstance().getEra();
        while(tempBuildings.peek().getEra() == era){    //verificare che non venga letto l'elemento successivo
            topBuildingCard.add(tempBuildings.pop());
        }
    }

    //da testare con il visitor
    public void initializeBottomRow(){
        bottomRow = new ArrayList<>();  //size sarà certamente nulla
        RowInitializerVisitor visitor = new RowInitializerVisitor();
        while(bottomRow.size() < playerNumber + 1){

            VisitorAdapter visitor = new VisitorAdapter() {
                @Override
                public void visitCard(CharacterCard character){
                    bottomRow.add(character);
                }
                @Override
                public void visitCard(EventCard event){
                    topRow.add(event);
                }
            };

            Card drawnCard = Game.getInstance().getDeck().drawCard();
            drawnCard.accept(visitor);
        }
    }
}
