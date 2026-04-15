package building_management.buildings;

import cards_and_deck.BuildingCard;
import enums.Effect;
import enums.GamePhase;
import game.Game;

// If at the end of every turn, when the owner moves his totem back to the Turn
// Order tile, they place it in a space that provides a bonus in food, they immediately
// take 1 additional Food token. The building has no effect if the totem is placed in the last place.
public class TotemFood extends BuildingCard {
    private final int foodBonus;

    public TotemFood(int era, String cardID, int cost, GamePhase activatedAt, Effect effect,
                     String effectDescription, int prestige, int foodBonus) {
        super(era, cardID, cost, activatedAt, effect, effectDescription, prestige);
        this.foodBonus = foodBonus;
    }

    @Override
    public int getFoodBonus(){
        return this.foodBonus;
    }

    @Override
    public void applyEffect() {
        int playerTurn = Game.getInstance().getTurnOrder().indexOf(this.getOwner());
        if(Game.getInstance().getOfferTrack().getTurnTile().getTileModifier()[playerTurn] > 0){
            this.getOwner().getTribe().modifyFood(this.foodBonus);
        }
    }
}
