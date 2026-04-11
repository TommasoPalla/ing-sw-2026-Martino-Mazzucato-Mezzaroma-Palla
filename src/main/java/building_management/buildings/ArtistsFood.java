package building_management.buildings;

import building_management.EffectContext;
import cards_and_deck.BuildingCard;
import enums.ContextParameters;
import enums.GamePhase;
import event_management.EventStrategy;

// During the Cave Paintings event, the owner takes 1 food token for every
// artist in his tribe
public class ArtistsFood extends BuildingCard {
    private final EventStrategy activationEvent;
    public ArtistsFood(int era, String name, String cardID, int cost, GamePhase activatedAt,
                       String effectDescription, int prestige, EventStrategy activationEvent) {
        super(era, name, cardID, cost, activatedAt, effectDescription, prestige);
        this.activationEvent = activationEvent;
    }
    @Override
    public void applyEffect(EffectContext context){
        int artistsNumber = context.getPlayer().getTribe().getArtistsNumber();
        int currentFoodBonus = context.getParam(ContextParameters.FOOD_BONUS);
        context.putParam(ContextParameters.FOOD_BONUS, artistsNumber + currentFoodBonus);

        //owner.getTribe().modifyFood( owner.getTribe().getArtistsNumber() );
    };
}
