package it.polimi.ingsw.Model.Cards;

import it.polimi.ingsw.Model.BuildingsManagement.EffectContext;
import it.polimi.ingsw.Enums.*;
import it.polimi.ingsw.Model.EventManagement.EventStrategy;
import it.polimi.ingsw.Model.GameBoard.OfferTrack;
import it.polimi.ingsw.Model.Users.*;

public abstract class BuildingCard extends Card {
    private final int cost; // food cost of the building card
    private final GamePhase activatedAt; // game phase during which this building card is activated
    private final Effect effect;    //da aggiungere al costruttore
    private final String effectDescription; //si potrebbe fare uno switch dentro il costruttore per alleggerire il JSON
    private final int prestige;
    private Player owner; // the owner of this building, assigned when the building is purchased

    // constructor, "overrides" Card constructor
    public BuildingCard(int era, String cardID, int cost, GamePhase activatedAt, Effect effect,
                        String effectDescription, int prestige){
        super(era, cardID);
        this.cost = cost;
        this.activatedAt = activatedAt;
        this.effect = effect;
        this.effectDescription = effectDescription;
        this.prestige = prestige;
        this.owner = null;
    }

    // getters for inherited attributes of this abstract class
    public int getCost(){
        return this.cost;
    }
    public GamePhase getActivatedAt(){ return this.activatedAt; }
    public Effect getEffect(){
        return this.effect;
    }
    public String getEffectDescription(){
        return this.effectDescription;
    }
    public int getPrestige(){return this.prestige; }
    public Player getOwner(){ return this.owner; }

    // abstract getters for child classes' specific attributes
    //mainly used for tests
    public int getFoodBonus(){return 0;}
    public int getPrestigeBonus(){return 0;}
    public int getStarBonus(){return 0;}
    public int getMultiplier(){return 0;}
    public CharacterRole getRoleEffect(){return CharacterRole.NONE;}
    public int getFoodDiscount(){return 0;}

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


    // Called in Tribe when the building is purchased. Used for buildings
    // "ComboFood","InventorsFood" and "BonusStars".
    public void effectOnPurchase(){}

    // The method is overridden in all buildings, the second one is used
    // by buildings related to events
    public void applyEffect(){}
    public void applyEffect(EffectContext context){}

    public boolean isUsedIn(Class<? extends EventStrategy> eventType){
        return false;
    }

    @Override
    public void accept(Visitor visitor){visitor.visitCard(this);}
}