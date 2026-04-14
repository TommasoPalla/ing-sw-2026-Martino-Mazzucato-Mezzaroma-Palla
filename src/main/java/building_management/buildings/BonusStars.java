package building_management.buildings;

import cards_and_deck.BuildingCard;
import enums.Effect;
import enums.GamePhase;

// During the Shamanic Ritual event, the owner's tribe has 3 additional stars
public class BonusStars extends BuildingCard {
    private final int starBonus;
    public BonusStars(int era, String cardID, int cost, GamePhase activatedAt, Effect effect,
                      String effectDescription, int prestige, int starBonus) {
        super(era, cardID, cost, activatedAt, effect, effectDescription, prestige);
        this.starBonus = starBonus;
    }

    @Override
    public void effectOnPurchase() {
        this.getOwner().getTribe().addShamansStars(this.starBonus);
    }
}
