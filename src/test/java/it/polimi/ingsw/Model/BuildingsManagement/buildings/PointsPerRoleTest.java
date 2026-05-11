package it.polimi.ingsw.Model.BuildingsManagement.buildings;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.PointsPerRole;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.Artist;
import it.polimi.ingsw.Model.Cards.Characters.Gatherer;
import it.polimi.ingsw.Model.Cards.Characters.Hunter;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.Users.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class PointsPerRoleTest {
    Game game;
    Player player1;
    Player player2;
    ArrayList<Player> players;
    BuildingManager buildingManager;
    BuildingCard pointsPerRole = new PointsPerRole(3, "PPR", 7, GamePhase.END_GAME, Effect.POINTS_PER_ROLE, "", 5, CharacterRole.ARTIST, 4);

    @BeforeEach
    void setup(){
        game = new Game(0, 2);
        game.addPlayer("pippo");
        game.addPlayer("pluto");
        player1 =  game.getPlayers().getFirst();
        player2 =  game.getPlayers().getLast();
        players = new ArrayList<>(Arrays.asList(player1, player2));
        game.startGameUnshuffled();
        buildingManager = game.getBuildingManager();

        player1.getTribe().addCharacterToTribe(new Artist(1, "A1", 2));
        player1.getTribe().addCharacterToTribe(new Artist(1, "A2", 2));
        player1.getTribe().addCharacterToTribe(new Artist(1, "A3", 2));
        player1.getTribe().addCharacterToTribe(new Gatherer(1, "G1", 2, 3));
        player1.getTribe().addCharacterToTribe(new Hunter(1, "H1", 2, false));
    }

    @Test
    void pointsPerRoleStandard(){
        player1.getTribe().modifyFood(5);       //2food because he is first + 5 = 7 food
        player1.getTribe().addBuildingToTribe(pointsPerRole);   //-7 food = 0

        assertEquals(0, player1.getTribe().getFoodReserve());       //2 food because he is first
        assertEquals(pointsPerRole.getPrestige(), player1.getTribe().getPrestigePoints());

        buildingManager.useBuilding(GamePhase.END_GAME, player1);
        assertEquals(pointsPerRole.getPrestige() +
                pointsPerRole.getPrestigeBonus() * player1.getTribe().getArtistsNumber(),
                player1.getTribe().getPrestigePoints());
    }

    @Test
    void incorrectPhase(){
        player1.getTribe().modifyFood(5);       //2food because he is first + 5 = 7 food
        player1.getTribe().addBuildingToTribe(pointsPerRole);   //-7 food = 0

        assertEquals(0, player1.getTribe().getFoodReserve());       //2 food because he is first
        assertEquals(pointsPerRole.getPrestige(), player1.getTribe().getPrestigePoints());

        buildingManager.useBuilding(GamePhase.ON_DRAW, player1);
        assertEquals(pointsPerRole.getPrestige(), player1.getTribe().getPrestigePoints());  //in a generic phase player1 doesn't get any extra pp
    }

    @Test
    void zeroCharacters(){
        player2.getTribe().modifyFood(4);       //3food because he is second + 4 = 7 food
        player2.getTribe().addBuildingToTribe(pointsPerRole);   //-7 food = 0

        assertEquals(0, player2.getTribe().getFoodReserve());
        assertEquals(pointsPerRole.getPrestige(), player2.getTribe().getPrestigePoints());

        buildingManager.useBuilding(GamePhase.END_GAME, player2);
        assertEquals(pointsPerRole.getPrestige(), player2.getTribe().getPrestigePoints());  //since player2 doesn't have any characters he doesn't get any extra pp
    }
}
