package it.polimi.ingsw.Model.BuildingsManagement.buildings;

import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.ArtistsFood;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.BonusPoints;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.Users.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BonusPointsTest {
    Game game;
    Player player1;
    Player player2;
    ArrayList<Player> players;
    BuildingManager buildingManager;
    BuildingCard bonusPoints = new BonusPoints(3, "BP", 10, GamePhase.END_GAME, Effect.BONUS_POINTS, "", 25);

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
    void bonusPointsTest(){
        player1.getTribe().modifyFood(10);                      //+10 food
        player1.getTribe().addBuildingToTribe(bonusPoints);     //-10 food

        assertEquals(0, player1.getTribe().getFoodReserve());
        assertEquals(0, player1.getTribe().getPrestigePoints());    //before the end of the game, the player has 0food and 0pp

        buildingManager.useBuilding(GamePhase.ON_PURCHASE, player1);
        assertEquals(0, player1.getTribe().getPrestigePoints());    //during a generic phase the effect is not activated

        buildingManager.useBuilding(GamePhase.END_GAME, player1);

        assertEquals(25, player1.getTribe().getPrestigePoints());
    }
}
