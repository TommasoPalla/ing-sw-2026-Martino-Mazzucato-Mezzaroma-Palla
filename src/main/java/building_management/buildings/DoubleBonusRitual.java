package building_management.buildings;

import building_management.EffectContext;
import cards_and_deck.BuildingCard;
import enums.CharacterRole;
import enums.GamePhase;
import enums.Parameters;
import event_management.EventStrategy;
import event_management.ShamanicRitualEvent;

import java.util.EnumMap;

// If the owner is one of the winners of the Shamanic Ritual event
// he gains double the indicated Prestige Points
public class DoubleBonusRitual extends BuildingCard {
    public DoubleBonusRitual(int era, String cardID, int cost, GamePhase activatedAt,
                             String effectDescription, int prestige, EnumMap<Parameters, Integer> inputParam) {
        super(era, cardID, cost, activatedAt, effectDescription, prestige, CharacterRole.NONE, inputParam);
    }

    @Override
    public void applyEffect(EffectContext context) {
        int ritualPoints = context.getParam(Parameters.PRESTIGE_BONUS);
        if(ritualPoints > 0) ritualPoints *= this.getParam(Parameters.PRESTIGE_BONUS);  //prestige bonus e' un multiplier
        context.putParam(Parameters.PRESTIGE_BONUS, ritualPoints);
    }

    @Override
    public boolean isUsedIn(Class<? extends EventStrategy> eventType) {
        return eventType.equals(ShamanicRitualEvent.class);
    }
}
