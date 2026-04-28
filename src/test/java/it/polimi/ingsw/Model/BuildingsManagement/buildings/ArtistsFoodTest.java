package it.polimi.ingsw.Model.BuildingsManagement.buildings;

import it.polimi.ingsw.Model.BuildingsManagement.Buildings.ArtistsFood;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.Artist;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Model.EventManagement.CavePaintingsEvent;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Enums.*;
import org.junit.jupiter.api.Test;
import it.polimi.ingsw.Model.Users.Player;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.EnumMap;
public class ArtistsFoodTest {
    BuildingManager buildingManager;
    EnumMap<Parameters, Integer> mapca =  new EnumMap<>(Parameters.class);
    BuildingCard artistsFood = new ArtistsFood(2, "AF", 7, GamePhase.ON_EVENT, Effect.ARTISTS_FOOD, "ciao", 5, 1);

    @Test
    public void artistsFoodTest() {
        // GAME AND BUILDINGMANAGER INIT
        Game game = null;
        Player player1 =  new Player(game,"giocatore1", Color.BLUE);
        Player player2 =  new Player(game,"giocatore2", Color.RED);
        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        game = new Game("1234", players.size());
        buildingManager = game.getBuildingManager();
        // CavePaintingsEvent INIT
        EnumMap<Parameters, Integer> inputPar = new EnumMap<>(Parameters.class);
        inputPar.put(Parameters.PRESTIGE_BONUS, 1);
        inputPar.put(Parameters.PRESTIGE_MALUS, 1);
        inputPar.put(Parameters.ARTIST_NUM, 1);
        CavePaintingsEvent cavePaintingsEvent = new CavePaintingsEvent(1, "CV", 1, 1, 1);
        // ARTISTSFOOD TEST

        Artist artist1 = new Artist(1, "A1", 3);
        Artist artist2 = new Artist(2, "A2", 3);


        player1.getTribe().addCharacterToTribe(artist1);
        player1.getTribe().addCharacterToTribe(artist2);
        player1.getTribe().modifyFood(7);
        player1.getTribe().addBuildingToTribe(artistsFood);
        assertEquals(0, player1.getTribe().getFoodReserve());
        assertEquals(0, player1.getTribe().getPrestigePoints()); // before calling the event building the player has 0 food and 0 pp
        cavePaintingsEvent.apply(cavePaintingsEvent, players, buildingManager);
        assertEquals(2, player1.getTribe().getFoodReserve());
        assertEquals(2, player1.getTribe().getPrestigePoints()); // player1 should have 2 more food thanks to the building and 2 pp
        assertEquals(-1, player2.getTribe().getPrestigePoints()); // player2 should lose 1 point because of the event
    }
}