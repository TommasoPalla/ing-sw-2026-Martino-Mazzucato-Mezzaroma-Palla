package it.polimi.ingsw.Model.BuildingsManagement.buildings;

import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.ComboHunters;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.*;
import it.polimi.ingsw.Model.EventManagement.HuntEvent;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.Users.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class ComboHunterTest {
    Game game;
    Player player1;
    Player player2;
    ArrayList<Player> players;
    BuildingManager buildingManager;
    BuildingCard comboHunter = new ComboHunters(1, "CH1", 7, GamePhase.ON_EVENT, Effect.COMBO_HUNTERS, "", 2, 1, 2);

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
    void comboHuntersDefault(){
        HuntEvent huntEvent = new HuntEvent(1, "HE1", 1, 1);

        player1.getTribe().addCharacterToTribe(new Hunter(1, "H1", 2, false));
        player1.getTribe().addCharacterToTribe(new Hunter(1, "H2", 2, false));

        player1.getTribe().modifyFood(-2);      //take away 2 food because he is first, he now has 0 food
        player1.getTribe().addBuildingToTribe(comboHunter);

        huntEvent.apply(huntEvent, players, buildingManager);

        //food = 0 + 1 * 2 from hunt event + 1 * 2 from building = 4
        assertEquals(4, player1.getTribe().getFoodReserve());
        //pp = 1 * 2 from hunt event + 2 * 2 from building = 6
        assertEquals(6, player1.getTribe().getPrestigePoints());
        assertEquals(3, player2.getTribe().getFoodReserve());   //3 food because he is second
        assertEquals(0, player2.getTribe().getPrestigePoints());//0 pp gained
    }
}
