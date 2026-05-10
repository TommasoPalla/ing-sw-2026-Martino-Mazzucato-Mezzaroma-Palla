package it.polimi.ingsw.Model.Users;

import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.BonusPoints;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.Artist;
import it.polimi.ingsw.Model.Cards.Characters.Builder;

import it.polimi.ingsw.Model.Cards.Characters.Inventor;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Enums.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class TribeTest {
    Game game;
    Tribe tribe1;
    BuildingManager buildingManager;
    Player player1;
    Player player2;
    ArrayList<Player> players;

    @BeforeEach
    void setup(){
        game = new Game(0, 2);
        player1 = new Player(game, "pippo");
        player2 = new Player(game, "pippo");
        players = new ArrayList<>(Arrays.asList(player1, player2));
        for(Player player : players)
            game.addPlayer(player.getName());
        tribe1 = player1.getTribe();
        game.startGame();
        buildingManager = game.getBuildingManager();
    }

    @Test
    void initTribe(){
        assertEquals(player1, tribe1.getTribeOwner());
        assertEquals(0, tribe1.getPrestigePoints());
        assertEquals(0, tribe1.getFoodReserve());
        assertEquals(0, tribe1.getShamansStars());
        assertEquals(0, tribe1.getBuildings().size());
        assertEquals(0, tribe1.getBuilderDiscount());
        assertEquals(0, tribe1.getGatherersDiscount());

        for(CharacterRole role : CharacterRole.values()){
            assertEquals(0, tribe1.getPopulation().get(role).size());
        }
        for (InventorType type : InventorType.values()){
            assertNull(tribe1.getInventorsPerType().get(type));
        }
    }

    @Test
    void attributesModifyLogic(){
        tribe1.modifyPrestigePoints(10);
        assertEquals(10, tribe1.getPrestigePoints());

        tribe1.modifyFood(2);
        assertEquals(2, tribe1.getFoodReserve());

        tribe1.modifyFood(-4);
        assertEquals(0, tribe1.getFoodReserve());
        assertEquals(8, tribe1.getPrestigePoints());

        tribe1.addShamansStars(10);
        assertEquals(10, tribe1.getShamansStars());
    }

    @Test
    void charactersPoints(){
        Inventor inventor1 = new Inventor(1, "I1", 2, InventorType.BOAT);
        Inventor inventor2 = new Inventor(1, "I2", 2, InventorType.BOWL);
        Inventor inventor3 = new Inventor(1, "I3", 2, InventorType.NECKLACE);
        Inventor inventor4 = new Inventor(1, "I4", 2, InventorType.DOLL);

        tribe1.addCharacterToTribe(inventor1);
        tribe1.addCharacterToTribe(inventor1);
        assertEquals(2, tribe1.getInventorsPerType().get(inventor1.getInventorType()));
        assertEquals(2, tribe1.calculateFinalPoints());

        tribe1.addCharacterToTribe(inventor2);
        tribe1.addCharacterToTribe(inventor2);
        assertEquals(8, tribe1.calculateFinalPoints());

        tribe1.addCharacterToTribe(inventor3);
        tribe1.addCharacterToTribe(inventor4);
        assertEquals(24, tribe1.calculateFinalPoints());

        Builder builder = new Builder(1, "hello", 2, 1, 4);
        tribe1.addCharacterToTribe(builder);
        assertEquals(1, tribe1.getPopulation().get(builder.getRole()).size());
        assertEquals(25, tribe1.calculateFinalPoints());

        Artist artist = new Artist(3, "A5", 5);
        tribe1.addCharacterToTribe(artist);
        tribe1.addCharacterToTribe(artist);
        assertEquals(2, tribe1.getPopulation().get(artist.getRole()).size());
        assertEquals(35, tribe1.calculateFinalPoints());
    }
    @Test
    void buildingsPoints(){
        BuildingCard bonusPoints = new BonusPoints(3, "hello", 0, GamePhase.END_GAME, Effect.BONUS_POINTS, "description", 25);

        tribe1.addBuildingToTribe(bonusPoints);
        assertEquals(1, tribe1.getBuildings().size());
        assertEquals(GamePhase.END_GAME, tribe1.getBuildings().getFirst().getActivatedAt());
        assertEquals(player1, tribe1.getBuildings().getFirst().getOwner());

        tribe1.getBuildings().getFirst().applyEffect();
        assertEquals(25, tribe1.getPrestigePoints());

        game.getBuildingManager().useBuilding(GamePhase.END_GAME, player1);
        assertEquals(50, tribe1.getPrestigePoints());

    }
}
