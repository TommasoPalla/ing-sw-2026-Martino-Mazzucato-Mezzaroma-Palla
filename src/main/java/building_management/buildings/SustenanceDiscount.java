package building_management.buildings;

import building_management.EffectContext;
import cards_and_deck.BuildingCard;
import enums.CharacterRole;
import enums.Parameters;
import enums.GamePhase;
import event_management.EventStrategy;
import event_management.SustenanceEvent;

public class SustenanceDiscount extends BuildingCard {
    private final CharacterRole role;

    public SustenanceDiscount(int era, String name, String cardID, int cost, GamePhase activatedAt,
                               String effectDescription, int prestige, CharacterRole role) {
        super(era, name, cardID, cost, activatedAt, effectDescription, prestige);
        this.role = role;
    }

    // Calculates the number of Character Cards of the specific role and adds it
    // to the Food discount
    @Override
    public void applyEffect(EffectContext context) {
        int characterNumber = Math.toIntExact(owner.getTribe().getPopulation().stream()
                .filter(card -> card.getRole() == role)
                .count());
        int currentFoodToPay = context.getParam(Parameters.FOOD_MALUS);
        context.putParam(Parameters.FOOD_MALUS, currentFoodToPay - characterNumber);
    }

    @Override
    public boolean isUsedIn(Class<? extends EventStrategy> eventType) {
        return eventType.equals(SustenanceEvent.class);
    }
}