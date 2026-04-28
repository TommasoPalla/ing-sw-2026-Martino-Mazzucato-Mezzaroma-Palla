package it.polimi.ingsw.Model.BuildingsManagement.Buildings;

import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Parser.BuildingCardDTO;

// At the end of the game, the owner gains 25 Prestige Points
public class BonusPoints extends BuildingCard {
    private final int prestigeBonus;

    public BonusPoints(int era, String cardID, int cost, GamePhase activatedAt,
                       Effect effect, String effectDescription, int prestigeBonus) { //no prestige
        super(era, cardID, cost, activatedAt, effect, effectDescription, 0);
        this.prestigeBonus = prestigeBonus;
    }

    public BonusPoints(BuildingCardDTO buildingData){
        super(buildingData.era, buildingData.cardID, buildingData.cost,
                buildingData.activatedAt, buildingData.effect, buildingData.effectDescription,
                0);
        this.prestigeBonus = buildingData.prestigeBonus;
    }
    @Override
    public int getPrestigeBonus(){
        return this.prestigeBonus;
    }
    @Override
    public void applyEffect() {
        this.getOwner().getTribe().modifyPrestigePoints(this.prestigeBonus);
    }
}
