package it.polimi.ingsw.UsersTest;

import it.polimi.ingsw.Model.BuildingsManagement.Buildings.BonusPoints;
import it.polimi.ingsw.Model.Cards.Buildings.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;

import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Enums.*;
import it.polimi.ingsw.Model.Users.Player;
import it.polimi.ingsw.Model.Users.Tribe;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class TribeTest {
    Game game;
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
        CharacterCard inventor = new CharacterCard(1, "hello", 2, CharacterRole.INVENTOR, 0, InventorType.BOAT, 0, 0, false);
        CharacterCard inventor2 = new CharacterCard(1, "hello", 2, CharacterRole.INVENTOR, 0, InventorType.BOWL, 0, 0, false);
        CharacterCard inventor3 = new CharacterCard(1, "hello", 2, CharacterRole.INVENTOR, 0, InventorType.NECKLACE, 0, 0, false);
        CharacterCard inventor4 = new CharacterCard(1, "hello", 2, CharacterRole.INVENTOR, 0, InventorType.DOLL, 0, 0, false);

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

        CharacterCard builder = new CharacterCard(1, "hello",2 , CharacterRole.BUILDER, 1,null, 0, 0, false);
        tribe.addCharacterToTribe(builder);
        assertEquals(1, tribe.getPopulation().get(builder.getRole()).size());
        assertEquals(25, tribe.calculateFinalPoints());

        CharacterCard artist = new CharacterCard(1, "hello", 2, CharacterRole.ARTIST, null, null, null, null, null);
        tribe.addCharacterToTribe(artist);
        tribe.addCharacterToTribe(artist);
        assertEquals(2, tribe.getPopulation().get(artist.getRole()).size());
        assertEquals(35, tribe.calculateFinalPoints());
    }
    @Test
    void buildingsPoints(){

        Player player2 = new Player("pluto", Color.RED);
        ArrayList<Player> players = new ArrayList<>();
        players.add(player);
        players.add(player2);
        Game game = new Game(players);
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
