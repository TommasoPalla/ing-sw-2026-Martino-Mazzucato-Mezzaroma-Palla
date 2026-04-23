package it.polimi.ingsw.Model.Users;

import it.polimi.ingsw.Model.Cards.Buildings.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.InventorType;
import it.polimi.ingsw.Model.Game.Game;

import java.util.ArrayList;
import java.util.EnumMap;

public class  Tribe {
    private final Game game;
    private Player tribeOwner;
    private int prestigePoints;
    private int foodReserve;
    private EnumMap<CharacterRole, ArrayList<CharacterCard>> population;
    private ArrayList<BuildingCard> buildings;
    private EnumMap<InventorType, Integer> inventorsPerType;

    private int builderDiscount = 0;
    private int gatherersDiscount = 0;
    private int shamansStars = 0;

    //Tribe's constructor
    public Tribe(Game gameInstance) {
        this.game = gameInstance;
        this.prestigePoints = 0;
        this.foodReserve = 0;
        this.population = new EnumMap<>(CharacterRole.class);
        for(CharacterRole role : CharacterRole.values()){
            population.put(role, new ArrayList<>());
        }
        this.buildings = new ArrayList<>();
        this.inventorsPerType = new EnumMap<>(InventorType.class);
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
    public EnumMap<CharacterRole, ArrayList<CharacterCard>> getPopulation() {
        return population;
    }
    public ArrayList<BuildingCard> getBuildings() {
        return buildings;
    }
    public int getHuntersNumber() {
        return population.get(CharacterRole.HUNTER).size();
    }
    public int getBuilderDiscount() {
        return builderDiscount;
    }
    public int getGatherersDiscount() {
        return gatherersDiscount;
    }
    public int getArtistsNumber() {
        return population.get(CharacterRole.ARTIST).size();
    }
    public int getShamansStars() {
        return shamansStars;
    }
    public EnumMap<InventorType, Integer> getInventorsPerType() {
        return inventorsPerType;
    }

    //actual methods
    public void modifyPrestigePoints(int pp) {
        prestigePoints += pp;
    }
    public void setOwner(Player player){this.tribeOwner=player;}
    public void modifyBuildingDiscount(int discount){ this.builderDiscount += discount; }

    public void modifyFood(int food) {
        if((foodReserve + food) < 0){
            modifyPrestigePoints(food + foodReserve);
            foodReserve = 0;
        }
        else foodReserve += food;
    }

    public void addShamansStars(int stars) {shamansStars += stars;}
    public void modifyGatherersDiscount(int discount){ gatherersDiscount += discount; }
    public void addHunterFood(int food) { foodReserve += food; }
    // Character is added to the player's list
    // Called in Player
    public void addCharacterToTribe(CharacterCard character) {
        population.get(character.getRole()).add(character);
        character.applyEffect(tribeOwner);
    }

    // The owner of the building card is assigned and the building is added to the player's list
    // of buildings in his tribe, and to the player's list of the buildings activated at that specific
    // game phase, in BuildingManager.
    //
    // Called in Player.
    public void addBuildingToTribe(BuildingCard building) {
        buildings.add(building);
        building.assignOwner(tribeOwner);
        game.getBuildingManager().addBuilding(building, tribeOwner);
        // Calls effectOnPurchase for the building. It only works with the buildings who override it
        building.effectOnPurchase();
        this.foodReserve -= building.getCost();
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