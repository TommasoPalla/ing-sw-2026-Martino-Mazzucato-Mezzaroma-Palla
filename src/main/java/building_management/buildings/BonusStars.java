package building_management.buildings;

import cards_and_deck.BuildingCard;
import enums.CharacterRole;
import enums.GamePhase;

import java.util.EnumMap;

// During the Shamanic Ritual event, the owner's tribe has 3 additional stars
public class BonusStars extends BuildingCard {
    public BonusStars(int era, String cardID, int cost, GamePhase activatedAt, String effectDescription,
                      int prestige) {
        super(era, cardID, cost, activatedAt, effectDescription, prestige, CharacterRole.NONE, null);
    }

    @Override
    public void effectOnPurchase() {
        owner.getTribe().addShamansStars(3);
    }
}
