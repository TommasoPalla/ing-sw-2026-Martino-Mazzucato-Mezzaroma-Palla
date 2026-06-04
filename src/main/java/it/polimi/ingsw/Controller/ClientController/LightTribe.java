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

    private int remainingAbove;
    private int remainingBelow;

    private boolean canDrawAdditional;

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
        this.remainingAbove = 0;
        this.remainingBelow = 0;
        this.canDrawAdditional = false;
    }

    /*
    * Getters
     */
    public String getPlayerName() { return playerName; }

    @Override
    public int getPrestigePoints() { return prestigePoints; }

    @Override
    public int getFoodReserve() { return foodReserve; }

    @Override
    public int getPopulationSize() { return populationSize; }

    @Override
    public Map<CharacterRole, ArrayList<CharacterCard>> getPopulation() { return population; }

    @Override
    public ArrayList<BuildingCard> getBuildings() { return buildings; }

    @Override
    public Map<InventorType, Integer> getInventorsPerType() { return inventorsPerType; }

    @Override
    public int getBuildersDiscount() { return builderDiscount; }

    @Override
    public int getGatherersDiscount() { return gatherersDiscount; }

    @Override
    public int getShamansStars() { return shamansStars; }

    @Override
    public int getHuntersNumber() { return population.get(CharacterRole.HUNTER).size(); }

    @Override
    public int getArtistsNumber() { return population.get(CharacterRole.ARTIST).size(); }

    @Override
    public int getBuildersNumber() {
        return population.get(CharacterRole.BUILDER).size();
    }

    @Override
    public int getGatherersNumber() {
        return population.get(CharacterRole.GATHERER).size();
    }

    @Override
    public int getShamansNumber() {
        return population.get(CharacterRole.SHAMAN).size();
    }

    public int getRemainingAbove() { return remainingAbove; }

    public int getRemainingBelow() { return remainingBelow; }

    public boolean getCanDrawAdditional() { return canDrawAdditional; }

    /*
    * Setters
     */
    public void setShamansStars(int stars) { this.shamansStars = stars; }
    public void setBuilderDiscount(int discount) { this.builderDiscount = discount; }
    public void setGathererDiscount(int discount) { this.gatherersDiscount = discount; }

    public void setRemainingDraws(int above, int below) {
        this.remainingAbove = above;
        this.remainingBelow = below;
    }

    public void decrementRemainingAbove() {
        if (remainingAbove > 0) remainingAbove--;
    }

    public void decrementRemainingBelow() {
        if (remainingBelow > 0) remainingBelow--;
    }

    @Override
    public void modifyFood(int food) {
        this.foodReserve += food;
    }
    @Override
    public void modifyPrestigePoints(int pp) { this.prestigePoints += pp; }
    @Override
    public void addShamansStars(int stars) { this.shamansStars += stars; }
    @Override
    public void addBuildersDiscount(int discount) { this.builderDiscount += discount; }
    @Override
    public void addGathererDiscount(int discount) { this.gatherersDiscount += discount; }

    public void setCanDrawAdditional(boolean canDrawAdditional) { this.canDrawAdditional = canDrawAdditional; }
    /**
     * Adds a character to the tribe and applies its immediate effects (resource bonuses/discounts).
     * @param character The character card to add.
     */
    public void addCharacter(CharacterCard character) {
        population.get(character.getRole()).add(character);
        populationSize++;
    }

    /**
     * Adds a building to the tribe, handles its cost (with discounts), prestige, and immediate purchase effects.
     * @param building The building card to add.
     */
    public void addBuilding(BuildingCard building) {
        buildings.add(building);
        if (building.getCardID().equals("E3_B_8"))
            this.canDrawAdditional = true;
    }

    @Override
    public void addInventor(InventorType type) {
        if (type != null && type != InventorType.NONE) {
            inventorsPerType.put(type, inventorsPerType.get(type) + 1);
        }
    }
}