package it.polimi.ingsw.EventManagement;

import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.MultiBonusRitual;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.NoMalusRitual;
import it.polimi.ingsw.Model.EventManagement.ShamanicRitualEvent;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.Users.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;

public class ShamanicRitualTest {

    private BuildingManager buildingManager;
    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;
    private ArrayList<Player> players;
    ShamanicRitualEvent event = new ShamanicRitualEvent(1, "SR1", 10, 5);

    @BeforeEach
    void setUp() {
        Game game = new Game(0, 4);
        player1 = new Player(game, "pippo");
        player2 = new Player(game, "pluto");
        player3 = new Player(game, "paperino");
        player4 = new Player(game, "topolino");
        players = new ArrayList<>(Arrays.asList(player1, player2, player3, player4));
        for(Player player : players)
            game.addPlayer(player.getName());
        game.startGame();
        buildingManager = game.getBuildingManager();
    }

    @Test
    void applyStandard(){
        player1.getTribe().addShamansStars(10);     //winner
        player2.getTribe().addShamansStars(8);
        player3.getTribe().addShamansStars(4);
        player4.getTribe().addShamansStars(3);      //loser

        event.apply(event, players, buildingManager);

        assertEquals(10, player1.getTribe().getPrestigePoints());
        assertEquals(0, player2.getTribe().getPrestigePoints());
        assertEquals(0, player3.getTribe().getPrestigePoints());
        assertEquals(-5, player4.getTribe().getPrestigePoints());
    }

    @Test
    void multipleWinnersAndLosers(){
        player1.getTribe().addShamansStars(10);     //winner
        player2.getTribe().addShamansStars(10);     //winner
        player3.getTribe().addShamansStars(3);      //loser
        player4.getTribe().addShamansStars(3);      //loser

        event.apply(event, players, buildingManager);

        assertEquals(10, player1.getTribe().getPrestigePoints());
        assertEquals(10, player2.getTribe().getPrestigePoints());
        assertEquals(-5, player3.getTribe().getPrestigePoints());
        assertEquals(-5, player4.getTribe().getPrestigePoints());
    }

    @Test
    void allDraw(){
        player1.getTribe().addShamansStars(10);
        player2.getTribe().addShamansStars(10);
        player3.getTribe().addShamansStars(10);
        player4.getTribe().addShamansStars(10);

        event.apply(event, players, buildingManager);

        assertEquals(5, player1.getTribe().getPrestigePoints());
        assertEquals(5, player2.getTribe().getPrestigePoints());
        assertEquals(5, player3.getTribe().getPrestigePoints());
        assertEquals(5, player4.getTribe().getPrestigePoints());
    }

    @Test
    void noMalusBuilding(){
        player1.getTribe().addShamansStars(10);     //winner
        player2.getTribe().addShamansStars(10);     //winner
        player3.getTribe().addShamansStars(3);      //loser NO POINTS LOST
        player3.getTribe().addBuildingToTribe(new NoMalusRitual(1, "NMR1", 0, GamePhase.ON_EVENT, Effect.NO_MALUS_RITUAL, "", 0));
        player4.getTribe().addShamansStars(3);      //loser

        event.apply(event, players, buildingManager);

        assertEquals(10, player1.getTribe().getPrestigePoints());
        assertEquals(10, player2.getTribe().getPrestigePoints());
        assertEquals(0, player3.getTribe().getPrestigePoints());
        assertEquals(-5, player4.getTribe().getPrestigePoints());
    }

    @Test
    void doubleBonusBuilding(){
        player1.getTribe().addShamansStars(10);     //winner
        player2.getTribe().addShamansStars(10);     //winner DOUBLE POINTS
        player2.getTribe().addBuildingToTribe(new MultiBonusRitual(1, "MBS1", 0, GamePhase.ON_EVENT, Effect.MULTIPLIER_RITUAL, "", 2));
        player3.getTribe().addShamansStars(3);      //loser
        player4.getTribe().addShamansStars(3);      //loser

        event.apply(event, players, buildingManager);

        assertEquals(10, player1.getTribe().getPrestigePoints());
        assertEquals(20, player2.getTribe().getPrestigePoints());
        assertEquals(-5, player3.getTribe().getPrestigePoints());
        assertEquals(-5, player4.getTribe().getPrestigePoints());
    }
}
