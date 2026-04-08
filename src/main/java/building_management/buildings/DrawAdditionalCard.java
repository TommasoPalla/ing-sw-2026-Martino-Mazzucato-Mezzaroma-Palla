package building_management.buildings;

import cards_and_deck.BuildingCard;
import enums.Era;
import enums.GamePhase;
import game_board.OfferTrack;

public class DrawAdditionalCard extends BuildingCard {
    public DrawAdditionalCard(Era era, String name, String cardID, int cost, GamePhase activatedAt, String effectDescription) {
        super(era, name, cardID, cost, activatedAt, effectDescription);
    }

    @Override
    public void applyEffect(){
        owner.drawFromTopRow();
    }
}
