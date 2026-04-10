package building_management.buildings;
import cards_and_deck.BuildingCard;

import enums.GamePhase;

// At the end of the game, the owner gains 25 Prestige Points
public class BonusPoints extends BuildingCard {
    public BonusPoints(int era, String name, String cardID, int cost, GamePhase activatedAt, String effectDescription, int prestige) {
        super(era, name, cardID, cost, activatedAt, effectDescription, prestige);
    }

    @Override
    public void applyEffect() {
        owner.getTribe().modifyPrestigePoints(25);
    }
}
