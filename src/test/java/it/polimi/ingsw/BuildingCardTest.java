package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import building_management.buildings.ComboFood;
import enums.Color;
import enums.Effect;
import enums.EventType;
import enums.GamePhase;
import event_management.SustenanceEvent;
import org.junit.jupiter.api.*;
import cards_and_deck.BuildingCard;
import cards_and_deck.CardLoader;
import users.Player;

import java.util.logging.Logger;
import java.util.List;
import java.util.logging.Level;


public class BuildingCardTest {
    private List<BuildingCard> allBuildingCards;
    CardLoader loader = new CardLoader();

    @Test
    public void initBuildings(){
        allBuildingCards = loader.loadBuildings("json/cards.json");
        if(!allBuildingCards.isEmpty()){
            //Logger logger = new Logger;
            //logger.log(Level.INFO, "cardID does Match"););
            assertEquals("E1_B_1", allBuildingCards.getFirst().getCardID(), "CardID does not match");
            assertEquals(1, allBuildingCards.getFirst().getEra(), "era does not match");
            assertEquals(4, allBuildingCards.getFirst().getCost(), "cost does not match");
            assertEquals(GamePhase.ON_DRAW, allBuildingCards.getFirst().getActivatedAt(), "activation time does not match");
            assertEquals(Effect.COMBO_FOOD, allBuildingCards.getFirst().getEffect(), "effect does not match");
            assertEquals(3, allBuildingCards.getFirst().getPrestige(), "prestige does not match");
            assertEquals("E3_B_8", allBuildingCards.getLast().getCardID());
        }
    }
    @Test
    public void otherMethods(){
        allBuildingCards = loader.loadBuildings("json/cards.json");
        Player player = new Player("provola", Color.RED);
        allBuildingCards.getFirst().assignOwner(player);
        assertEquals(player, allBuildingCards.getFirst().getOwner());
        assertTrue(allBuildingCards.get(3).isUsedIn(SustenanceEvent.class));
    }
}
