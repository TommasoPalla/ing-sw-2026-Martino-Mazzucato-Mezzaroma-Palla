package it.polimi.ingsw.building_managementTest;

import it.polimi.ingsw.Model.BuildingsManagement.Buildings.BonusPoints;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.Cards.Buildings.BuildingCard;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Enums.Parameters;
import it.polimi.ingsw.Model.Game.Game;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.Model.Users.Player;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.EnumMap;
public class BuildingManagerTest {
    BuildingManager buildingManager;
    EnumMap<Parameters, Integer> mapca =  new EnumMap<>(Parameters.class);
    BuildingCard bonusPoints = new BonusPoints(2, "BP", 10, GamePhase.END_GAME, Effect.BONUS_POINTS, "+25 points", 25);

    @Test
    public void buildingManagerTest() {
        // GAME AND BUILDINGMANAGER INIT
        Player player1 =  new Player("giocatore1", Color.BLUE);
        Player player2 =  new Player("giocatore2", Color.RED);
        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        Game game = new Game(players);
        //buildingManager = Game.getInstance().getBuildingManager();

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
