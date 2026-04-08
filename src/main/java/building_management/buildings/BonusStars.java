package building_management.buildings;

import cards_and_deck.BuildingCard;
import enums.Era;
import enums.GamePhase;

public class BonusStars extends BuildingCard {
    public BonusStars(Era era, String name, String cardID, int cost, GamePhase activatedAt, String effectDescription) {
        super(era, name, cardID, cost, activatedAt, effectDescription);
    }

    /* Owner's tribe gets 3 bonus shaman stars
    *
     */
    @Override
    public void applyEffect() {
        owner.getTribe().addShamansStars(3);
    }
}
