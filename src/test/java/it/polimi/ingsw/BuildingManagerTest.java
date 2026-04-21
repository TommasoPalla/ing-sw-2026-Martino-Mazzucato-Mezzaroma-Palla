package it.polimi.ingsw;

import building_management.BuildingManager;
import building_management.buildings.BonusPoints;
import cards_and_deck.BuildingCard;
import enums.Color;
import enums.Effect;
import enums.GamePhase;
import enums.Parameters;
import game.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import users.Player;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.EnumMap;
public class BuildingManagerTest {
    BuildingManager buildingManager = new BuildingManager();
    EnumMap<Parameters, Integer> mapca =  new EnumMap<>(Parameters.class);
    BuildingCard bonusPoints = new BonusPoints(2, "BP", 10, GamePhase.END_GAME,
            Effect.BONUS_POINTS, "+25 points", 0);

    @Test
    public void buildingManagerTest() {
        Player player1 =  new Player("giocatore1", Color.BLUE);
        Player player2 =  new Player("giocatore2", Color.RED);
        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        Game game = new Game(players);
        player1.getTribe().modifyPrestigePoints(50);
        player1.getTribe().addBuildingToTribe(bonusPoints);
        assertEquals(player1.getTribe().getBuildings().getFirst(), bonusPoints);
        assertEquals(bonusPoints.getOwner(), player1);
        assertEquals(Game.getInstance().getBuildingManager().getBuildingsMap().get(bonusPoints.getActivatedAt()).get(player1).getFirst(), bonusPoints);
        buildingManager.useBuilding(GamePhase.END_GAME, player1);
        assertEquals(player1.getTribe().getPrestigePoints(), 75);
    }
}
