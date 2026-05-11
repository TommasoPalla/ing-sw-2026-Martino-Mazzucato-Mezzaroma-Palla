package it.polimi.ingsw.Model.BuildingsManagement.buildings;

import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.NoMalusRitual;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.Shaman;
import it.polimi.ingsw.Model.EventManagement.ShamanicRitualEvent;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.Users.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NoMalusRitualTest {
    Game game;
    Player player1;
    Player player2;
    ArrayList<Player> players;
    BuildingManager buildingManager;
    BuildingCard noMalus = new NoMalusRitual(1, "NM1", 5, GamePhase.ON_EVENT, Effect.NO_MALUS_RITUAL, "", 4);

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
    void noMalusStandard(){
        ShamanicRitualEvent shamanicRitualEvent = new ShamanicRitualEvent(1, "SR1", 10, 5);

        player1.getTribe().addCharacterToTribe(new Shaman(1, "SH1", 2, 3));

        player2.getTribe().modifyFood(2);                           //3 food because he is second +2 food = 5
        player2.getTribe().addBuildingToTribe(noMalus);    //-5 food = 0

        assertEquals(0, player2.getTribe().getFoodReserve());
        assertEquals(noMalus.getPrestige(), player2.getTribe().getPrestigePoints());    // before the event, the player has 0food and 4pp (from building)

        shamanicRitualEvent.apply(shamanicRitualEvent, players, buildingManager);

        assertEquals(shamanicRitualEvent.getPrestigeBonus(), player1.getTribe().getPrestigePoints());
        assertEquals(noMalus.getPrestige(), player2.getTribe().getPrestigePoints());    //player2 loses but no pp are detracted
    }

    @Test
    void winningWithBuilding(){
        ShamanicRitualEvent shamanicRitualEvent = new ShamanicRitualEvent(1, "SR1", 10, 5);

        player1.getTribe().addCharacterToTribe(new Shaman(1, "SH1", 2, 3));

        player1.getTribe().modifyFood(3);                           //2 food because he is second +3 food = 5
        player1.getTribe().addBuildingToTribe(noMalus);    //-5 food = 0

        assertEquals(0, player1.getTribe().getFoodReserve());
        assertEquals(noMalus.getPrestige(), player1.getTribe().getPrestigePoints());    // before the event, the player has 0food and 4pp (from building)

        shamanicRitualEvent.apply(shamanicRitualEvent, players, buildingManager);

        assertEquals(shamanicRitualEvent.getPrestigeBonus() + noMalus.getPrestige(), player1.getTribe().getPrestigePoints());
        assertEquals(-shamanicRitualEvent.getPrestigeMalus(), player2.getTribe().getPrestigePoints());
    }
}
