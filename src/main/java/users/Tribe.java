package users;

import cards_and_deck.BuildingCard;
import cards_and_deck.CharacterCard;
import enums.GamePhase;
import enums.InventorType;
import game.Game;

import java.util.ArrayList;

public class  Tribe {
    private final Player tribeOwner;
    private int prestigePoints;
    private int foodReserve;
    private ArrayList<CharacterCard> population;
    private ArrayList<BuildingCard> buildings;
    private int[] inventorsPerType;

    private int huntersNumber;
    private int builderDiscount;
    private int gatherersDiscount;
    private int artistsNumber;
    private int shamansStars;

    //Tribe's constructor
    public Tribe(Player tribeOwner) {
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

    public ArrayList<CharacterCard> getPopulation() {
        return population;
    }

    public ArrayList<BuildingCard> getBuildings() {
        return buildings;
    }

    public int getHuntersNumber() {
        return huntersNumber;
    }

    public int getBuilderDiscount() {
        return builderDiscount;
    }

    public int getGatherersDiscount() {
        return gatherersDiscount;
    }

    public int getArtistsNumber() {
        return artistsNumber;
    }

    public int getShamansStars() {
        return shamansStars;
    }

    public int[] getInventorsPerType() {
        return inventorsPerType;
    }

    //actual methods
    public void modifyPrestigePoints(int pp) {
        prestigePoints += pp;
    }

    public void modifyFood(int food) {
        foodReserve += food;
    }

    public void addShamansStars(int stars) {
        shamansStars += stars;
    }

    // Character is added to the player's list
    // Called in Player (?)
    public void addCharacterToTribe(CharacterCard character) {
        population.add(character);
        //da finire (?)
    }

    // The owner of the building card is assigned and the building is added to the player's list
    // of buildings in his tribe, and to the player's list of the buildings activated at that specific
    // game phase, in BuildingManager
    //
    // Called in Player (?)
    public void addBuildingToTribe(BuildingCard building) {
        buildings.add(building);
        building.assignOwner(tribeOwner);
        Game.getInstance().getBuildingManager().addBuilding(building, tribeOwner);
    }


    public int calculateFinalPoints() {


        int populationPoints = 0;
        for (CharacterCard character : population) {
            populationPoints = populationPoints + character.getPrestigePoints().orElse(0);
        }

        int buildingPoints = 0;
        for (BuildingCard building : buildings) {
            buildingPoints += building.getPrestige();
        }

        int artistsPoints = (artistsNumber / 2) * 10;


        int inventorsPoints = 0;
        int numInventors = 0;
        for (int i = 0; i < InventorType.values().length; i++) {
            if (inventorsPerType[i] != 0) {
                numInventors++;
            }
            inventorsPoints += inventorsPerType[i];
        }
        inventorsPoints = inventorsPoints * numInventors;



        return (prestigePoints + artistsPoints + populationPoints + buildingPoints + inventorsPoints);

    }
}