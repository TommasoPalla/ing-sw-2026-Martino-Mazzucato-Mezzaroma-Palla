package it.polimi.ingsw.Model.Users;

import it.polimi.ingsw.Model.BuildingsManagement.Buildings.BonusPoints;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.Artist;
import it.polimi.ingsw.Model.Cards.Characters.Builder;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;

import it.polimi.ingsw.Model.Cards.Characters.Inventor;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Enums.*;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TribeTest {
    Game game = new Game("12", 4);
    Tribe tribe = new Tribe(game);
    Player player = new Player(game, "aaa", Color.BLUE);
    @Test
    void initTribe(){
        assertEquals(player, tribe.getTribeOwner());
        assertEquals(0, tribe.getPrestigePoints());
        assertEquals(0, tribe.getFoodReserve());
        assertEquals(0, tribe.getShamansStars());
        assertEquals(0, tribe.getBuildings().size());
        assertEquals(0, tribe.getBuilderDiscount());
        assertEquals(0, tribe.getGatherersDiscount());

        for(CharacterRole role : CharacterRole.values()){
            assertEquals(0, tribe.getPopulation().get(role).size());
        }
        for (InventorType type : InventorType.values()){
            assertNull(tribe.getInventorsPerType().get(type));
        }
    }
    @Test
    void attributesModifyLogic(){
        tribe.modifyPrestigePoints(10);
        assertEquals(10, tribe.getPrestigePoints());

        tribe.setOwner(player);
        assertEquals(player, tribe.getTribeOwner());

        tribe.modifyFood(2);
        assertEquals(2, tribe.getFoodReserve());

        tribe.modifyFood(-4);
        assertEquals(0, tribe.getFoodReserve());
        assertEquals(8, tribe.getPrestigePoints());

        tribe.addShamansStars(10);
        assertEquals(10, tribe.getShamansStars());
    }
    @Test
    void charactersPoints(){
        Inventor inventor = new Inventor(1, "hello", 2, InventorType.BOAT);
        Inventor inventor2 = new Inventor(1, "hello", 2, InventorType.BOWL);
        Inventor inventor3 = new Inventor(1, "hello", 2, InventorType.NECKLACE);
        Inventor inventor4 = new Inventor(1, "hello", 2, InventorType.DOLL);

        tribe.addCharacterToTribe(inventor);
        tribe.addCharacterToTribe(inventor);
        assertEquals(2, tribe.getInventorsPerType().get(inventor.getInventorType()));
        assertEquals(2, tribe.calculateFinalPoints());

        tribe.addCharacterToTribe(inventor2);
        tribe.addCharacterToTribe(inventor2);
        assertEquals(8, tribe.calculateFinalPoints());

        tribe.addCharacterToTribe(inventor3);
        tribe.addCharacterToTribe(inventor4);
        assertEquals(24, tribe.calculateFinalPoints());

        Builder builder = new Builder(1, "hello", 2, 1, 4);
        tribe.addCharacterToTribe(builder);
        assertEquals(1, tribe.getPopulation().get(builder.getRole()).size());
        assertEquals(25, tribe.calculateFinalPoints());

        Artist artist = new Artist(3, "A5", 5);
        tribe.addCharacterToTribe(artist);
        tribe.addCharacterToTribe(artist);
        assertEquals(2, tribe.getPopulation().get(artist.getRole()).size());
        assertEquals(35, tribe.calculateFinalPoints());
    }
    @Test
    void buildingsPoints(){
        Game game = new Game("34", 5);
        Player player2 = new Player(game, "pluto", Color.RED);
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        players.add(player2);
        BuildingCard bonusPoints = new BonusPoints(3, "hello", 10, GamePhase.END_GAME, Effect.BONUS_POINTS, "description", 25);

        tribe.addBuildingToTribe(bonusPoints);
        assertEquals(1, tribe.getBuildings().size());
        assertEquals(GamePhase.END_GAME, tribe.getBuildings().getFirst().getActivatedAt());
        assertEquals(player, tribe.getBuildings().getFirst().getOwner());

        tribe.getBuildings().getFirst().applyEffect();
        assertEquals(25, tribe.getPrestigePoints());

        game.getBuildingManager().useBuilding(GamePhase.END_GAME, player);
        assertEquals(50, tribe.getPrestigePoints());

    }
}
