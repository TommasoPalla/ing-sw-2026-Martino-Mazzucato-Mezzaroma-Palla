package it.polimi.ingsw.Model.BuildingsManagement;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.BonusPoints;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Game.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.Model.Users.Player;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

public class BuildingManagerTest {
    Game game;
    Player player1;
    Player player2;
    ArrayList<Player> players;
    BuildingManager buildingManager;
    BuildingCard bonusPoints = new BonusPoints(1, "BP1", 0, GamePhase.END_GAME, Effect.BONUS_POINTS, "", 25);


    @BeforeEach
    void setup(){
        game = new Game(0, 2);
        player1 =  new Player(game,"pippo", Color.BLACK);
        player2 =  new Player(game,"pluto", Color.RED);
        players = new ArrayList<>(Arrays.asList(player1, player1));
        for(Player player : players)
            game.addPlayer(player.getName());
        game.startGame();
        buildingManager = game.getBuildingManager();
    }

    @Test
    void initTest(){
        for(GamePhase gamePhase : GamePhase.values()){
            assertNotNull(buildingManager.getBuildingsMap().get(gamePhase));
            for(Player player : players){
                assertTrue(buildingManager.getBuildingsMap().get(gamePhase).containsKey(player.getName()));
                assertTrue(buildingManager.getBuildingsMap().get(gamePhase).get(player.getName()).isEmpty());
            }
        }
    }

    @Test
    public void buildingManagerTest() {

        player1.getTribe().modifyPrestigePoints(50);
        player1.getTribe().addBuildingToTribe(bonusPoints);

        assertEquals(bonusPoints, player1.getTribe().getBuildings().getFirst());
        assertEquals(player1, bonusPoints.getOwner());
        assertEquals(player1, player1.getTribe().getBuildings().getFirst().getOwner());

        assertEquals(bonusPoints, buildingManager.getBuildingsMap().get(bonusPoints.getActivatedAt()).get(player1.getName()).getFirst());

        buildingManager.useBuilding(GamePhase.END_GAME, player1);
        assertEquals(75, player1.getTribe().getPrestigePoints());
    }
}