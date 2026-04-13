package cards_and_deck;

import building_management.EffectContext;
import enums.*;
import event_management.EventStrategy;
import users.*;


public class BuildingCard extends Card {
    private final int cost; // food cost of the building card
    private final GamePhase activatedAt; // game phase during which this building card is activated
    private final String effectDescription; //si potrebbe fare uno switch dentro il costruttore per alleggerire il JSON
    protected Player owner; // the owner of this building, assigned when the building is purchased
    private final int prestige;

    //private final CharacterRole roleEffect;

    // constructor, "overrides" Card constructor
    public BuildingCard(int era, String cardID, int cost, GamePhase activatedAt, String effectDescription, int prestige){
        super(era, cardID);
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


    // Called in Player when the building is purchased. Used for buildings
    // "ComboFood" and "InventorsFood"
    public void effectOnPurchase(){
    }

    // The method is overridden in all buildings, the second one is used
    // by buildings related to events
    public void applyEffect(){}
    public void applyEffect(EffectContext context){};
    public boolean isUsedIn(Class<? extends EventStrategy> eventType){
        return false;
    }
}
