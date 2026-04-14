package building_management.buildings;

import building_management.EffectContext;
import cards_and_deck.BuildingCard;
import enums.CharacterRole;
import enums.Parameters;
import enums.GamePhase;
import event_management.CavePaintingsEvent;
import event_management.EventStrategy;

import java.util.EnumMap;

// During the Cave Paintings event, the owner takes 1 food token for every
// artist in his tribe
public class RoleFood extends BuildingCard {
    public RoleFood(int era, String cardID, int cost, GamePhase activatedAt, String effectDescription,
                    int prestige, CharacterRole effectRole, EnumMap<Parameters, Integer> foodBonus) {
        super(era, cardID, cost, activatedAt, effectDescription, prestige, effectRole, foodBonus);
    }
    @Override
    public void applyEffect(EffectContext context){
        int charactersPerRole = context.getPlayer().getTribe().getPopulation().get(this.getRoleEffect()).size();
        int currentFoodBonus = context.getParam(Parameters.FOOD_BONUS);
        int foodValue = charactersPerRole * this.getParam(Parameters.FOOD_BONUS) + currentFoodBonus;
        context.putParam(Parameters.FOOD_BONUS, foodValue);
    }

    @Override
    public boolean isUsedIn(Class<? extends EventStrategy> eventType) {
        return eventType.equals(CavePaintingsEvent.class);
    }
}
