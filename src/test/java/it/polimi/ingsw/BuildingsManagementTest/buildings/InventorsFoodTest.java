package it.polimi.ingsw.BuildingsManagementTest.buildings;

import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.InventorsFood;
import it.polimi.ingsw.Model.Cards.Buildings.BuildingCard;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Enums.*;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.Model.Users.Player;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.EnumMap;
public class InventorsFoodTest {
    BuildingManager buildingManager;
    EnumMap<Parameters, Integer> mapca =  new EnumMap<>(Parameters.class);
    BuildingCard inventorsFood = new InventorsFood(3, "BBB", 5, GamePhase.ON_DRAW, Effect.INVENTORS_FOOD, "ciao", 6, 3);

    @Test
    public void inventorsFoodTest() {
        // GAME AND BUILDINGMANAGER INIT
        Game game = null;
        Player player1 =  new Player(game,"giocatore1", Color.BLUE);
        Player player2 =  new Player(game,"giocatore2", Color.RED);
        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        game = new Game("1234", players);
        // INVENTORSFOOD TEST
        buildingManager = game.getBuildingManager();
        /*CharacterCard inv1 = new CharacterCard(1, "A", 3, CharacterRole.INVENTOR, null, InventorType.BOAT, null, null, null);
        CharacterCard inv2 = new CharacterCard(1, "B", 3, CharacterRole.INVENTOR, null, InventorType.NECKLACE, null, null, null);
        CharacterCard inv3 = new CharacterCard(1, "C", 3, CharacterRole.INVENTOR, null, InventorType.BOAT, null, null, null);
        */
        player2.getTribe().modifyFood(5);
        player2.getTribe().addBuildingToTribe(inventorsFood);
        //player2.getTribe().addCharacterToTribe(inv1);
        buildingManager.useBuilding(GamePhase.ON_DRAW, player2);
        //player2.getTribe().addCharacterToTribe(inv2);
        buildingManager.useBuilding(GamePhase.ON_DRAW, player2);
        assertEquals(0, player2.getTribe().getFoodReserve()); // No food should be gained, since the two inventors are of different types
        //player2.getTribe().addCharacterToTribe(inv3);
        buildingManager.useBuilding(GamePhase.ON_DRAW, player2);
        assertEquals(3, player2.getTribe().getFoodReserve()); // New couple of BOATS => 3 food bonus to the player's tribe
    }
}
