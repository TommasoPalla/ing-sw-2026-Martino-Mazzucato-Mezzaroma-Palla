package it.polimi.ingsw.Model.GameBoard;

import it.polimi.ingsw.Model.Cards.*;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Game.Game;

import java.util.ArrayList;

/**
 * This class represents the game board, with the current available cards,
 * divided in top and bottom row, and the Offer Tiles suitable for this game's number
 * of players.
 */
public class OfferTrack{

    /**
     * instance of the {@link Game}.
     */
    private final Game game;

    /**
     * The list of {@link OfferTile} of this game.
     */
    private final ArrayList<OfferTile> offerTiles;

    /**
     * The current top row.
     */
    private ArrayList<Card> topRow;

    /**
     * The current bottom row.
     */
    public ArrayList<Card> bottomRow;

    /**
     * The current Buildings' top row.
     */
    private ArrayList<BuildingCard> topBuildingCard;

    /**
     * The current Buildings' bottom row.
     */
    private ArrayList<BuildingCard> bottomBuildingCard;

    /**
     * A reference to the {@link TurnTile}.
     */
    private final TurnTile turnTile;

    /**
     * The number of players playing this match.
     */
    private final int playerNumber;
    int[] availableBuildingsPerEra;

    public OfferTrack(Game gameInstance, int playerNumber){
        this.game = gameInstance;
        this.offerTiles = new ArrayList<>();
        this.topRow = new ArrayList<>();
        this.bottomRow = new ArrayList<>();
        this.topBuildingCard = new ArrayList<>();
        this.bottomBuildingCard = new ArrayList<>();
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
        if(playerNumber == 5) offerTiles.add(new OfferTile('A'));
        offerTiles.add(new OfferTile('B'));
        offerTiles.add(new OfferTile('C'));
        if(playerNumber >= 3) offerTiles.add(new OfferTile('D'));
        offerTiles.add(new OfferTile('E'));
        offerTiles.add(new OfferTile('F'));
        if(playerNumber >= 4) offerTiles.add(new OfferTile('G'));
    }

    // GETTERS ---------------------------------------------------------------------------------------------------------
    public ArrayList<OfferTile> getOfferTiles() {return offerTiles;}
    public ArrayList<Card> getTopRow() {return topRow;}
    public ArrayList<Card> getBottomRow() {return bottomRow;}
    public ArrayList<BuildingCard> getTopBuildingCard() {return topBuildingCard;}
    public ArrayList<BuildingCard> getBottomBuildingCard() {return bottomBuildingCard;}
    public TurnTile getTurnTile() {return turnTile;}
    // -----------------------------------------------------------------------------------------------------------------

    /**
     * Here visitor pattern is used to identify only the Event Cards
     * among cards in the bottom row of the offer track.
     * @return The list of Event cards in the bottom row.
     */
    public ArrayList<EventCard> getBottomEvents(){
        ArrayList<EventCard> bottomEventCards = new ArrayList<>();

        VisitorAdapter visitor = new VisitorAdapter() {
            @Override
            public void visitCard(EventCard event) {
                bottomEventCards.add(event);
            }
        };
        for(Card card : bottomRow){
            card.accept(visitor);
        }
        return bottomEventCards;
    }

    /**
     * Here visitor pattern is used to identify only the Event Cards
     * among cards in the top row of the offer track.
     * @return The list of Event cards in the top row.
     */
    public ArrayList<EventCard> getTopEvents(){
        ArrayList<EventCard> topEventCards = new ArrayList<>();
        VisitorAdapter visitor = new VisitorAdapter() {
            @Override
            public void visitCard(EventCard event) {
                topEventCards.add(event);
            }
        };
        for(Card card : topRow){
            card.accept(visitor);
        }
        return topEventCards;
    }


    // Actual functions ------------------------------------------------------------------------------------------------

    /**
     * InitializeBottomRow method is called only at the beginning of a new game,
     * therefore bottomRow and topRow will be empty new arrays (created by constructor).
     * CharacterCards will be added to the bottomRow while EventCards to the topRow,
     * as prescribed by the  game's rules and implemented with visitor pattern.
     */
    public void initializeBottomRow(){
        RowInitializerVisitor visitor = new RowInitializerVisitor(this);
        /*bottomRow is empty at the first iteration, because initialized
         by class constructor right before.*/
        while(bottomRow.size() < playerNumber + 1){
            Card drawnCard = game.getDeck().drawCard();
            drawnCard.accept(visitor);
        }
    }

    /**
     * This method is called everytime new cards are needed:
     * both at the end of each turn and at the start of the game.
     * When called at the end of the turn, it always comes after
     * the topRow has been cleared by the moveCardToBottom method.
     * When called for the first time at the start of the game,
     * the field has been created with 'new' operand right before.
     * @return the list of cards forming the new top row.
     */
    public ArrayList<Card> repopulateTopRow() {
        while(topRow.size() < playerNumber + 4) {
            topRow.add(game.getDeck().drawCard());
        }
        return topRow;
    }

    /**
     * This method gets new building cards for the current era.
     * BuildingsDeck is already built with correct number of cards for each era,
     * considering the number of players,
     * therefore no checkIP is needed other than cards' era.
     */
    public void repopulateTopBuildingCards(){
        int era = game.getEra();
        while(!game.getDeck().getBuildingsDeck().isEmpty() && game.getDeck().getBuildingsDeck().peek().getEra() == era){
            topBuildingCard.add(game.getDeck().getBuildingsDeck().pop());
        }
    }

    /**
     * When the round is over and the events have been resolved, the bottom row is cleared and the top
     * row cards are moved to the bottom row.
     */
    public void moveCardsToBottom(){
        bottomRow.clear();
        bottomRow.addAll(topRow);
        topRow = new ArrayList<>();
    }

    /**
     * When a new era has come, the bottom buildings are discarded, and the top buildings are moved to the bottom row.
     */
    public void moveBuildings(){
        bottomBuildingCard.clear();
        bottomBuildingCard.addAll(topBuildingCard);
        topBuildingCard = new ArrayList<>();
    }
}