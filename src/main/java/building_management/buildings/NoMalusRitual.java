package building_management.buildings;

import building_management.EffectContext;
import cards_and_deck.BuildingCard;
import enums.GamePhase;
import enums.Parameters;
import event_management.EventStrategy;
import event_management.ShamanicRitualEvent;

// if the owner loses the Shamanic Ritual event, he doesn't lose any pp
public class NoMalusRitual extends BuildingCard {
    public NoMalusRitual(int era, String cardID, int cost, GamePhase activatedAt, String effectDescription, int prestige) {
        super(era, cardID, cost, activatedAt, effectDescription, prestige);
    }

    @Override
    public void applyEffect(EffectContext context) {
        int ritualPoints = context.getParam(Parameters.PRESTIGE_MALUS);
        if(ritualPoints < 0) context.putParam(Parameters.PRESTIGE_MALUS, 0);
    }

    @Override
    public boolean isUsedIn(Class<? extends EventStrategy> eventType) {
        return eventType.equals(ShamanicRitualEvent.class);
    }
}
