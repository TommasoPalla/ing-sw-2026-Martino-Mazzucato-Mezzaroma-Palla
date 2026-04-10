package building_management.buildings;

import cards_and_deck.BuildingCard;
import enums.GamePhase;
import game.Game;

// After resolving all actions and before the End of the Round phase, the owner can take one Character or one Building
// card (paying its cost) from the top row
public class DrawAdditionalCard extends BuildingCard {
    public DrawAdditionalCard(int era, String name, String cardID, int cost, GamePhase activatedAt, String effectDescription, int prestige) {
        super(era, name, cardID, cost, activatedAt, effectDescription, prestige);
    }

    /* Se la topRow non è vuota, pesca una carta in più a fine turno
     */
    @Override
    public void applyEffect(){
        if(!Game.getInstance().getOfferTrack().getTopRow().isEmpty()) {
            owner.drawFromTopRow();
        }
    }
}
