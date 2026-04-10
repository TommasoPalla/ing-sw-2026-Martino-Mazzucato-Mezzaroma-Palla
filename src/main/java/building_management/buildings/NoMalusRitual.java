package building_management.buildings;

import cards_and_deck.BuildingCard;
import enums.GamePhase;

// if the owner loses the Shamanic Ritual event, he doesn't lose any pp
public class NoMalusRitual extends BuildingCard {
    public NoMalusRitual(int era, String name, String cardID, int cost, GamePhase activatedAt, String effectDescription, int prestige) {
        super(era, name, cardID, cost, activatedAt, effectDescription, prestige);
    }

    @Override
    public void applyEffect() {
        return ;
    }
}
