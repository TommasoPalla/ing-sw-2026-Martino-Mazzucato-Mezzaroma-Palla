package building_management.buildings;

import cards_and_deck.BuildingCard;
import enums.GamePhase;

public class ComboFood extends BuildingCard {
    public ComboFood(int era, String name, String cardID, int cost, GamePhase activatedAt, String effectDescription) {
        super(era, name, cardID, cost, activatedAt, effectDescription);
    }

    // da ragionare
    @Override
    public void applyEffect() {

    }
}

