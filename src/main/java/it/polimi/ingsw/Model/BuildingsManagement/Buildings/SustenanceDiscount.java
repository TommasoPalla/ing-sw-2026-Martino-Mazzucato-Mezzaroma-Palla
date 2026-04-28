package it.polimi.ingsw.Model.BuildingsManagement.Buildings;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.Parameters;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.BuildingsManagement.EffectContext;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.EventManagement.EventStrategy;
import it.polimi.ingsw.Model.EventManagement.SustenanceEvent;

public class SustenanceDiscount extends BuildingCard {
    private final CharacterRole roleEffect;
    private final int foodDiscount; //stored as a positive number

    public SustenanceDiscount(int era, String cardID, int cost, GamePhase activatedAt, Effect effect,
                              String effectDescription, int prestige, CharacterRole roleEffect, int foodDiscount) {
        super(era, cardID, cost, activatedAt, effect, effectDescription, prestige);
        this.roleEffect = roleEffect;
        this.foodDiscount = foodDiscount;
    }

    @Override
    public CharacterRole getRoleEffect(){
        return this.roleEffect;
    }
    @Override
    public int getFoodDiscount(){
        return this.foodDiscount;
    }

    // Calculates the number of Character Cards of the specific role and adds it
    // to the Food discount
    @Override
    public void applyEffect(EffectContext context) {
        int characterNumber = this.getOwner().getTribe().getPopulation().get(this.roleEffect).size();
        int currentFoodToPay = context.getParam(Parameters.FOOD_MALUS);
        context.putParam(Parameters.FOOD_MALUS, currentFoodToPay - characterNumber*this.foodDiscount);
    }

    @Override
    public boolean isUsedIn(Class<? extends EventStrategy> eventType) {
        return eventType.equals(SustenanceEvent.class);
    }
}