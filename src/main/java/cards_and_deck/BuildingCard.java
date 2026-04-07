package cards_and_deck;

import enums.*;
import users.*;

public class BuildingCard extends Card {
    private final int cost;
    private final GamePhase activatedAt;
    private final String effectDescription;
    /*attributi accessori per l'effetto
     */

    // constructor, "overrides" Card constructor
    public BuildingCard(Era era, String name, String cardID, int cost, GamePhase activatedAt, String effectDescription){
        super(era, name, cardID);
        this.cost = cost;
        this.activatedAt = activatedAt;
        this.effectDescription = effectDescription;
    }

    // getters
    public String getEffect(){
        return this.effectDescription;
    }
    public int getCost(){
        return this.cost;
    }

    // actual functions
    /**
     * check whether player has sufficient food to purchase this buildingCard
     */
    // chiamata all'interno dei metodi di draw
    public boolean isPurchasable(Player player){
        int discountedCost = cost - player.getTribe().getGathererDiscount();
        int foodReserve = player.getTribe().getFoodReserve();
        if(foodReserve >= discountedCost){
            return true;
        }
        else {
            return false;
        }
    }
    /* quando player.draw(from top o bottom) pesca un building chiama questo metodo
        passa la propria tribù (tramite getTribe)
        viene sempre chiamato questo metodo che controlla se l'effetto è immediato
        e lo applica*/
    public void effectOnPurchase(Tribe tribe){
        if (activatedAt != GamePhase.ON_DRAW){
            return;
        }
        /* da definire la "strategy" dei building,
        effect è il metodo dell'interfaccia building strategy*/
        this.effect();
    }
}
