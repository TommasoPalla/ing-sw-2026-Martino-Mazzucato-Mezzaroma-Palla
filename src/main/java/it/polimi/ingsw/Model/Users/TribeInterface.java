package it.polimi.ingsw.Model.Users;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.InventorType;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;

import java.util.ArrayList;
import java.util.Map;

public interface TribeInterface {
    void modifyFood(int food);
    void modifyPrestigePoints(int pp);
    void addShamansStars(int stars);
    void addBuildersDiscount(int discount);
    void addGathererDiscount(int discount);
    void addInventor(InventorType type);

    int getFoodReserve();
    int getPrestigePoints();
    int getPopulationSize();
    int getHuntersNumber();
    int getArtistsNumber();
    int getBuildersDiscount();
    int getGatherersDiscount();
    int getShamansStars();
    Map<InventorType, Integer> getInventorsPerType();
    Map<CharacterRole, ArrayList<CharacterCard>> getPopulation();
    ArrayList<BuildingCard> getBuildings();
}
