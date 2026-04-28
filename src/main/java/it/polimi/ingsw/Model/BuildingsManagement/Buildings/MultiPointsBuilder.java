package it.polimi.ingsw.Model.BuildingsManagement.Buildings;

import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;

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
    public int getMultiplier(){
        return this.multiplier;
    }

    @Override
    public void applyEffect() {
        int builderPoints = 0;
        for (CharacterCard builder: this.getOwner().getTribe().getPopulation().get(CharacterRole.BUILDER)) {
            builderPoints += builder.getPrestigePoints()*multiplier; // prima di aggiungere le classi personaggio c'era "orElseThrow()"
        }
        this.getOwner().getTribe().modifyPrestigePoints(builderPoints);
    }
}
