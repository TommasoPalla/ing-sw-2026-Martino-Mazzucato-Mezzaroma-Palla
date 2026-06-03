package it.polimi.ingsw.Model.EventManagement;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.ArtistsFood;
import it.polimi.ingsw.Model.Cards.Characters.Artist;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.Users.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class CavePaintingsTest {
    private BuildingManager buildingManager;
    private Player player1;
    private Player player2;
    private Player player3;
    private Player player4;
    private ArrayList<Player> players;
    //if a player has 0-1 artists he gets -2 prestige points, otherwise 1 prestige point per artist he has
    CavePaintingsEvent event = new CavePaintingsEvent(1, "SR1", 2, 1, 2);

    @BeforeEach
    void setUp() {
        Game game = new Game(0, 4);
        player1 = new Player(game, "pippo", Color.RED);
        player2 = new Player(game, "pluto", Color.BLUE);
        player3 = new Player(game, "paperino", Color.PURPLE);
        player4 = new Player(game, "topolino", Color.WHITE);
        players = new ArrayList<>(Arrays.asList(player1, player2, player3, player4));
        for(Player player : players)
            game.addPlayer(player.getName());
        game.startGame();
        buildingManager = game.getBuildingManager();

        for(Player player : players){
            for(int i = 0; i < players.indexOf(player); i++){       //player 1 has 0 artists, player 2 has 1 artist, ...
                player.getTribe().addCharacterToTribe(new Artist(1, "ARTIST", 4));
            }
        }
    }

    @Test
    void applyStandard(){

        event.apply(event, players, buildingManager);

        assertEquals(-2, player1.getTribe().getPrestigePoints());   //0 artists => -2 prestige
        assertEquals(-2, player2.getTribe().getPrestigePoints());   //1 artists => -2 prestige
        assertEquals(2, player3.getTribe().getPrestigePoints());    //2 artists => +2 prestige
        assertEquals(3, player4.getTribe().getPrestigePoints());    //3 artists => +3 prestige
    }

    @Test
    void applyArtistsFoodBuilding(){
        //building gives +1 food per artist during cave paintings event
        player1.getTribe().addBuildingToTribe(new ArtistsFood(1, "", 0, GamePhase.ON_EVENT, Effect.ARTISTS_FOOD, "", 0, 1));
        player2.getTribe().addBuildingToTribe(new ArtistsFood(1, "", 0, GamePhase.ON_EVENT, Effect.ARTISTS_FOOD, "", 0, 1));
        player4.getTribe().addBuildingToTribe(new ArtistsFood(1, "", 0, GamePhase.ON_EVENT, Effect.ARTISTS_FOOD, "", 0, 1));

        event.apply(event, players, buildingManager);

        assertEquals(0, player1.getTribe().getFoodReserve());       //0 artists => +0 food
        assertEquals(1, player2.getTribe().getFoodReserve());       //1 artist  => +1 food
        assertEquals(3, player4.getTribe().getFoodReserve());       //3 artists => +3 food
    }
}
