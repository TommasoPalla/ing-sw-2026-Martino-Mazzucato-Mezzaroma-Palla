package building_management.buildings;

import building_management.EffectContext;
import cards_and_deck.BuildingCard;
import enums.Parameters;
import enums.GamePhase;
import event_management.CavePaintingsEvent;
import event_management.EventStrategy;

// During the Cave Paintings event, the owner takes 1 food token for every
// artist in his tribe
public class ArtistsFood extends BuildingCard {
    public ArtistsFood(int era, String cardID, int cost, GamePhase activatedAt,
                       String effectDescription, int prestige) {
        super(era, cardID, cost, activatedAt, effectDescription, prestige);
    }
    @Override
    public void applyEffect(EffectContext context){
        int artistsNumber = context.getPlayer().getTribe().getArtistsNumber();
        int currentFoodBonus = context.getParam(Parameters.FOOD_BONUS);
        context.putParam(Parameters.FOOD_BONUS, artistsNumber + currentFoodBonus);
    }

    @Override
    public boolean isUsedIn(Class<? extends EventStrategy> eventType) {
        return eventType.equals(CavePaintingsEvent.class);
    }
}
