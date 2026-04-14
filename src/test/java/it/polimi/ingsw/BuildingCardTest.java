package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.*;
import cards_and_deck.BuildingCard;
import cards_and_deck.CardLoader;

import java.util.logging.Logger;
import java.util.List;
import java.util.logging.Level;


public class BuildingCardTest {
    private List<BuildingCard> allBuildingCards;
    CardLoader loader = new CardLoader();

    @Test
    public void initBuildings(){
        this.allBuildingCards = loader.loadBuildings("json/test_card.json");
        if(!allBuildingCards.isEmpty()){
            assertEquals("E1_B_1", this.allBuildingCards.get(0).getCardID(), "CardID does not match");
            assertEquals("ON_DRAW", this.allBuildingCards.get(0).getActivatedAt());
        }
    }
}
