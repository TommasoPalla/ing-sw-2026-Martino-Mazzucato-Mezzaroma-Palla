package it.polimi.ingsw.Model.BuildingsManagement.buildings;

import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.BonusPoints;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.InventorsFood;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.Inventor;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.Model.Users.Player;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;

public class InventorsFoodTest {
    Game game;
    Player player1;
    Player player2;
    ArrayList<Player> players;
    BuildingManager buildingManager;
    BuildingCard inventorsFood = new InventorsFood(3, "BBB", 5, GamePhase.ON_DRAW, Effect.INVENTORS_FOOD, "ciao", 6, 3);

    @BeforeEach
    void setup(){
        game = new Game(0, 2);
        player1 =  new Player(game,"pippo");
        player2 =  new Player(game,"pluto");
        players = new ArrayList<>(Arrays.asList(player1, player2));
        for(Player player : players)
            game.addPlayer(player.getName());
        game.startGame();
        buildingManager = game.getBuildingManager();
    }

    @Test
    public void inventorsFoodTest() {
        player2.getTribe().modifyFood(5);
        player2.getTribe().addBuildingToTribe(inventorsFood);
        assertEquals(0, player2.getTribe().getFoodReserve());
        assertEquals(6, player2.getTribe().getPrestigePoints());

        player2.getTribe().addCharacterToTribe(new Inventor(1, "I1", 2, InventorType.NECKLACE));
        buildingManager.useBuilding(GamePhase.ON_DRAW, player2);
        assertEquals(0, player2.getTribe().getFoodReserve());

        player2.getTribe().addCharacterToTribe(new Inventor(1, "I1", 2, InventorType.BOAT));
        buildingManager.useBuilding(GamePhase.ON_DRAW, player2);
        assertEquals(0, player2.getTribe().getFoodReserve());   //different InventorTypes => no food gained

        player2.getTribe().addCharacterToTribe(new Inventor(1, "I1", 2, InventorType.BOAT));
        buildingManager.useBuilding(GamePhase.ON_DRAW, player2);
        assertEquals(3, player2.getTribe().getFoodReserve());   //new couple of BOATS => 3 food bonus
    }
}
