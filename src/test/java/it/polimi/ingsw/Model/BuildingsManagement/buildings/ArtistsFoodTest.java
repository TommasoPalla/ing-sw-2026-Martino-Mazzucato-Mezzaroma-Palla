package it.polimi.ingsw.Model.BuildingsManagement.buildings;

import it.polimi.ingsw.Model.BuildingsManagement.Buildings.ArtistsFood;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.Artist;
import it.polimi.ingsw.Model.EventManagement.CavePaintingsEvent;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Enums.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.Model.Users.Player;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;

public class ArtistsFoodTest {
    Game game;
    Player player1;
    Player player2;
    ArrayList<Player> players;
    BuildingManager buildingManager;
    BuildingCard artistsFood = new ArtistsFood(2, "AF", 7, GamePhase.ON_EVENT, Effect.ARTISTS_FOOD, "", 5, 1);

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
    void artistsFoodTest() {
        CavePaintingsEvent cavePaintingsEvent = new CavePaintingsEvent(1, "CV", 1, 1, 1);

        player1.getTribe().addCharacterToTribe(new Artist(1, "A1", 2));
        player1.getTribe().addCharacterToTribe(new Artist(2, "A2", 2));

        player1.getTribe().modifyFood(5);                       //2 food because he is first +5 food = 7
        player1.getTribe().addBuildingToTribe(artistsFood);     //-7 food = 0

        assertEquals(0, player1.getTribe().getFoodReserve());
        assertEquals(5, player1.getTribe().getPrestigePoints());    // before the event, the player has 0food and 5pp (from building)

        cavePaintingsEvent.apply(cavePaintingsEvent, players, buildingManager);

        assertEquals(2, player1.getTribe().getFoodReserve());       //player1 should have 0 + 2*1 food (one for each artist)
        assertEquals(7, player1.getTribe().getPrestigePoints());    // player1 should have 2 more food thanks to the building and 2 pp
        assertEquals(-1, player2.getTribe().getPrestigePoints());   // player2 should lose 1 point because of the event
    }
}