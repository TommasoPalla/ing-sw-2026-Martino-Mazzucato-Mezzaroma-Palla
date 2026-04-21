package it.polimi.ingsw;

import building_management.BuildingManager;
import building_management.buildings.ArtistsFood;
import building_management.buildings.BonusPoints;
import building_management.buildings.InventorsFood;
import cards_and_deck.BuildingCard;
import cards_and_deck.CharacterCard;
import enums.*;
import event_management.EventManager;
import game.Game;
import org.junit.jupiter.api.BeforeEach;
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
        // ARTISTSFOOD TEST
        player1.getTribe().addBuildingToTribe(artistsFood);
        buildingManager.useBuilding(GamePhase.ON_EVENT, player1);
    }
}
