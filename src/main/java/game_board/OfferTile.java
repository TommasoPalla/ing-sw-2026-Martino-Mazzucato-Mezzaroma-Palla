package game_board;
import users.Player;

public class OfferTile {
    private int cardsFromAbove;
    private int cardsFromBelow;
    private int foodBonus;
    private char offerTileID;

    private boolean occupied;
    protected Player currentOccupant;

    public OfferTile(char offerTileID){
        this.occupied = false;
        this.currentOccupant = null;
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
