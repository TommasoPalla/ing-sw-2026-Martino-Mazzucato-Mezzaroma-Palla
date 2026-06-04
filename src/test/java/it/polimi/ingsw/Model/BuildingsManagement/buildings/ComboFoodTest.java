package it.polimi.ingsw.Model.BuildingsManagement.buildings;

import it.polimi.ingsw.Controller.GameController;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Enums.InventorType;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.ComboFood;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.*;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.Users.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ComboFoodTest {
    Game game;
    GameController controller;
    Player player1;
    Player player2;
    ArrayList<Player> players;
    BuildingManager buildingManager;
    BuildingCard comboFood = new ComboFood(1, "CF", 4, GamePhase.ON_DRAW, Effect.COMBO_FOOD, "", 3, 5);

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
        player1.getTribe().addCharacterToTribe(new Shaman(1, "S1", 2, 3));
        player1.getTribe().addCharacterToTribe(new Gatherer(1, "G1", 2, 3));
        player1.getTribe().addCharacterToTribe(new Inventor(1, "I1", 2, InventorType.NECKLACE));
    }

    @Test
    void comboFoodDefault(){
        player1.getTribe().modifyFood(-2);      //take away 2 food because he is first, he now has 0 food
        player1.getTribe().addBuildingToTribe(comboFood);

        //fifth character does not trigger the building
        player1.getTribe().addCharacterToTribe(new Builder(1, "B1", 2, 0, 3));
        assertEquals(0, player1.getTribe().getFoodReserve());

        //sixth character does trigger the building
        player1.getTribe().addCharacterToTribe(new Hunter(1, "H1", 2, false));
        assertEquals(comboFood.getFoodBonus(), player1.getTribe().getFoodReserve());
    }

    @Test
    void notACombo(){
        player1.getTribe().modifyFood(-2);      //take away 2 food because he is first, he now has 0 food
        player1.getTribe().addBuildingToTribe(comboFood);

        player1.getTribe().addCharacterToTribe(new Builder(1, "B1", 2, 0, 3));
        player1.getTribe().addCharacterToTribe(new Builder(1, "B1", 2, 0, 3));
        //at this moment player1 does not have a combo (missing a hunter)
        assertEquals(0, player1.getTribe().getFoodReserve());

        player1.getTribe().addCharacterToTribe(new Hunter(1, "H1", 2, false));
        assertEquals(comboFood.getFoodBonus(), player1.getTribe().getFoodReserve());
    }

    @Test
    void purchaseAfterCombo(){
        player1.getTribe().addCharacterToTribe(new Builder(1, "B1", 2, 0, 3));
        player1.getTribe().addCharacterToTribe(new Hunter(1, "H1", 2, false));

        player1.getTribe().modifyFood(-2);      //take away 2 food because he is first, he now has 0 food
        player1.getTribe().addBuildingToTribe(comboFood);

        //since player1 bought the building after completing a combo he doesn't get the bonus food
        assertEquals(0, player1.getTribe().getFoodReserve());
    }
}
