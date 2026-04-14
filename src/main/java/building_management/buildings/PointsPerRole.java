package building_management.buildings;

import cards_and_deck.BuildingCard;
import enums.CharacterRole;
import enums.GamePhase;
import enums.Parameters;

import java.util.EnumMap;

// At the end of the game, the owner gains the indicated amount of Prestige Points for each Character card
// of the indicated role in his tribe
public class PointsPerRole extends BuildingCard {
    private final int BonusPoints;
    private final CharacterRole role;

    public PointsPerRole(int era, String cardID, int cost, GamePhase activatedAt, String effectDescription, int prestige,
                         int bonusPoints, CharacterRole role, EnumMap<Parameters, Integer> inputParam) {
        super(era, cardID, cost, activatedAt, effectDescription, prestige, role, inputParam);
        this.BonusPoints = bonusPoints;
        this.role = role;
    }

    // Calculates the number of Character Cards of the specific role and multiplies it by the BonusPoints
    // parameter
    @Override
    public void applyEffect() {
        int characterNumber = owner.getTribe().getPopulation().get(role).size();
        owner.getTribe().modifyPrestigePoints(BonusPoints * characterNumber);
    }
}
