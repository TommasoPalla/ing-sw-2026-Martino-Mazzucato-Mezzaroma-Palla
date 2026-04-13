package building_management.buildings;

import cards_and_deck.BuildingCard;
import cards_and_deck.CharacterCard;
import enums.CharacterRole;
import enums.GamePhase;

// At the end of the game, the owner gains double the Prestige Points indicated
// on the Builder cards in his tribe
public class DoublePointsBuilder extends BuildingCard {
    public DoublePointsBuilder(int era, String cardID, int cost, GamePhase activatedAt, String effectDescription, int prestige) {
        super(era, cardID, cost, activatedAt, effectDescription, prestige);
    }

    @Override
    public void applyEffect() {
        int builderPoints = 0;
        for (CharacterCard builder: owner.getTribe().getPopulation().get(CharacterRole.BUILDER)) {
            builderPoints += builder.getPrestigePoints().orElseThrow();
        }
        owner.getTribe().modifyPrestigePoints(builderPoints);
    }
}
