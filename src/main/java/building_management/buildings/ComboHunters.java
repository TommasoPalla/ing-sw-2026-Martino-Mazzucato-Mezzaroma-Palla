package building_management.buildings;

import building_management.EffectContext;
import cards_and_deck.BuildingCard;
import enums.CharacterRole;
import enums.Parameters;
import enums.GamePhase;
import event_management.EventStrategy;
import event_management.HuntEvent;

import java.util.EnumMap;

// During the Hunt event, the owner takes 1 Food token and gains
// 1 additional Prestige Point for each Hunter in his tribe
public class ComboHunters extends BuildingCard {
    public ComboHunters(int era, String cardID, int cost, GamePhase activatedAt, String effectDescription,
                        int prestige, CharacterRole role, EnumMap<Parameters, Integer> bonusFood) {
        super(era, cardID, cost, activatedAt, effectDescription, prestige, role, bonusFood);
    }

    @Override
    public void applyEffect(EffectContext context) {
        int initialFoodBonus = context.getParam(Parameters.FOOD_BONUS);
        int initialPrestigeBonus = context.getParam(Parameters.PRESTIGE_BONUS);

        //int huntersNumber = context.getPlayer().getTribe().getHuntersNumber();
        int charactersPerRole = context.getPlayer().getTribe().getPopulation().get(this.getRoleEffect()).size();
        context.putParam(Parameters.FOOD_BONUS, charactersPerRole + initialFoodBonus);
        context.putParam(Parameters.PRESTIGE_BONUS, charactersPerRole + initialPrestigeBonus);
    }

    @Override
    public boolean isUsedIn(Class<? extends EventStrategy> eventType) {
        return eventType.equals(HuntEvent.class);
    }
}
