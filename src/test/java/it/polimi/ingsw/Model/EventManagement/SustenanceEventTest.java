package it.polimi.ingsw.Model.EventManagement;

import it.polimi.ingsw.Controller.GameController;
import it.polimi.ingsw.Enums.*;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.SustenanceDiscount;
import it.polimi.ingsw.Model.Cards.Characters.Artist;
import it.polimi.ingsw.Model.Cards.Characters.Gatherer;
import it.polimi.ingsw.Model.Cards.Characters.Hunter;
import it.polimi.ingsw.Model.Cards.Characters.Inventor;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.Users.Player;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SustenanceEventTest {
    Game game;
    GameController controller;
    private BuildingManager buildingManager;
    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;
    private ArrayList<Player> players;
    SustenanceEvent event = new SustenanceEvent(1, "S1", 1, 3);

    @BeforeEach
    void setUp() {
        game = new Game(0, 4);
        controller = new GameController(game);
        game.setController(controller);

        player1 = new Player(game, "pippo", Color.BLUE);
        player2 = new Player(game, "pluto", Color.PURPLE);
        player3 = new Player(game, "paperino", Color.RED);
        player4 = new Player(game, "topolino", Color.WHITE);
        players = new ArrayList<>(Arrays.asList(player1, player2, player3, player4));
        for(Player player : players)
            game.addPlayer(player.getName());
        game.startGame();

        //reset all players' food reserves to 0 for testing
        for(Player player : players)
            player.getTribe().modifyFood(-player.getTribe().getFoodReserve());

        buildingManager = game.getBuildingManager();
    }

    @Test
    void sustenanceApply() {
        // case food needed > food reserve
        player1.getTribe().modifyFood(4);
        for (int i = 0; i < 5; i++)
            player1.getTribe().addCharacterToTribe(new Artist(1, "A1", 2));
        assertEquals(5, player1.getTribe().getPopulationSize());

        //case discount > food reserve
        player2.getTribe().modifyFood(8);
        for (int i = 0; i < 5; i++)
            player2.getTribe().addCharacterToTribe(new Gatherer(1, "A1", 2, 3));
        assertEquals(5, player2.getTribe().getPopulationSize());

        //food reserve > food needed (standard case)
        player3.getTribe().modifyFood(10);
        for (int i = 0; i < 5; i++)
            player3.getTribe().addCharacterToTribe(new Inventor(1, "I1", 2, InventorType.NECKLACE));
        assertEquals(5, player3.getTribe().getPopulationSize());

        //food reserve > food needed (standard case) + Inventors do not pay
        player4.getTribe().modifyFood(10);
        for (int i = 0; i < 5; i++)
            player4.getTribe().addCharacterToTribe(new Inventor(1, "I1", 2, InventorType.NECKLACE));
        player4.getTribe().addBuildingToTribe(new SustenanceDiscount(1, "SD1", 2, GamePhase.ON_EVENT, Effect.SUSTENANCE_DISCOUNT, "mhh food", 3, CharacterRole.INVENTOR, 1));
        assertEquals(5, player4.getTribe().getPopulationSize());
        assertEquals(1, player4.getTribe().getBuildings().size());

        event.apply(event, players, buildingManager);

        assertEquals(0, player1.getTribe().getFoodReserve());
        assertEquals(-3, player1.getTribe().getPrestigePoints());

        assertEquals(8, player2.getTribe().getFoodReserve());
        assertEquals(0, player2.getTribe().getPrestigePoints());

        assertEquals(5, player3.getTribe().getFoodReserve());
        assertEquals(0, player3.getTribe().getPrestigePoints());

        assertEquals(0, player4.getTribe().getPrestigePoints());
        assertEquals(5, player4.getTribe().getPopulation().get(CharacterRole.INVENTOR).size());
        assertEquals(10, player4.getTribe().getFoodReserve());

    }

    @Test
    void artistsDiscount(){

        player1.getTribe().modifyFood(5);
        for (int i = 0; i < 3; i++)
            player1.getTribe().addCharacterToTribe(new Artist(1, "A1", 2));
        for (int i = 0; i < 2; i++)
            player1.getTribe().addCharacterToTribe(new Hunter(1, "H", 4, false));
        assertEquals(5, player1.getTribe().getPopulationSize());

        player1.getTribe().addBuildingToTribe(new SustenanceDiscount(1, "SD2", 2, GamePhase.ON_EVENT, Effect.SUSTENANCE_DISCOUNT, "F O O D", 3, CharacterRole.ARTIST, 1));
        //building correctly initialized
        assertEquals(CharacterRole.ARTIST, player1.getTribe().getBuildings().getFirst().getRoleEffect());
        assertEquals(3, player1.getTribe().getBuildings().getFirst().getOwner().getTribe().getArtistsNumber());
        assertEquals(2, player1.getTribe().getBuildings().getFirst().getOwner().getTribe().getHuntersNumber());
        assertEquals(1, player1.getTribe().getBuildings().getFirst().getFoodDiscount());

        event.apply(event, players, buildingManager);
        //he pays: 5 (population) - 3 (1 discount per artist) = 2   => 5 - 2 = 3
        assertEquals(3, player1.getTribe().getFoodReserve());
        assertEquals(0, player1.getTribe().getPrestigePoints());
    }
}