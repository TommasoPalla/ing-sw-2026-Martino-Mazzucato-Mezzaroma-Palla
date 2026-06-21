package it.polimi.ingsw.Model.Cards;

import it.polimi.ingsw.Model.BuildingsManagement.EffectContext;
import it.polimi.ingsw.Enums.*;
import it.polimi.ingsw.Model.EventManagement.EventStrategy;
import it.polimi.ingsw.Model.Users.*;
import it.polimi.ingsw.Utils.Visitor;
import it.polimi.ingsw.View.TUIView.TuiIcons;

import java.util.Map;

/**
 * A building card, unlike a Character card, has a cost in Food that the player has to pay to acquire it, and it does
 * not increase the tribe's population number.
 * It has an effect that is activated during a specific {@link GamePhase} and it can provide Food, Prestige Points or
 * give the owner advantages like discounts during events, additional draws or possibility to avoid losing Prestige
 * Points. It extends the general class {@link Card}.
 */
public abstract class BuildingCard extends Card {
    private final int cost; // food cost of the building card
    private final GamePhase activatedAt; // game phase during which this building card is activated
    private final Effect effect;
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

    @Override
    public Map<String, String> getDisplayStats() {
        Map<String, String> stats = super.getDisplayStats();
        stats.put(TuiIcons.FOOD, String.valueOf(this.cost));
        stats.put(TuiIcons.PRESTIGE_POINTS, String.valueOf(prestige));
        if (activatedAt == GamePhase.END_TURN) stats.put(TuiIcons.END_PHASE, "");
        if (getFoodBonus() > 0) stats.put(TuiIcons.FOOD_BONUS + "+", String.valueOf(getFoodBonus()));
        if (getPrestigeBonus() > 0) stats.put(TuiIcons.PRESTIGE_POINTS + "+", String.valueOf(getPrestigeBonus()));
        if (getStarBonus() > 0) stats.put(TuiIcons.SHAMANS_STARS + "+", String.valueOf(getStarBonus()));
        return stats;
    }

    // actual functions
    /**
     * checkIP whether player has sufficient food to purchase this buildingCard
     */
//    public boolean isPurchasable(Player player){
//        int discountedCost = cost - player.getTribe().getBuildersDiscount();
//        int foodReserve = player.getTribe().getFoodReserve();
//        return foodReserve >= discountedCost;
//    }

    /**
     * When the Building card is acquired, the player who purchased it is set as its owner.
     * @param player the player who purchased it.
     */
    public void assignOwner(Player player){
        this.owner = player;
    }


    /**
     * Called in Tribe when the building is purchased. Used for buildings
     * "ComboFood","InventorsFood" and "BonusStars".
     * @param tribe the {@link TribeInterface} of the player who just acquired the Building card.
     */
    public void effectOnPurchase(TribeInterface tribe){}

    /**
     * It applies the effect of the building. The method is overridden in all buildings.
     */
    public void applyEffect(){}

    /**
     * It applies the effect of the building when its activation time is during a certain event.
     * @param context the {@link EffectContext} containing the information about the event.
     */
    public void applyEffect(EffectContext context){}

    public boolean isUsedIn(Class<? extends EventStrategy> eventType){
        return false;
    }

    @Override
    public void accept(Visitor visitor){visitor.visitCard(this);}
}