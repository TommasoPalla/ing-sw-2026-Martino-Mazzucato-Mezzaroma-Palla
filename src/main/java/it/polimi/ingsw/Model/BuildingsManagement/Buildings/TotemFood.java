package it.polimi.ingsw.Model.BuildingsManagement.Buildings;

import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.Parser.BuildingCardDTO;

// If at the end of every turn, when the owner moves his totem back to the Turn
// Order tile, they place it in a space that provides a bonus in food, they immediately
// take 1 additional Food token. The building has no effect if the totem is placed in the last place.
public class TotemFood extends BuildingCard {
    private Game game;
    private final int foodBonus;

    public TotemFood(Game gameInstance, int era, String cardID, int cost, GamePhase activatedAt, Effect effect,
                     String effectDescription, int prestige, int foodBonus) {
        super(era, cardID, cost, activatedAt, effect, effectDescription, prestige);
        this.game = gameInstance;
        this.foodBonus = foodBonus;
    }
    public TotemFood(BuildingCardDTO buildingData){
        super(buildingData.era, buildingData.cardID, buildingData.cost,
                buildingData.activatedAt, buildingData.effect, buildingData.effectDescription,
                buildingData.prestige);
        this.game = null;
        this.foodBonus = buildingData.foodBonus;
    }
    //da verificare
    public void updateGame(Game instance){
        this.game = instance;
    }

    @Override
    public int getFoodBonus(){
        return this.foodBonus;
    }

    @Override
    public void applyEffect() {
        int playerTurn = game.getTurnOrder().indexOf(this.getOwner());
        if(game.getOfferTrack().getTurnTile().getTileModifier()[playerTurn] > 0){
            this.getOwner().getTribe().modifyFood(this.foodBonus);
        }
    }
}
