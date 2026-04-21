package it.polimi.ingsw.building_management.buildings;

import building_management.BuildingManager;
import building_management.buildings.ArtistsFood;
import cards_and_deck.BuildingCard;
import cards_and_deck.CharacterCard;
import cards_and_deck.EventCard;
import enums.*;
import event_management.CavePaintingsEvent;
import game.Game;
import org.junit.jupiter.api.Test;
import users.Player;
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
        Player player1 =  new Player("giocatore1", Color.BLUE);
        Player player2 =  new Player("giocatore2", Color.RED);
        ArrayList<Player> players = new ArrayList<>();
        players.add(player1);
        players.add(player2);
        Game game = new Game(players);
        buildingManager = Game.getInstance().getBuildingManager();
        // CavePaintingsEvent INIT
        EnumMap<Parameters, Integer> inputPar = new EnumMap<>(Parameters.class);
        inputPar.put(Parameters.PRESTIGE_BONUS, 1);
        inputPar.put(Parameters.PRESTIGE_MALUS, 1);
        inputPar.put(Parameters.ARTIST_NUM, 1);
        EventCard cavePaintingsEventCard = new EventCard(1, "CV", EventType.CAVE_PAINTINGS, inputPar);
        CavePaintingsEvent cavePaintingsEvent = new CavePaintingsEvent();
        // ARTISTSFOOD TEST
        CharacterCard artist1 = new CharacterCard(1, "A1", 3, CharacterRole.ARTIST, null, null, null, null, null);
        CharacterCard artist2 = new CharacterCard(2, "A2", 3, CharacterRole.ARTIST, null, null, null, null, null);
        player1.getTribe().addCharacterToTribe(artist1);
        player1.getTribe().addCharacterToTribe(artist2);
        player1.getTribe().modifyFood(7);
        player1.getTribe().addBuildingToTribe(artistsFood);
        assertEquals(0, player1.getTribe().getFoodReserve());
        assertEquals(0, player1.getTribe().getPrestigePoints()); // before calling the event building the player has 0 food and 0 pp
        cavePaintingsEvent.apply(cavePaintingsEventCard, players, buildingManager);
        assertEquals(2, player1.getTribe().getFoodReserve());
        assertEquals(2, player1.getTribe().getPrestigePoints()); // player1 should have 2 more food thanks to the building and 2 pp
        assertEquals(-1, player2.getTribe().getPrestigePoints()); // player2 should lose 1 point because of the event
    }
}