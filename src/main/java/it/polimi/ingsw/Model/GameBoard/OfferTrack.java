package it.polimi.ingsw.Model.GameBoard;

import it.polimi.ingsw.Model.Cards.*;
import it.polimi.ingsw.Model.Cards.Buildings.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Game.Game;

import java.util.ArrayDeque;
import java.util.ArrayList;

public class OfferTrack{
    private final Game game;
    private ArrayList<OfferTile> offerTiles;
    private ArrayList<Card> topRow;
    public ArrayList<Card> bottomRow;
    private ArrayList<BuildingCard> topBuildingCard;
    private ArrayList<BuildingCard> bottomBuildingCard;
    private TurnTile turnTile;
    private int currentPlayerTileIdx;
    private int playerNumber;
    int[] availableBuildingsPerEra;

    public OfferTrack(Game gameInstance, int playerNumber){
        this.game = gameInstance;
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
     * Once found, event cards are added to bottomEventCards list and returned.
     */
    public ArrayList<EventCard> getBottomEvents(){
        ArrayList<EventCard> bottomEventCards = new ArrayList<>();

        VisitorAdapter visitor = new VisitorAdapter() {
            @Override
            public void visitCard(EventCard event, OfferTrack offerTrack) {
                bottomEventCards.add(event);
            }
        };
        for(Card card : bottomRow){
            card.accept(visitor, this);
        }
        return bottomEventCards;
    }

    public ArrayList<EventCard> getTopEvents(){
        ArrayList<EventCard> topEventCards = new ArrayList<>();
        VisitorAdapter visitor = new VisitorAdapter() {
            @Override
            public void visitCard(EventCard event, OfferTrack offerTrack) {
                topEventCards.add(event);
            }
        };
        for(Card card : topRow){
            card.accept(visitor, this);
        }
        return topEventCards;
    }


    //actual functions

    /*queste due funzioni sembrano ridondanti, in player c'è già draw che fa la stessa cosa
    inoltre qui non c'è nessun controllo sulla "pescabilità" della carta

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
    //stesso problema dei due metodi sopra, già risolto in player con drawCard che chiama drawable
    //---------------------------FORSE DA CAPIRE SE LE EXCEPTION UCCIDONO L'INPUT DELL'UTENTE--------
    public BuildingCard pickBuildingFromTop(int index){
        if(index >= topBuildingCard.toArray().length) throw new ArrayIndexOutOfBoundsException("Can't pick the indexed card, empty row or wrong index");
        BuildingCard cardPicked = topBuildingCard.get(index);
        topBuildingCard.remove(index);
        return cardPicked;
    }

    public BuildingCard pickBuildingFromBottom(int index){
        if(index >= bottomBuildingCard.toArray().length) throw new ArrayIndexOutOfBoundsException("Can't pick the indexed card, empty row or wrong index");
        return bottomBuildingCard.remove(index);
    }*/
    //-----------------------------------------------------------------------------------------------

    //la botttomRow sarà sicuramente vuota? dove viene fatto il contorllo che la svuota?
    public void moveCardsToBottom(){
        bottomRow = topRow;
        topRow =new ArrayList<Card>();

    }
    public void moveBuildings(){bottomBuildingCard = topBuildingCard;}


    public void repopulateTopRow() {
        while(topRow.size() < playerNumber + 4) {
            topRow.add(game.getDeck().drawCard());
        }
    }
    //forse si può fare meglio, così però non serve drawBuilding
    public void repopulateTopBuildingCards(){
        topBuildingCard = new ArrayList<BuildingCard>();    //sennò size può avere un valore variabile
        ArrayDeque<BuildingCard> tempBuildings = game.getDeck().getBuildingsDeck();
        int era = game.getEra();
        while(tempBuildings.peek().getEra() == era){    //verificare che non venga letto l'elemento successivo
            topBuildingCard.add(tempBuildings.pop());
        }
    }

    //da testare con il visitor

    /**initializeBottomRow method is called only at the beginning of a new game,
     * therefore bottomRow and topRow will be empty new arrays (created by constructor).
     * CharacterCards will be added to the bottomRow while EventCards to the topRow,
     * as prescribed by the rules.
     */
    public void initializeBottomRow(){
        RowInitializerVisitor visitor = new RowInitializerVisitor();
        //size sarà certamente nulla perchè il metodo è chiamato poco dopo il costruttore di offertrack
        while(bottomRow.size() < playerNumber + 1){
            Card drawnCard = game.getDeck().drawCard();
            drawnCard.accept(visitor, this);
        }
    }
}
