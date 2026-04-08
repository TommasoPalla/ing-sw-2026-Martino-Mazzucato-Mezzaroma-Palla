package users;

import cards_and_deck.BuildingCard;
import cards_and_deck.CharacterCard;

import java.util.ArrayList;

public class Tribe {
    private int prestigePoints;
    private int foodReserve;
    private ArrayList<CharacterCard> population;
    private ArrayList<BuildingCard> buildings;

    private int huntersNumber;
    private int builderDiscount;
    private int gatherersDiscount;
    private int artistsNumber;
    private int shamanStars;
    private int inventorsNumberPerType;

    //getters
    public int getPrestigePoints() {return prestigePoints;}
    public int getFoodReserve() {return foodReserve;}
    public ArrayList<CharacterCard> getPopulation() {return population;}
    public ArrayList<BuildingCard> getBuildings() {return buildings;}
    public int getHuntersNumber() {return huntersNumber;}
    public int getBuilderDiscount() {return builderDiscount;}
    public int getGatherersDiscount() {return gatherersDiscount;}
    public int getArtistsNumber() {return artistsNumber;}
    public int getShamanStars() {return shamanStars;}
    public int getInventorsNumberPerType() {return inventorsNumberPerType;}
}
