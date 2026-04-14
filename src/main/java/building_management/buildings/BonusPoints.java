package building_management.buildings;
import cards_and_deck.BuildingCard;

import enums.CharacterRole;
import enums.GamePhase;
import enums.Parameters;

import java.util.EnumMap;

// At the end of the game, the owner gains 25 Prestige Points
public class BonusPoints extends BuildingCard {
    public BonusPoints(int era, String cardID, int cost, GamePhase activatedAt, String effectDescription, int prestige,
                       EnumMap<Parameters, Integer> inputPar) {
        super(era, cardID, cost, activatedAt, effectDescription, prestige, CharacterRole.NONE, inputPar);
    }

    @Override
    public void applyEffect() {
        owner.getTribe().modifyPrestigePoints(this.getParam(Parameters.PRESTIGE_BONUS));
    }
}
