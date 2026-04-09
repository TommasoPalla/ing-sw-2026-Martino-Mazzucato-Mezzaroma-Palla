package building_management.buildings;

import cards_and_deck.BuildingCard;
import enums.GamePhase;

public class DrawAdditionalCard extends BuildingCard {
    public DrawAdditionalCard(int era, String name, String cardID, int cost, GamePhase activatedAt, String effectDescription) {
        super(era, name, cardID, cost, activatedAt, effectDescription);
    }

    @Override
    public void applyEffect(){
        owner.drawFromTopRow();
    }//da aggiungere controllo dimensione top row
}
