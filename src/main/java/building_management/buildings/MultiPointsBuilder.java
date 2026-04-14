package building_management.buildings;

import cards_and_deck.BuildingCard;
import cards_and_deck.CharacterCard;
import enums.CharacterRole;
import enums.Effect;
import enums.GamePhase;

// At the end of the game, the owner gains double the Prestige Points indicated
// on the Builder cards in his tribe
public class MultiPointsBuilder extends BuildingCard {
    private final int multiplier;
    public MultiPointsBuilder(int era, String cardID, int cost, GamePhase activatedAt, Effect effect,
                              String effectDescription, int prestige, int multiplier) {
        super(era, cardID, cost, activatedAt, effect, effectDescription, prestige);
        this.multiplier = multiplier;
    }

    @Override
    public void applyEffect() {
        int builderPoints = 0;
        for (CharacterCard builder: this.getOwner().getTribe().getPopulation().get(CharacterRole.BUILDER)) {
            builderPoints += builder.getPrestigePoints().orElseThrow()*multiplier;
        }
        this.getOwner().getTribe().modifyPrestigePoints(builderPoints);
    }
}
