package building_management.buildings;

import building_management.EffectContext;
import cards_and_deck.BuildingCard;
import enums.Parameters;
import enums.GamePhase;
import enums.Effect;
import event_management.CavePaintingsEvent;
import event_management.EventStrategy;

// During the Cave Paintings event, the owner takes 1 food token for every
// artist in his tribe
public class ArtistsFood extends BuildingCard {
    private final int foodBonus;
    public ArtistsFood(int era, String cardID, int cost, GamePhase activatedAt, Effect effect,
                       String effectDescription, int prestige, int foodBonus) {
        super(era, cardID, cost, activatedAt, effect, effectDescription, prestige);
        this.foodBonus = foodBonus;
    }
    @Override
    public void applyEffect(EffectContext context){
        int artistsNumber = context.getPlayer().getTribe().getArtistsNumber();
        int currentFoodBonus = context.getParam(Parameters.FOOD_BONUS);
        context.putParam(Parameters.FOOD_BONUS, artistsNumber*this.foodBonus + currentFoodBonus);
    }

    @Override
    public boolean isUsedIn(Class<? extends EventStrategy> eventType) {
        return eventType.equals(CavePaintingsEvent.class);
    }
}
