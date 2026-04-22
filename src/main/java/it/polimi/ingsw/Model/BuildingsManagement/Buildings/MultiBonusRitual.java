package it.polimi.ingsw.Model.BuildingsManagement.Buildings;

import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Enums.Parameters;
import it.polimi.ingsw.Model.BuildingsManagement.EffectContext;
import it.polimi.ingsw.Model.Cards.Buildings.BuildingCard;
import it.polimi.ingsw.Model.EventManagement.EventStrategy;
import it.polimi.ingsw.Model.EventManagement.ShamanicRitualEvent;

// If the owner is one of the winners of the Shamanic Ritual event
// he gains double the indicated Prestige Points
public class MultiBonusRitual extends BuildingCard {
    private final int multiplier;

    public MultiBonusRitual(int era, String cardID, int cost, GamePhase activatedAt, Effect effect,
                            String effectDescription, int multiplier) {    //no prestige
        super(era, cardID, cost, activatedAt, effect, effectDescription, 0);
        this.multiplier = multiplier;
    }

    @Override
    public int getMultiplier(){
        return this.multiplier;
    }

    @Override
    public void applyEffect(EffectContext context) {
        int ritualPoints = context.getParam(Parameters.PRESTIGE_BONUS);
        if(ritualPoints > 0) ritualPoints *= this.multiplier;     //potrebbe lanciare un eccezione
        context.putParam(Parameters.PRESTIGE_BONUS, ritualPoints);
    }

    @Override
    public boolean isUsedIn(Class<? extends EventStrategy> eventType) {
        return eventType.equals(ShamanicRitualEvent.class);
    }
}
