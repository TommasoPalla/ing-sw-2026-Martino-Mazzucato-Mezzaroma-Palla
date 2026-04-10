package building_management.buildings;

import cards_and_deck.BuildingCard;
import enums.GamePhase;
import event_management.EventStrategy;

// If the owner is one of the winners of the Shamanic Ritual event
// he gains double the indicated Prestige Points
public class DoubleBonusRitual extends BuildingCard {
    private final EventStrategy activationEvent;
    public DoubleBonusRitual(int era, String name, String cardID, int cost, GamePhase activatedAt,
                             String effectDescription, int prestige,  EventStrategy activationEvent) {
        super(era, name, cardID, cost, activatedAt, effectDescription, prestige);
        this.activationEvent = activationEvent;
    }

    @Override
    public void applyEffect() {
        if (owner.getRitualWinnerBonus() != 0) {
            owner.getTribe().modifyPrestigePoints( owner.getRitualWinnerBonus() );
            owner.setRitualWinnerBonus(0);
        }
    }
}
