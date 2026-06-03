package it.polimi.ingsw.Model.EventManagement;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.InventorType;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.Cards.Characters.*;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.Users.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class EventManagerTest {
    private EventManager eventManager;
    ArrayList<EventCard> events = new ArrayList<>();

    private BuildingManager buildingManager;

    private Player player1;
    private Player player2;
    private ArrayList<Player> players;

    @BeforeEach
    void setup(){
        Game game = new Game(0, 2);
        eventManager = game.getEventManager();

        player1 = new Player(game, "pippo", Color.PURPLE);
        player2 = new Player(game, "pluto", Color.BLUE);
        players = new ArrayList<>(Arrays.asList(player1, player2));
        for(Player player : players){
            game.addPlayer(player.getName());
        }

        game.startGame();
        buildingManager = game.getBuildingManager();

        //Player 1 has: 1 artist, 2 hunter, 1 gatherer, 6 food
        player1.getTribe().modifyFood(6);
        player1.getTribe().addCharacterToTribe(new Artist(1, "ARTIST", 2));
        player1.getTribe().addCharacterToTribe(new Hunter(1, "HUNTER1", 2, false));
        player1.getTribe().addCharacterToTribe(new Hunter(1, "HUNTER2", 2, false));
        player1.getTribe().addCharacterToTribe(new Gatherer(1, "GATHERER1", 2, 3));

        //Player 2 has: 1 shaman, 2 inventors, 4 food
        player2.getTribe().modifyFood(4);
        player2.getTribe().addCharacterToTribe(new Shaman(1, "SHAMAN", 2, 3));
        player2.getTribe().addCharacterToTribe(new Inventor(1, "INVENTOR1", 2, InventorType.NECKLACE));
        player2.getTribe().addCharacterToTribe(new Inventor(1, "INVENTOR2", 2, InventorType.BOAT));
    }
    @Test
    void standardOrderedEvents(){
        events.add(new ShamanicRitualEvent(1, "SR", 10, 5));    //player1: -5pp, player2: +10pp
        events.add(new CavePaintingsEvent(1, "CP1", 1, 2, 3)); //player1: +2pp, player2: -3pp
        events.add(new HuntEvent(1, "H1", 1, 3));       //player1: +2pp +6food, player2: +0pp, +0food
        events.add(new SustenanceEvent(2, "S1", 1, 3)); //player1: -1food -0pp, player2: -3food -0pp

        eventManager.resolve(events, players, buildingManager);

        assertEquals(-1, player1.getTribe().getPrestigePoints());
        assertEquals(11, player1.getTribe().getFoodReserve());
        assertEquals(+7, player2.getTribe().getPrestigePoints());
        assertEquals(1, player2.getTribe().getFoodReserve());
    }

    @Test
    void wrongOrderEvents(){

        events.add(new SustenanceEvent(2, "S1", 1, 3));
        events.add(new ShamanicRitualEvent(1, "SR", 10, 5));
        events.add(new CavePaintingsEvent(1, "CP1", 1, 2, 3));
        events.add(new HuntEvent(1, "H1", 1, 3));

        eventManager.resolve(events, players, buildingManager);

        /* The results are the same because the rules impose that the SustenanceEvent
           must be resolved last even if is first in the list of events
         */
        assertEquals(-1, player1.getTribe().getPrestigePoints());
        assertEquals(11, player1.getTribe().getFoodReserve());
        assertEquals(+7, player2.getTribe().getPrestigePoints());
        assertEquals(1, player2.getTribe().getFoodReserve());

    }



}
