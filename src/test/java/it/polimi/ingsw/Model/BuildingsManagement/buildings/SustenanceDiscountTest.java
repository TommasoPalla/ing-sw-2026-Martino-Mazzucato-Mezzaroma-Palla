package it.polimi.ingsw.Model.BuildingsManagement.buildings;

import it.polimi.ingsw.Controller.GameController;
import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.SustenanceDiscount;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.Artist;
import it.polimi.ingsw.Model.Cards.Characters.Shaman;
import it.polimi.ingsw.Model.EventManagement.SustenanceEvent;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.Users.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SustenanceDiscountTest {
    Game game;
    GameController controller;
    Player player1;
    Player player2;
    ArrayList<Player> players;
    BuildingManager buildingManager;
    BuildingCard sustenanceDiscount = new SustenanceDiscount(1, "SD1", 7, GamePhase.ON_EVENT, Effect.SUSTENANCE_DISCOUNT, "", 5, CharacterRole.SHAMAN, 1);

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

        player1.getTribe().addCharacterToTribe(new Artist(1, "A1", 2));
        player1.getTribe().addCharacterToTribe(new Artist(2, "A2", 2));
        player1.getTribe().addCharacterToTribe(new Shaman(1, "S1", 2, 3));
        player1.getTribe().addCharacterToTribe(new Shaman(1, "S2", 2, 2));
        player1.getTribe().addCharacterToTribe(new Shaman(1, "S3", 2, 2));
    }

    @Test
    void standard(){
        SustenanceEvent sustenanceEvent = new SustenanceEvent(1, "SE", 1, 3);

        player1.getTribe().modifyFood(-2);      //take away 2 food because he is first, he now has 0 food
        player1.getTribe().addBuildingToTribe(sustenanceDiscount);

        assertEquals(0, player1.getTribe().getFoodReserve());
        assertEquals(0, player1.getTribe().getPrestigePoints());    // before the event, the player has 0food and 0pp

        //5 characters but the shamans pay 1 less (they are "free" during sustenance) => 2 food to pay
        player1.getTribe().modifyFood(2);
        sustenanceEvent.apply(sustenanceEvent, players, buildingManager);

        assertEquals(0, player1.getTribe().getFoodReserve());
        assertEquals(0, player1.getTribe().getPrestigePoints());
    }

    @Test
    void losingPp(){
        SustenanceEvent sustenanceEvent = new SustenanceEvent(1, "SE", 1, 3);

        player1.getTribe().modifyFood(-2);      //take away 2 food because he is first, he now has 0 food
        player1.getTribe().addBuildingToTribe(sustenanceDiscount);

        assertEquals(0, player1.getTribe().getFoodReserve());
        assertEquals(0, player1.getTribe().getPrestigePoints());    // before the event, the player has 0food and 0pp

        //5 characters but the shamans pay 1 less (they are "free" during sustenance) => 2 food to pay. Has 0 food => -2 * 3 pp
        sustenanceEvent.apply(sustenanceEvent, players, buildingManager);

        assertEquals(0, player1.getTribe().getFoodReserve());
        assertEquals(-6, player1.getTribe().getPrestigePoints());
    }
}
