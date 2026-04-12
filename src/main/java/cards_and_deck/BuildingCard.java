package cards_and_deck;

import building_management.EffectContext;
import enums.*;
import event_management.EventStrategy;
import users.*;

import javax.naming.Context;
import java.util.EnumMap;

public class BuildingCard extends Card {
    private final int cost; // food cost of the building card
    private final GamePhase activatedAt; // game phase during which this building card is activated
    private final String effectDescription;
    protected Player owner; // the owner of this building, assigned when the building is purchased
    private final int prestige;

    // constructor, "overrides" Card constructor
    public BuildingCard(int era, String name, String cardID, int cost, GamePhase activatedAt, String effectDescription, int prestige){
        super(era, name, cardID);
        this.cost = cost;
        this.activatedAt = activatedAt;
        this.effectDescription = effectDescription;
        this.prestige=prestige;
        this.owner = null;
    }

    // getters
    public String getEffect(){
        return this.effectDescription;
    }
    public int getCost(){
        return this.cost;
    }
    public GamePhase getActivatedAt(){ return this.activatedAt; }
    public Player getOwner(){ return this.owner; }
    public int getPrestige(){return this.prestige; }

    // actual functions
    /**
     * check whether player has sufficient food to purchase this buildingCard
     */
    // chiamata all'interno dei metodi di draw
    public boolean isPurchasable(Player player){
        int discountedCost = cost - player.getTribe().getGatherersDiscount();
        int foodReserve = player.getTribe().getFoodReserve();
        return foodReserve >= discountedCost;
    }
    public void assignOwner(Player player){
        this.owner = player;
    }
    /* quando player.draw(from top o bottom) pesca un building chiama questo metodo
        passa la propria tribù (tramite getTribe)
        viene sempre chiamato questo metodo che controlla se l'effetto è immediato
        e lo applica*/
    public void effectOnPurchase(){
        if (activatedAt != GamePhase.ON_DRAW){ //da cambiare in on purchase
            return;
        }
        this.applyEffect();
    }

    public void applyEffect(){}
    public void applyEffect(EffectContext context){};
    public boolean isUsedIn(Class<? extends EventStrategy> eventType){
        return false;
    }
}
