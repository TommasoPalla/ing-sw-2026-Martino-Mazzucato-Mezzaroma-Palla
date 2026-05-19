package it.polimi.ingsw.Model.GameBoard;

public class OfferTile {
    private int cardsFromAbove;
    private int cardsFromBelow;
    private int foodBonus;
    private final char offerTileID;

    private String currentOccupant; //player name

    public OfferTile(char offerTileID){
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

    // GETTERS --------------------------------------------------------------------------------------------------------

    /**
     * Gets the amount of cards that this tile allows the player to pick from the top row.
     * @return the number of cards.
     */
    public int getCardsFromAbove() {return cardsFromAbove;}

    /**
     * Gets the amount of cards that this tile allows the player to pick from the bottom row.
     * @return the number of cards.
     */
    public int getCardsFromBelow(){return cardsFromBelow;}

    /**
     * Checks if the tile is already occupied by a player.
     * @return true if the tile is already occupied by a player, false if not.
     */
    public boolean isOccupied(){
        if (currentOccupant == null) return false;
        else return true;
    }

    /**
     * Gets the Food bonus given by the tile
     * @return 0 if it does not give any food, else it returns the food bonus.
     */
    public int getFoodBonus(){return foodBonus;}

    /**
     * Gets the code of this tile.
     * @return the code of the tile (char)
     */
    public char getTileCode(){return offerTileID;}

    /**
     * Gets the currentOccupant of the tile.
     * @return It returns the player name if it's occupied, else it returns null.
     */
    public String getCurrentOccupant(){return currentOccupant;}

    // -----------------------------------------------------------------------------------------------------------------
    // ACTUAL FUNCTIONS ------------------------------------------------------------------------------------------------
    /**
     * Sets the occupant of the tile when this tile is chosen by a player during the totem placing phase.
     * @param playerName The name of the player whose occupying the tile.
     */
    public void occupy(String playerName){
        currentOccupant = playerName;
    }

    /**
     * It frees the tile when the player has drawn all the cards and returned to the turn tile.
     * Sets currentOccupant to null
     */
    public void free(){
        currentOccupant = null;
    }
}