package it.polimi.ingsw.Model.BuildingsManagement.buildings;

import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.BonusStars;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.ComboFood;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.Users.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BonusStarsTest {
    Game game;
    Player player1;
    Player player2;
    ArrayList<Player> players;
    BuildingManager buildingManager;
    BuildingCard bonusStars = new BonusStars(2, "BS", 6, GamePhase.ON_PURCHASE, Effect.BONUS_STARS, "", 0, 3);

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
    }

    @Test
    void bonusStarsStandard(){
        player1.getTribe().modifyFood(4);                      //2 food because he is first + 4 = 6
        player1.getTribe().addBuildingToTribe(bonusStars);     //-6 food = 0
        assertEquals(0, player1.getTribe().getFoodReserve());

        //after purchase INDEPENDENTLY of GamePhases the player receives the stars
        assertEquals(3, player1.getTribe().getShamansStars());
    }

}
