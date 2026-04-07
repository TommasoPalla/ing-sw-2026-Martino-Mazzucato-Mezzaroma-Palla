package game_board;
import users.Player;

public class OfferTile {
    private int cardsFromAbove;
    private int cardsFromBelow;
    private int foodBonus;

    private boolean occupied;
    protected Player currentOccupant;

    OfferTile(String cardID){
        this.occupied = false;
        this.currentOccupant = null;
        switch (cardID){
            case "OT_A":
                this.cardsFromAbove = 0;
                this.cardsFromBelow = 0;
                this.foodBonus = 3;
                break;
            case "OT_B":
                this.cardsFromAbove = 0;
                this.cardsFromBelow = 1;
                this.foodBonus = 0;
                break;
            case "OT_C":
                this.cardsFromAbove = 1;
                this.cardsFromBelow = 0;
                this.foodBonus = 0;
                break;
            case "OT_D":
                this.cardsFromAbove = 0;
                this.cardsFromBelow = 2;
                this.foodBonus = 0;
                break;
            case "OT_E":
                this.cardsFromAbove = 1;
                this.cardsFromBelow = 1;
                this.foodBonus = 0;
                break;
            case "OT_F":
                this.cardsFromAbove = 2;
                this.cardsFromBelow = 0;
                this.foodBonus = 0;
                break;
            case "OT_G":
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
