package building_management.buildings;

import cards_and_deck.BuildingCard;
import enums.GamePhase;
import game.Game;

public class DrawAdditionalCard extends BuildingCard {
    public DrawAdditionalCard(int era, String name, String cardID, int cost, GamePhase activatedAt, String effectDescription) {
        super(era, name, cardID, cost, activatedAt, effectDescription);
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
