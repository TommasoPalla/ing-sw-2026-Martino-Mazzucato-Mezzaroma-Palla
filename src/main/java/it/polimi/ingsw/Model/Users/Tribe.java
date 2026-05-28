package it.polimi.ingsw.Model.Users;

import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.InventorType;
import it.polimi.ingsw.Model.Game.Game;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

public class  Tribe implements TribeInterface{
    private final Game game;
    private final Player tribeOwner;
    private int prestigePoints;
    private int foodReserve;
    private int populationSize = 0;
    private final EnumMap<CharacterRole, ArrayList<CharacterCard>> population;
    private final ArrayList<BuildingCard> buildings;
    private final EnumMap<InventorType, Integer> inventorsPerType;

    private int builderDiscount = 0;
    private int gatherersDiscount = 0;
    private int shamansStars = 0;

    //Tribe's constructor
    public Tribe(Game gameInstance, Player tribeOwner) {
        this.game = gameInstance;
        this.prestigePoints = 0;
        this.foodReserve = 0;
        this.population = new EnumMap<>(CharacterRole.class);
        for(CharacterRole role : CharacterRole.values()){
            population.put(role, new ArrayList<>());
        }
        this.buildings = new ArrayList<>();
        this.inventorsPerType = new EnumMap<>(InventorType.class);
        this.tribeOwner = tribeOwner;
    }

    //getters
    public Player getTribeOwner() {
        return tribeOwner;
    }
    @Override
    public int getPrestigePoints() { return prestigePoints; }

    @Override
    public int getFoodReserve() { return foodReserve; }

    @Override
    public ArrayList<BuildingCard> getBuildings() { return buildings; }

    @Override
    public int getHuntersNumber() { return population.get(CharacterRole.HUNTER).size(); }

    @Override
    public int getBuildersDiscount() { return builderDiscount; }

    @Override
    public int getGatherersDiscount() { return gatherersDiscount; }

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
    public Map<CharacterRole, ArrayList<CharacterCard>> getPopulation() { return population; }

    @Override
    public Map<InventorType, Integer> getInventorsPerType() { return inventorsPerType; }

    @Override
    public int getShamansNumber() {
        return population.get(CharacterRole.SHAMAN).size();
    }

    @Override
    public int getShamansStars() { return shamansStars; }

    @Override
    public int getPopulationSize(){ return populationSize; }

    @Override
    public void modifyPrestigePoints(int pp) { prestigePoints += pp; }

    @Override
    public void modifyFood(int food) {
        if((foodReserve + food) < 0){
            modifyPrestigePoints(food + foodReserve);
            foodReserve = 0;
        }
        else foodReserve += food;
    }

    @Override
    public void addShamansStars(int stars) {
        shamansStars += stars;
    }
    @Override
    public void addBuildersDiscount(int discount) {
        this.builderDiscount += discount;
    }
    @Override
    public void addGathererDiscount(int discount) {
        this.gatherersDiscount += discount;
    }
    @Override
    public void addInventor(InventorType type) {
        inventorsPerType.putIfAbsent(type, 0);
        inventorsPerType.put(type, inventorsPerType.get(type) + 1);
    }

    /**
     * Adds a character to this tribe
     * @param character the specific character to add
     */
    public void addCharacterToTribe(CharacterCard character) {
        population.get(character.getRole()).add(character);
        character.applyEffect(this);
        populationSize += 1;
        game.getBuildingManager().useBuilding(GamePhase.ON_DRAW, this.tribeOwner);
    }

    /** The owner of the building card is assigned and the building is added to the player's list
     * of buildings. The building is also registered in BuildingManager, for the specific player at
     * the specific activation phase (GamePhase)
     * @param building the building to add to the tribe
     */
    public void addBuildingToTribe(BuildingCard building) {
        buildings.add(building);
        building.assignOwner(tribeOwner);
        game.getBuildingManager().addBuilding(building, tribeOwner);
        building.effectOnPurchase(this);
        this.prestigePoints += building.getPrestige();
    }


    public int calculatePlayerFinalPoints() {

        int populationPoints = 0;
        for (CharacterCard character : population.get(CharacterRole.BUILDER)) {
            populationPoints = populationPoints + character.getPrestigePoints();
        }

        int buildingPoints = 0;
        for (BuildingCard building : buildings) {
            buildingPoints += building.getPrestige();
        }

        int artistsPoints = (population.get(CharacterRole.ARTIST).size() / 2) * 10;

        int numInventors = 0;
        for (InventorType invention : inventorsPerType.keySet()) {
            numInventors += inventorsPerType.get(invention);
        }
        int inventorsPoints = numInventors * inventorsPerType.size();

        return this.prestigePoints + artistsPoints + populationPoints + buildingPoints + inventorsPoints;
    }
}