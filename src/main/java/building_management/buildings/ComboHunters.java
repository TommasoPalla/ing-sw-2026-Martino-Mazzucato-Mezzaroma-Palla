package building_management.buildings;

import building_management.EffectContext;
import cards_and_deck.BuildingCard;
import enums.Parameters;
import enums.GamePhase;
import event_management.EventStrategy;
import event_management.HuntEvent;

// During the Hunt event, the owner takes 1 Food token and gains
// 1 additional Prestige Point for each Hunter in his tribe
public class ComboHunters extends BuildingCard {
    public ComboHunters(int era, String name, String cardID, int cost, GamePhase activatedAt,
                       String effectDescription, int prestige) {
        super(era, name, cardID, cost, activatedAt, effectDescription, prestige);
    }

    @Override
    public void applyEffect(EffectContext context) {
        int initialFoodBonus = context.getParam(Parameters.FOOD_BONUS);
        int initialPrestigeBonus = context.getParam(Parameters.PRESTIGE_BONUS);

        int huntersNumber = context.getPlayer().getTribe().getHuntersNumber();
        context.putParam(Parameters.FOOD_BONUS, huntersNumber + initialFoodBonus);
        context.putParam(Parameters.PRESTIGE_BONUS, huntersNumber + initialPrestigeBonus);
    }

    @Override
    public boolean isUsedIn(Class<? extends EventStrategy> eventType) {
        return eventType.equals(HuntEvent.class);
    }
}
