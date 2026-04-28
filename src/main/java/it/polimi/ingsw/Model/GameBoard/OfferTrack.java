package it.polimi.ingsw.Model.GameBoard;

import it.polimi.ingsw.Model.Cards.*;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Game.Game;

import java.util.ArrayList;

/**OfferTrack class represents game board, with current available cards,
 * divided in top and bottom row, and the offer tiles suitable for current number
 * of players.
 */
public class OfferTrack{
    private final Game game;
    private final ArrayList<OfferTile> offerTiles;
    private ArrayList<Card> topRow;
    public ArrayList<Card> bottomRow;
    private ArrayList<BuildingCard> topBuildingCard;
    private ArrayList<BuildingCard> bottomBuildingCard;
    private /* forse final*/ TurnTile turnTile;

    /**@deprecated
     */
    private int currentPlayerTileIdx;

    private final int playerNumber;
    int[] availableBuildingsPerEra;

    public OfferTrack(Game gameInstance, int playerNumber){
        this.game = gameInstance;
        this.offerTiles = new ArrayList<>();
        this.topRow = new ArrayList<>();
        this.bottomRow = new ArrayList<>();
        this.topBuildingCard = new ArrayList<>();
        this.bottomBuildingCard = new ArrayList<>();
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

    /**@deprecated
     */
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

    /**@deprecated
     */
    public void updatePlayerTile(int idx){
        this.currentPlayerTileIdx = idx;
    }

    //da testare con il visitor
    /**initializeBottomRow method is called only at the beginning of a new game,
     * therefore bottomRow and topRow will be empty new arrays (created by constructor).
     * CharacterCards will be added to the bottomRow while EventCards to the topRow,
     * as prescribed by the  game's rules and implemented with visitor pattern.
     */
    public void initializeBottomRow(){
        RowInitializerVisitor visitor = new RowInitializerVisitor();
        /*bottomRow is empty at the first iteration, because initialized
         by class constructor right before.*/
        while(bottomRow.size() < playerNumber + 1){
            Card drawnCard = game.getDeck().drawCard();
            drawnCard.accept(visitor, this);
        }
    }

    /**repopulateTopRow method is called everytime new cards are needed:
     * both at the end of each turn and at the start of the game.
     * When called at the end of the turn, it always comes after
     * the topRow has been cleared by the moveCardToBottom method.
     * When called for the first time at the start of the game,
     * the field has been created with 'new' operand right before.
     */
    public ArrayList<Card> repopulateTopRow() {
        while(topRow.size() < playerNumber + 4) {
            topRow.add(game.getDeck().drawCard());
        }
        return topRow;
    }

    /**Method gets new building cards for the current era.
     * BuildingsDeck is already built with correct number of cards for each era,
     * considering the number of players,
     * therefore no check is needed other than cards' era.
     */
    public void repopulateTopBuildingCards(){
        topBuildingCard = new ArrayList<>();
        int era = game.getEra();
        while(game.getDeck().getBuildingsDeck().peek().getEra() == era){
            // getEra potrebbe lanciare eccezione, come gestirlo?
            topBuildingCard.add(game.getDeck().getBuildingsDeck().pop());
        }
    }

    public void moveCardsToBottom(){
        bottomRow = topRow;
        topRow = new ArrayList<>();

    }

    public void moveBuildings(){bottomBuildingCard = topBuildingCard;}
}