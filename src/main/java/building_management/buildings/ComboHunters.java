package building_management.buildings;

import cards_and_deck.BuildingCard;
import enums.GamePhase;
import event_management.EventStrategy;

// During the Hunt event, the owner takes 1 Food token and gains
// 1 additional Prestige Point for each Hunter in his tribe
public class ComboHunters extends BuildingCard {
    private final EventStrategy activationEvent;
    public ComboHunters(int era, String name, String cardID, int cost, GamePhase activatedAt,
                       String effectDescription, int prestige, EventStrategy activationEvent) {
        super(era, name, cardID, cost, activatedAt, effectDescription, prestige);
        this.activationEvent = activationEvent;
    }

    @Override
    public void applyEffect() {
        owner.getTribe().modifyFood( owner.getTribe().getHuntersNumber());
        owner.getTribe().modifyPrestigePoints( owner.getTribe().getHuntersNumber());
    }
}
