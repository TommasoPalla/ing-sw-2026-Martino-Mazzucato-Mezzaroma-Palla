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
    public int getPrestigePoints() {
        return prestigePoints;
    }
    public int getFoodReserve() {
        return foodReserve;
    }
    public ArrayList<BuildingCard> getBuildings() {
        return buildings;
    }
    @Override
    public int getHuntersNumber() {
        return population.get(CharacterRole.HUNTER).size();
    }
    public int getBuilderDiscount() {
        return builderDiscount;
    }
    public int getGatherersDiscount() {
        return gatherersDiscount;
    }
    @Override
    public int getArtistsNumber() {
        return population.get(CharacterRole.ARTIST).size();
    }

    @Override
    public Map<CharacterRole, ArrayList<CharacterCard>> getPopulation() {
        return population;
    }

    @Override
    public Map<InventorType, Integer> getInventorsPerType() {
        return inventorsPerType;
    }

    public int getShamansStars() {
        return shamansStars;
    }
    public int getPopulationSize(){return populationSize;}

    //actual methods
    public void modifyPrestigePoints(int pp) {
        prestigePoints += pp;
    }
    public void modifyBuildingDiscount(int discount){ this.builderDiscount += discount; }

    public void modifyFood(int food) {
        if((foodReserve + food) < 0){
            modifyPrestigePoints(food + foodReserve);
            foodReserve = 0;
        }
        else foodReserve += food;
    }

    public void addShamansStars(int stars) {shamansStars += stars;}
    @Override
    public void addShamanStars(int stars) {
        shamansStars += stars;
    }
    @Override
    public void addBuilderDiscount(int discount) {
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
    @Override
    public void addFood(int food) {
        modifyFood(food);
    }
    @Override
    public void addPrestigePoints(int pp) {
        modifyPrestigePoints(pp);
    }

    public void modifyGatherersDiscount(int discount){ gatherersDiscount += discount; }
    public void addHunterFood(int food) { foodReserve += food; }
    // Character is added to the player's list
    // Called in Player
    public void addCharacterToTribe(CharacterCard character) {
        population.get(character.getRole()).add(character);
        character.applyEffect(this);
        populationSize += 1;
        game.getBuildingManager().useBuilding(GamePhase.ON_DRAW, this.tribeOwner);
    }

    // The owner of the building card is assigned and the building is added to the player's list
    // of buildings in his tribe, and to the player's list of the buildings activated at that specific
    // game phase, in BuildingManager.
    //
    // Called in Player.
    public void addBuildingToTribe(BuildingCard building) {
        //if(building.getCost() > foodReserve) return;
        buildings.add(building);
        building.assignOwner(tribeOwner);
        game.getBuildingManager().addBuilding(building, tribeOwner);
        // Calls effectOnPurchase for the building. It only works with the buildings who override it
        building.effectOnPurchase(this);
        this.foodReserve -= building.getCost();
        this.prestigePoints += building.getPrestige();
    }


    public int calculateFinalPoints() {

        // Points from builders
        int populationPoints = 0;
        for (CharacterCard character : population.get(CharacterRole.BUILDER)) {
            populationPoints = populationPoints + character.getPrestigePoints(); //prima c'era .orElse(0)
        }

        // Points from buildings
        int buildingPoints = 0;
        for (BuildingCard building : buildings) {
            buildingPoints += building.getPrestige();
        }

        // Points from artists
        int artistsPoints = (population.get(CharacterRole.ARTIST).size() / 2) * 10;

        // Points from inventors
        int numInventors = 0;
        for (InventorType invention : inventorsPerType.keySet()) {
            numInventors += inventorsPerType.get(invention);
        }
        int inventorsPoints = numInventors * inventorsPerType.size();

        return (artistsPoints + populationPoints + buildingPoints + inventorsPoints);
    }
}