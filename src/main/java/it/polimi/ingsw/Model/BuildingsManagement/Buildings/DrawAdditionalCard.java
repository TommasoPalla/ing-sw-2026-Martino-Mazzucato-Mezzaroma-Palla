package it.polimi.ingsw.Model.BuildingsManagement.Buildings;

import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.Parser.BuildingCardDTO;

// After resolving all actions and before the End of the Round phase, the owner can take one Character or one Building
// card (paying its cost) from the top row
public class DrawAdditionalCard extends BuildingCard {
    private Game game;
    public DrawAdditionalCard(Game gameInstance, int era, String cardID, int cost, GamePhase activatedAt, Effect effect,
                              String effectDescription, int prestige) {
        super(era, cardID, cost, activatedAt, effect, effectDescription, prestige);
        this.game = gameInstance;
    }
    public DrawAdditionalCard(BuildingCardDTO buildingData){
        super(buildingData.era, buildingData.cardID, buildingData.cost,
                buildingData.activatedAt, buildingData.effect, buildingData.effectDescription,
                buildingData.prestige);
        this.game = null;
    }
    //da verificare che abbia senso, nel costruttore non può andare
    public void updateGame(Game instance){
        this.game = instance;
    }

    /* Se la topRow non è vuota, pesca una carta in più a fine turno
     */
    @Override
    public void applyEffect(){

        int index=0;//provvisorio!!

        if(!game.getOfferTrack().getTopRow().isEmpty()) {
            this.getOwner().drawFromTopRow(index, game.getOfferTrack());
        }
    }
}
