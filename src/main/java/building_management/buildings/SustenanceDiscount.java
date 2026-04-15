package building_management.buildings;

import building_management.EffectContext;
import cards_and_deck.BuildingCard;
import enums.CharacterRole;
import enums.Effect;
import enums.Parameters;
import enums.GamePhase;
import event_management.EventStrategy;
import event_management.SustenanceEvent;

public class SustenanceDiscount extends BuildingCard {
    private final CharacterRole roleEffect;
    private final int foodDiscount; //stored as a positive number

    public SustenanceDiscount(int era, String cardID, int cost, GamePhase activatedAt, Effect effect,
                              String effectDescription, int prestige, CharacterRole roleEffect, int foodDiscount) {
        super(era, cardID, cost, activatedAt, effect, effectDescription, prestige);
        this.roleEffect = roleEffect;
        this.foodDiscount = foodDiscount;
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