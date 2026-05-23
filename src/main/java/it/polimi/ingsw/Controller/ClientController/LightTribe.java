package it.polimi.ingsw.Controller.ClientController;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.InventorType;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Users.TribeInterface;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

/**
 * Light version of the Tribe class, used on the client side to store the state of a player's tribe.
 * It mirrors the server-side Tribe logic to handle immediate effects when adding cards.
 */
public class LightTribe implements TribeInterface {
    private final String playerName;
    private int prestigePoints;
    private int foodReserve;
    private int populationSize;
    private final Map<CharacterRole, ArrayList<CharacterCard>> population;
    private final ArrayList<BuildingCard> buildings;
    private final EnumMap<InventorType, Integer> inventorsPerType;

    private int builderDiscount;
    private int gatherersDiscount;
    private int shamansStars;

    public LightTribe(String playerName) {
        this.playerName = playerName;
        this.prestigePoints = 0;
        this.foodReserve = 0;
        this.populationSize = 0;
        this.population = new EnumMap<>(CharacterRole.class);
        for (CharacterRole role : CharacterRole.values()) {
            if (role == CharacterRole.NONE) continue;
            population.put(role, new ArrayList<>());
        }
        this.buildings = new ArrayList<>();
        this.inventorsPerType = new EnumMap<>(InventorType.class);
        for (InventorType type : InventorType.values()) {
            if (type == InventorType.NONE) continue;
            inventorsPerType.put(type, 0);
        }
        this.builderDiscount = 0;
        this.gatherersDiscount = 0;
        this.shamansStars = 0;
    }

    //getters
    public String getPlayerName() { return playerName; }
    public int getPrestigePoints() { return prestigePoints; }
    public int getFoodReserve() { return foodReserve; }
    public int getPopulationSize() { return populationSize; }
    public Map<CharacterRole, ArrayList<CharacterCard>> getPopulation() { return population; }
    public ArrayList<BuildingCard> getBuildings() { return buildings; }
    public Map<InventorType, Integer> getInventorsPerType() { return inventorsPerType; }
    public int getBuilderDiscount() { return builderDiscount; }
    public int getGatherersDiscount() { return gatherersDiscount; }
    public int getShamansStars() { return shamansStars; }

    //setters
    public void setFood(int food) { this.foodReserve = food; }
    @Override
    public void addFood(int food) {
        if ((foodReserve + food) < 0) {
            prestigePoints += (food + foodReserve);
            foodReserve = 0;
        } else {
            foodReserve += food;
        }
    }
    @Override
    public void addPrestigePoints(int pp) { this.prestigePoints += pp; }
    public void setPrestigePoints(int pp) { this.prestigePoints = pp; }
    @Override
    public void addShamanStars(int stars) { this.shamansStars += stars; }
    @Override
    public void addBuilderDiscount(int discount) { this.builderDiscount += discount; }
    @Override
    public void addGathererDiscount(int discount) { this.gatherersDiscount += discount; }

    @Override
    public int getHuntersNumber() {
        return population.get(CharacterRole.HUNTER).size();
    }

    @Override
    public int getArtistsNumber() {
        return population.get(CharacterRole.ARTIST).size();
    }

    /**
     * Adds a character to the tribe and applies its immediate effects (resource bonuses/discounts).
     * @param character The character card to add.
     */
    public void addCharacter(CharacterCard character) {
        population.get(character.getRole()).add(character);
        populationSize++;
        character.applyEffect(this);
    }

    /**
     * Adds a building to the tribe, handles its cost (with discounts), prestige, and immediate purchase effects.
     * @param building The building card to add.
     */
    public void addBuilding(BuildingCard building) {
        buildings.add(building);
        
        int cost = Math.max(0, building.getCost() - gatherersDiscount);
        this.foodReserve -= cost;
        this.prestigePoints += building.getPrestige();

        building.effectOnPurchase(this);
    }

    @Override
    public void addInventor(InventorType type) {
        if (type != null && type != InventorType.NONE) {
            inventorsPerType.put(type, inventorsPerType.get(type) + 1);
        }
    }
}
