package it.polimi.ingsw.Model.GameBoard;
import it.polimi.ingsw.Model.Users.Player;

public class OfferTile {
    private int cardsFromAbove;
    private int cardsFromBelow;
    private int foodBonus;
    private final char offerTileID;

    private boolean occupied;
    private Player currentOccupant;

    public OfferTile(char offerTileID){
        this.occupied = false;
        this.currentOccupant = null;
        this.offerTileID = offerTileID;
        switch (offerTileID){
            case 'A':
                this.cardsFromAbove = 0;
                this.cardsFromBelow = 0;
                this.foodBonus = 3;
                break;
            case 'B':
                this.cardsFromAbove = 0;
                this.cardsFromBelow = 1;
                this.foodBonus = 0;
                break;
            case 'C':
                this.cardsFromAbove = 1;
                this.cardsFromBelow = 0;
                this.foodBonus = 0;
                break;
            case 'D':
                this.cardsFromAbove = 0;
                this.cardsFromBelow = 2;
                this.foodBonus = 0;
                break;
            case 'E':
                this.cardsFromAbove = 1;
                this.cardsFromBelow = 1;
                this.foodBonus = 0;
                break;
            case 'F':
                this.cardsFromAbove = 2;
                this.cardsFromBelow = 0;
                this.foodBonus = 0;
                break;
            case 'G':
                this.cardsFromAbove = 2;
                this.cardsFromBelow = 1;
                this.foodBonus = 0;
                break;
        }
    }

    //getters
    public int getCardsFromAbove() {return cardsFromAbove;}
    public int getCardsFromBelow(){return cardsFromBelow;}
    public boolean isOccupied(){return occupied;}
    public int getFoodBonus(){return foodBonus;}
    public char getTileCode(){return offerTileID;}
    public Player getCurrentOccupant(){return currentOccupant;}

    //actual functions
    public void occupy(Player player){
        currentOccupant = player;
        occupied = true;
    }
    public void free(){
        currentOccupant = null;
        occupied = false;
    }
}
