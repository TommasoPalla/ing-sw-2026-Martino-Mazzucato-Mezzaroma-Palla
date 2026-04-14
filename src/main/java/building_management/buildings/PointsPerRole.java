package building_management.buildings;

import cards_and_deck.BuildingCard;
import enums.CharacterRole;
import enums.Effect;
import enums.GamePhase;

// At the end of the game, the owner gains the indicated amount of Prestige Points for each Character card
// of the indicated role in his tribe
public class PointsPerRole extends BuildingCard {
    private final CharacterRole roleEffect;
    private final int prestigeBonus;


    public PointsPerRole(int era, String cardID, int cost, GamePhase activatedAt, Effect effect,
                         String effectDescription, int prestige, CharacterRole roleEffect, int prestigeBonus) {
        super(era, cardID, cost, activatedAt, effect, effectDescription, prestige);
        this.roleEffect = roleEffect;
        this.prestigeBonus = prestigeBonus;
    }

    // Calculates the number of Character Cards of the specific role and multiplies it by the BonusPoints
    // parameter
    @Override
    public void applyEffect() {
        int characterNumber = owner.getTribe().getPopulation().get(role).size();
        owner.getTribe().modifyPrestigePoints(BonusPoints * characterNumber);
    }
}
