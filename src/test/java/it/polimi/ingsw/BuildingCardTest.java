package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.assertEquals;

import building_management.buildings.ComboFood;
import enums.Effect;
import enums.GamePhase;
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
        this.allBuildingCards = loader.loadBuildings("json/cardsTest.json");
        if(!allBuildingCards.isEmpty()){
            //Logger logger = new Logger;
            //logger.log(Level.INFO, "cardID does Match"););
            assertEquals("E1_B_1", this.allBuildingCards.get(0).getCardID(), "CardID does not match");
            assertEquals(1, this.allBuildingCards.get(0).getEra(), "era does not match");
            assertEquals(4, this.allBuildingCards.get(0).getCost(), "cost does not match");
            assertEquals(GamePhase.ON_DRAW, this.allBuildingCards.get(0).getActivatedAt(), "activation time does not match");
            assertEquals(Effect.COMBO_FOOD, this.allBuildingCards.get(0).getEffect(), "effect does not match");
            assertEquals(3, this.allBuildingCards.get(0).getPrestige(), "prestige does not match");
        }
    }
}
