package it.polimi.ingsw.Model.BuildingsManagement.buildings;

import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.MultiBonusRitual;
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

public class MultiBonusRitualTest {
    Game game;
    Player player1;
    Player player2;
    ArrayList<Player> players;
    BuildingManager buildingManager;
    BuildingCard multiBonusRitual = new MultiBonusRitual(2, "MBR", 7, GamePhase.ON_EVENT, Effect.MULTIPLIER_RITUAL, "", 2);

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
    void standardMultiBonus(){
        ShamanicRitualEvent shamanicRitualEvent = new ShamanicRitualEvent(1, "SR1", 10, 5);

        player1.getTribe().addCharacterToTribe(new Shaman(1, "SH1", 2, 3));

        player1.getTribe().modifyFood(5);                           //2 food because he is first +5 food = 7
        player1.getTribe().addBuildingToTribe(multiBonusRitual);    //-7 food = 0

        assertEquals(0, player1.getTribe().getFoodReserve());
        assertEquals(multiBonusRitual.getPrestige(), player1.getTribe().getPrestigePoints());    // before the event, the player has 0food and 5pp (from building)

        shamanicRitualEvent.apply(shamanicRitualEvent, players, buildingManager);

        assertEquals(shamanicRitualEvent.getPrestigeBonus() * multiBonusRitual.getMultiplier(), player1.getTribe().getPrestigePoints());
        assertEquals(-shamanicRitualEvent.getPrestigeMalus(), player2.getTribe().getPrestigePoints());
    }

    @Test
    void losingWithBuilding(){
        ShamanicRitualEvent shamanicRitualEvent = new ShamanicRitualEvent(1, "SR1", 10, 5);

        player2.getTribe().addCharacterToTribe(new Shaman(1, "SH1", 2, 3));

        player1.getTribe().modifyFood(5);                           //2 food because he is first +5 food = 7
        player1.getTribe().addBuildingToTribe(multiBonusRitual);    //-7 food = 0

        assertEquals(0, player1.getTribe().getFoodReserve());
        assertEquals(multiBonusRitual.getPrestige(), player1.getTribe().getPrestigePoints());    // before the event, the player has 0food and 5pp (from building)

        shamanicRitualEvent.apply(shamanicRitualEvent, players, buildingManager);

        assertEquals(-shamanicRitualEvent.getPrestigeMalus(), player1.getTribe().getPrestigePoints());
        assertEquals(shamanicRitualEvent.getPrestigeBonus(), player2.getTribe().getPrestigePoints());
    }
}
