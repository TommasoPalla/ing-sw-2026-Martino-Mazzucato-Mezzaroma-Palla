package building_management.buildings;

import cards_and_deck.BuildingCard;
import enums.GamePhase;

// During the Shamanic Ritual event, the owner's tribe has 3 additional stars
public class BonusStars extends BuildingCard {
    public BonusStars(int era, String name, String cardID, int cost, GamePhase activatedAt, String effectDescription, int prestige) {
        super(era, name, cardID, cost, activatedAt, effectDescription, prestige);
    }

    @Override
    public void applyEffect() {
        owner.getTribe().addShamansStars(3);
    }
}
