package it.polimi.ingsw.Model.EventManagement;

import it.polimi.ingsw.Enums.*;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.SustenanceDiscount;
import it.polimi.ingsw.Model.Cards.Characters.Artist;
import it.polimi.ingsw.Model.Cards.Characters.Gatherer;
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
    private BuildingManager buildingManager;
    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;
    private ArrayList<Player> players;
    SustenanceEvent event = new SustenanceEvent(1, "S1", 1, 3);

    @BeforeEach
    void setUp() {
        Game game = new Game(0, 4);
        player1 = new Player(game, "pippo", Color.BLUE);
        player2 = new Player(game, "pluto", Color.PURPLE);
        player3 = new Player(game, "paperino", Color.RED);
        player4 = new Player(game, "topolino", Color.WHITE);
        players = new ArrayList<>(Arrays.asList(player1, player2, player3, player4));
        for(Player player : players)
            game.addPlayer(player.getName());
        game.startGame();
        buildingManager = game.getBuildingManager();
    }

    @Test
    void sustenanceApply() {

        //food needed > food reserve
        player1.getTribe().modifyFood(4);
        for(int i = 0; i < 5; i++) player1.getTribe().addCharacterToTribe(new Artist(1, "A1", 2));
        assertEquals(5, player1.getTribe().getPopulationSize());

        //discount > food reserve
        player2.getTribe().modifyFood(8);
        for(int i = 0; i < 5; i++) player2.getTribe().addCharacterToTribe(new Gatherer(1, "A1", 2, 3));
        assertEquals(5, player2.getTribe().getPopulationSize());

        //food reserve > food needed (standard case)
        player3.getTribe().modifyFood(10);
        for(int i = 0; i < 5; i++) player3.getTribe().addCharacterToTribe(new Inventor(1, "I1", 2, InventorType.NECKLACE));
        assertEquals(5, player3.getTribe().getPopulationSize());

        //food reserve > food needed (standard case) + Inventors do not pay
        player4.getTribe().modifyFood(10);
        for(int i = 0; i < 5; i++) player4.getTribe().addCharacterToTribe(new Inventor(1, "I1", 2, InventorType.NECKLACE));
        player4.getTribe().addBuildingToTribe(new SustenanceDiscount(1, "SD1", 2, GamePhase.ON_EVENT, Effect.SUSTENANCE_DISCOUNT,"mhh food", 3, CharacterRole.INVENTOR, 1));
        assertEquals(5, player4.getTribe().getPopulationSize());
        assertEquals(1, player4.getTribe().getBuildings().size());

        event.apply(event, players, buildingManager);

        assertEquals(0, player1.getTribe().getFoodReserve());
        assertEquals(-3, player1.getTribe().getPrestigePoints());

        assertEquals(8, player2.getTribe().getFoodReserve());
        assertEquals(0, player2.getTribe().getPrestigePoints());

        assertEquals(5, player3.getTribe().getFoodReserve());
        assertEquals(0, player3.getTribe().getPrestigePoints());

        assertEquals(3, player4.getTribe().getPrestigePoints());
        assertEquals(5, player4.getTribe().getPopulation().get(CharacterRole.INVENTOR).size());
        assertEquals(8, player4.getTribe().getFoodReserve());

        player1.getTribe().modifyFood(3);   //qui ha 5 cibo
        player1.getTribe().addBuildingToTribe(new SustenanceDiscount(1, "SD2", 2, GamePhase.ON_EVENT, Effect.SUSTENANCE_DISCOUNT, "F O O D", 3, CharacterRole.ARTIST, 1));
        //building correctly initialized
        assertEquals(CharacterRole.ARTIST, player1.getTribe().getBuildings().getFirst().getRoleEffect());
        assertEquals(5, player1.getTribe().getBuildings().getFirst().getOwner().getTribe().getPopulation().get(CharacterRole.ARTIST).size());
        assertEquals(1, player1.getTribe().getBuildings().getFirst().getFoodDiscount());

        event.apply(event, players, buildingManager);
        //event correctly applies
        assertEquals(1, player1.getTribe().getFoodReserve());
        assertEquals(0, player1.getTribe().getPrestigePoints());
    }
}