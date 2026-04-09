package cards_and_deck;

import enums.*;
import users.*;

public class BuildingCard extends Card {
    private final int cost;
    private final GamePhase activatedAt;
    private final String effectDescription;
    protected Player owner;
    /*attributi accessori per l'effetto
     */

    // constructor, "overrides" Card constructor
    public BuildingCard(int era, String name, String cardID, int cost, GamePhase activatedAt, String effectDescription){
        super(era, name, cardID);
        this.cost = cost;
        this.activatedAt = activatedAt;
        this.effectDescription = effectDescription;
        this.owner = null;
    }

    // getters
    public String getEffect(){
        return this.effectDescription;
    }
    public int getCost(){
        return this.cost;
    }
    public Player getOwner(){ return this.owner; }

    // actual functions
    /**
     * check whether player has sufficient food to purchase this buildingCard
     */
    // chiamata all'interno dei metodi di draw
    public boolean isPurchasable(Player player){
        int discountedCost = cost - player.getTribe().getGatherersDiscount();
        int foodReserve = player.getTribe().getFoodReserve();
        if(foodReserve >= discountedCost){
            return true;
        }
        else {
            return false;
        }
    }
    /*
    * assigns an owner to the card
     */
    public void assignOwner(Player player){
        this.owner = player;
    }
    /* quando player.draw(from top o bottom) pesca un building chiama questo metodo
        passa la propria tribù (tramite getTribe)
        viene sempre chiamato questo metodo che controlla se l'effetto è immediato
        e lo applica*/
    public void effectOnPurchase(Tribe tribe){
        if (activatedAt != GamePhase.ON_DRAW){ //da cambiare in on purchase
            return;
        }
        this.applyEffect();
    }

   public void applyEffect(){}
}
