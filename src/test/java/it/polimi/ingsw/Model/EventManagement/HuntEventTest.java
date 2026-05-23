package it.polimi.ingsw.Model.EventManagement;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.ComboHunters;
import it.polimi.ingsw.Model.Cards.Characters.Hunter;
import it.polimi.ingsw.Model.EventManagement.HuntEvent;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.Users.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class HuntEventTest {

    private BuildingManager buildingManager;
    private Player player1;
    private Player player2;
    private ArrayList<Player> players;
    HuntEvent event = new HuntEvent(1, "H1", 1, 3);

    @BeforeEach
    void setUp() {
        Game game = new Game(0, 2);
        player1 = new Player(game, "pippo", Color.BLACK);
        player2 = new Player(game, "pluto", Color.BLUE);
        players = new ArrayList<>(Arrays.asList(player1, player2));
        for(Player player : players)
            game.addPlayer(player.getName());
        game.startGame();
        buildingManager = game.getBuildingManager();

        for(Player player : players)
            for(int i = 0; i < players.indexOf(player); i++)       //player 1 has 0 hunter, player 2 has 1 hunter
                player.getTribe().addCharacterToTribe(new Hunter(1, "HUNTER", 2, false));
    }

    @Test
    void standardHunt(){

        event.apply(event, players, buildingManager);

        assertEquals(0, player1.getTribe().getFoodReserve());
        assertEquals(0, player1.getTribe().getPrestigePoints());
        assertEquals(3, player2.getTribe().getFoodReserve());
        assertEquals(1, player2.getTribe().getPrestigePoints());
    }

    @Test
    void huntWithComboBuilding(){
        //Add another hunter to player2 (justifying the '*2' in the asserts)
        player2.getTribe().addCharacterToTribe(new Hunter(1, "HUNTER", 2, false));
        player2.getTribe().addBuildingToTribe(new ComboHunters(1, "CH1", 0, GamePhase.ON_EVENT, Effect.COMBO_HUNTERS, "", 0, 1, 1));

        event.apply(event, players, buildingManager);

        assertEquals(0, player1.getTribe().getFoodReserve());
        assertEquals(0, player1.getTribe().getPrestigePoints());
        assertEquals(3*2+1*2, player2.getTribe().getFoodReserve());
        assertEquals(1*2+1*2, player2.getTribe().getPrestigePoints());
    }
}
