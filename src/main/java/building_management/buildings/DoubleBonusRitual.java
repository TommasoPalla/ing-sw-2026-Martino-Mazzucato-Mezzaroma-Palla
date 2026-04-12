package building_management.buildings;

import building_management.EffectContext;
import cards_and_deck.BuildingCard;
import enums.GamePhase;
import enums.Parameters;
import event_management.EventStrategy;
import event_management.ShamanicRitualEvent;

// If the owner is one of the winners of the Shamanic Ritual event
// he gains double the indicated Prestige Points
public class DoubleBonusRitual extends BuildingCard {
    public DoubleBonusRitual(int era, String name, String cardID, int cost, GamePhase activatedAt,
                             String effectDescription, int prestige) {
        super(era, name, cardID, cost, activatedAt, effectDescription, prestige);
    }

    @Override
    public void applyEffect(EffectContext context) {
        int ritualPoints = context.getParam(Parameters.PRESTIGE_BONUS);
        if(ritualPoints > 0) ritualPoints *= 2;
        context.putParam(Parameters.PRESTIGE_BONUS, ritualPoints);
    }

    @Override
    public boolean isUsedIn(Class<? extends EventStrategy> eventType) {
        return eventType.equals(ShamanicRitualEvent.class);
    }
}
