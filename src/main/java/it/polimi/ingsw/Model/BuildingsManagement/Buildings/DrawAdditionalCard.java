package it.polimi.ingsw.Model.BuildingsManagement.Buildings;

import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.Parser.BuildingCardDTO;

/**
 * After resolving all actions and before the End of the Round phase, the owner can take one Character or one Building
 * card (paying its cost) from the top row. This building has no applyEffect method, since its effect is applied by
 * checking if it is present in a player's tribe.
 */
public class DrawAdditionalCard extends BuildingCard {
    private Game game;

    public DrawAdditionalCard(BuildingCardDTO buildingData){
        super(buildingData.era, buildingData.cardID, buildingData.cost,
                buildingData.activatedAt, buildingData.effect, buildingData.effectDescription,
                buildingData.prestige);
        this.game = null;
    }
    public void updateGame(Game instance){
        this.game = instance;
    }
}