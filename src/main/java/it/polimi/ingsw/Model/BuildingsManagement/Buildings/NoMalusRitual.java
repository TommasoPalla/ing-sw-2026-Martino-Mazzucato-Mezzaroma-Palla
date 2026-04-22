package it.polimi.ingsw.Model.BuildingsManagement.Buildings;

import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Enums.Parameters;
import it.polimi.ingsw.Model.BuildingsManagement.EffectContext;
import it.polimi.ingsw.Model.Cards.Buildings.BuildingCard;
import it.polimi.ingsw.Model.EventManagement.EventStrategy;
import it.polimi.ingsw.Model.EventManagement.ShamanicRitualEvent;

// if the owner loses the Shamanic Ritual event, he doesn't lose any pp
public class NoMalusRitual extends BuildingCard {
    public NoMalusRitual(int era, String cardID, int cost, GamePhase activatedAt, Effect effect,
                         String effectDescription, int prestige) {
        super(era, cardID, cost, activatedAt, effect, effectDescription, prestige);
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
