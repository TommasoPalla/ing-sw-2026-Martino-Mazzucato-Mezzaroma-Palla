package it.polimi.ingsw.Model.BuildingsManagement.buildings;

import it.polimi.ingsw.Controller.GameController;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.MultiBonusRitual;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.MultiPointsBuilder;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.Builder;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.Users.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MultiPointsBuilderTest {
    Game game;
    GameController controller;
    Player player1;
    Player player2;
    ArrayList<Player> players;
    BuildingManager buildingManager;
    BuildingCard multiPointsBuilder = new MultiPointsBuilder(2, "MPB", 6, GamePhase.END_GAME, Effect.MULTIPLIER_BUILDER, "", 4, 2);

    @BeforeEach
    void setup(){
        game = new Game(0, 2);
        controller = new GameController(game);
        game.setController(controller);

        game.addPlayer("pippo");
        game.addPlayer("pluto");
        player1 =  game.getPlayers().getFirst();
        player2 =  game.getPlayers().getLast();
        players = new ArrayList<>(Arrays.asList(player1, player2));
        game.startGameUnshuffled();
        buildingManager = game.getBuildingManager();

        player1.getTribe().modifyFood(-2);      //take away 2 food because he is first, he now has 0 food
        player1.getTribe().addBuildingToTribe(multiPointsBuilder);
    }

    @Test
    void multiBuilderStandard(){
        player1.getTribe().addCharacterToTribe(new Builder(1, "B1", 2, 0, 2));
        player1.getTribe().addCharacterToTribe(new Builder(1, "B2", 2, 3, 1));
        buildingManager.useBuilding(GamePhase.END_GAME, player1);
        player1.getTribe().calculatePlayerFinalPoints();
        assertEquals(10, player1.getTribe().getPrestigePoints());   //3 * 2 from the building + 4 from purchasing the building
    }

    @Test
    void noBonus(){
        //with 0 builders or builders that gives 0 discount we can't se the building's effects
        player1.getTribe().addCharacterToTribe(new Builder(1, "B1", 2, 0, 2));
        buildingManager.useBuilding(GamePhase.END_GAME, player1);
        player1.getTribe().calculatePlayerFinalPoints();
        assertEquals(multiPointsBuilder.getPrestige(), player1.getTribe().getPrestigePoints());   //3 * 2 from the building + 4 from purchasing the building
    }
}
