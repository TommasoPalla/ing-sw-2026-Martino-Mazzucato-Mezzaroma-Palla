package it.polimi.ingsw.Model.BuildingsManagement.Buildings;

import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Parser.BuildingCardDTO;
import it.polimi.ingsw.Model.Users.TribeInterface;

// During the Shamanic Ritual event, the owner's tribe has 3 additional stars
public class BonusStars extends BuildingCard {
    private final int starBonus;

    public BonusStars(int era, String cardID, int cost, GamePhase activatedAt, Effect effect,
                      String effectDescription, int prestige, int starBonus) {
        super(era, cardID, cost, activatedAt, effect, effectDescription, prestige);
        this.starBonus = starBonus;
    }
    public BonusStars(BuildingCardDTO buildingData){
        super(buildingData.era, buildingData.cardID, buildingData.cost,
                buildingData.activatedAt, buildingData.effect, buildingData.effectDescription,
                buildingData.prestige);
        this.starBonus = buildingData.starBonus;
    }
    @Override
    public int getStarBonus(){
        return this.starBonus;
    }

    @Override
    public void effectOnPurchase(TribeInterface tribe) {
        tribe.addShamansStars(this.starBonus);
    }
}
