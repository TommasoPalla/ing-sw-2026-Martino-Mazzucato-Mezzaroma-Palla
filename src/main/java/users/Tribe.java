package users;

import cards_and_deck.BuildingCard;
import cards_and_deck.CharacterCard;
import enums.CharacterRole;
import enums.InventorType;
import game.Game;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

public class  Tribe {
    private final Player tribeOwner;
    private int prestigePoints;
    private int foodReserve;
    private EnumMap<CharacterRole, ArrayList<CharacterCard>> population;
    private ArrayList<BuildingCard> buildings;
    private EnumMap<InventorType, Integer> inventorsPerType;

    private int builderDiscount = 0;
    private int gatherersDiscount = 0;
    private int shamansStars = 0;

    //Tribe's constructor
    public Tribe(Player tribeOwner) {
        this.tribeOwner = tribeOwner;
        this.prestigePoints=0;
        this.foodReserve=0;
        this.population=null;
        this.buildings=null;
        this.inventorsPerType=null;
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
    public Map<CharacterRole, ArrayList<CharacterCard>> getPopulation() {
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

    public void modifyFood(int food) {
        if((foodReserve + food) < 0){
            foodReserve = 0;
            modifyPrestigePoints(-1);
        }
        else foodReserve += food;
    }

    public void addShamansStars(int stars) {shamansStars += stars;}

    // Character is added to the player's list
    // Called in Player (?)
    public void addCharacterToTribe(CharacterCard character) {
        population.get(character.getRole()).add(character);
        switch(character.getRole()){
            case BUILDER:
                builderDiscount += character.getBuildingDiscount().orElseThrow();
                break;
            case GATHERER:
                gatherersDiscount += 3;
                break;
            case SHAMAN:
                shamansStars += character.getShamanStars().orElseThrow();
                break;
            case INVENTOR:
                inventorsPerType.putIfAbsent(character.getInventorType(), 0);
                inventorsPerType.put(character.getInventorType(), inventorsPerType.get(character.getInventorType()) + 1);
                break;
        }
    }

    // The owner of the building card is assigned and the building is added to the player's list
    // of buildings in his tribe, and to the player's list of the buildings activated at that specific
    // game phase, in BuildingManager.
    //
    // Called in Player.
    public void addBuildingToTribe(BuildingCard building) {
        buildings.add(building);
        building.assignOwner(tribeOwner);
        Game.getInstance().getBuildingManager().addBuilding(building, tribeOwner);
        // Calls effectOnPurchase for the building. It only works with the buildings who override it
        building.effectOnPurchase();
    }


    public int calculateFinalPoints() {

        // Points from builders
        int populationPoints = 0;
        for (CharacterCard character : population.get(CharacterRole.BUILDER)) {
            populationPoints = populationPoints + character.getPrestigePoints().orElse(0);
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