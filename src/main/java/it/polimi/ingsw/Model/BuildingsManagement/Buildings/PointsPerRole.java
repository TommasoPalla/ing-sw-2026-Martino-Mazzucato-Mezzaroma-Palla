package it.polimi.ingsw.Model.BuildingsManagement.Buildings;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.Buildings.BuildingCard;

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

    @Override
    public CharacterRole getRoleEffect(){
        return this.roleEffect;
    }
    @Override
    public int getPrestigeBonus(){
        return this.prestigeBonus;
    }

    // Calculates the number of Character Cards of the specific role and multiplies it by the BonusPoints
    // parameter
    @Override
    public void applyEffect() {
        int characterNumber = this.getOwner().getTribe().getPopulation().get(this.roleEffect).size();
        this.getOwner().getTribe().modifyPrestigePoints(this.prestigeBonus * characterNumber);
    }
}
