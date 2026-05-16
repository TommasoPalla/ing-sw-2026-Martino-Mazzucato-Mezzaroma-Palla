package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.Users.Player;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {
    GameController controller = new GameController(new Game(1, 4));
    @Test
    void addPlayerTest() {
        //controller.addPlayer("pippo");
        ArrayList<Player> players = controller.getGameModel().getPlayers();
        System.out.println(players.getFirst().getName());
    }
}