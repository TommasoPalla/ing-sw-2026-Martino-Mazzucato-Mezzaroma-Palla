package it.polimi.ingsw.Model.BuildingsManagement;

import it.polimi.ingsw.Model.BuildingsManagement.Buildings.BonusPoints;
import it.polimi.ingsw.Model.Cards.Buildings.BuildingCard;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Game.Game;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.Model.Users.Player;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

public class BuildingManagerTest {
    BuildingManager buildingManager;
    BuildingCard bonusPoints = new BonusPoints(2, "BP", 10, GamePhase.END_GAME, Effect.BONUS_POINTS, "+25 points", 25);

    @Test
    public void buildingManagerTest() {
        // GAME AND BUILDINGMANAGER INIT
        Map<String, Color> players = new HashMap<>();
        players.put("giocatore1", Color.BLUE);
        players.put("giocatore2", Color.RED);
        Game game = new Game("1234", players);
        Player player1 = game.getPlayers().getFirst();
        Player player2 = game.getPlayers().getLast();
        buildingManager = game.getBuildingManager();

        // BONUSPOINTS TEST
        player1.getTribe().modifyPrestigePoints(50);
        player1.getTribe().addBuildingToTribe(bonusPoints);
        assertEquals(player1.getTribe().getBuildings().getFirst(), bonusPoints); // checks if player's tribe saved the building
        assertEquals(bonusPoints.getOwner(), player1);
       /* assertEquals(buildingManager.getBuildingsMap().get(bonusPoints.getActivatedAt()).get(player1).getFirst(), bonusPoints); // checks if BuildingManager saved the building in the map
        buildingManager.useBuilding(GamePhase.END_GAME, player1);*/
        assertEquals(75, player1.getTribe().getPrestigePoints()); // checks if the building worked
    }
}
