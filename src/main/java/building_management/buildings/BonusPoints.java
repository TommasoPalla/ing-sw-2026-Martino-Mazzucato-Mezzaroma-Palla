package building_management.buildings;
import cards_and_deck.BuildingCard;

import enums.GamePhase;
import enums.Effect;

// At the end of the game, the owner gains 25 Prestige Points
public class BonusPoints extends BuildingCard {
    private final int prestigeBonus;
    public BonusPoints(int era, String cardID, int cost, GamePhase activatedAt,
                       Effect effect, String effectDescription, int prestigeBonus) { //no prestige
        super(era, cardID, cost, activatedAt, effect, effectDescription, 0);
        this.prestigeBonus = prestigeBonus;
    }

    @Override
    public void applyEffect() {
        this.getOwner().getTribe().modifyPrestigePoints(this.prestigeBonus);
    }
}
