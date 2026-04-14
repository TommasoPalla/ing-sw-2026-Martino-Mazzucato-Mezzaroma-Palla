package building_management.buildings;

import building_management.EffectContext;
import cards_and_deck.BuildingCard;
import enums.Effect;
import enums.Parameters;
import enums.GamePhase;
import event_management.EventStrategy;
import event_management.HuntEvent;

// During the Hunt event, the owner takes 1 Food token and gains
// 1 additional Prestige Point for each Hunter in his tribe
public class ComboHunters extends BuildingCard {

    private final int foodBonus;
    private final int prestigeBonus;
    public ComboHunters(int era, String cardID, int cost, GamePhase activatedAt, Effect effect,
                        String effectDescription, int prestige, int foodBonus, int prestigeBonus) {
        super(era, cardID, cost, activatedAt, effect, effectDescription, prestige);
        this.foodBonus = foodBonus;
        this.prestigeBonus = prestigeBonus;
    }

    @Override
    public void applyEffect(EffectContext context) {
        int initialFoodBonus = context.getParam(Parameters.FOOD_BONUS);
        int initialPrestigeBonus = context.getParam(Parameters.PRESTIGE_BONUS);

        int huntersNumber = context.getPlayer().getTribe().getHuntersNumber();
        context.putParam(Parameters.FOOD_BONUS, huntersNumber*this.foodBonus + initialFoodBonus);
        context.putParam(Parameters.PRESTIGE_BONUS, huntersNumber*this.prestigeBonus + initialPrestigeBonus);
    }

    @Override
    public boolean isUsedIn(Class<? extends EventStrategy> eventType) {
        return eventType.equals(HuntEvent.class);
    }
}
