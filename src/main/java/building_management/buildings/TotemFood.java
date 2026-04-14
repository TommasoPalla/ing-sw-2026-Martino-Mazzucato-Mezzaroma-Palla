package building_management.buildings;

import cards_and_deck.BuildingCard;
import enums.GamePhase;
import game.Game;

// If at the end of every turn, when the owner moves his totem back to the Turn
// Order tile, they place it in a space that provides a bonus in food, they immediately
// take 1 additional Food token. The building has no effect if the totem is placed in the last place.
public class TotemFood extends BuildingCard {
    public TotemFood(int era, String cardID, int cost, GamePhase activatedAt, String effectDescription, int prestige) {
        super(era, cardID, cost, activatedAt, effectDescription, prestige);
    }

    @Override
    public void applyEffect() {
        int playerTurn = Game.getInstance().getTurnOrder().indexOf(owner);
        if(Game.getInstance().getOfferTrack().getTurnTile().getTileModifier()[playerTurn] > 0){
            owner.getTribe().modifyFood(1);
        }
    }
}
