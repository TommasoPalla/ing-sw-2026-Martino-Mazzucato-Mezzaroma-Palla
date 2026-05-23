package it.polimi.ingsw.Model.Users;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.InventorType;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;

import java.util.ArrayList;
import java.util.Map;

public interface TribeInterface {
    void addFood(int food);
    void addPrestigePoints(int pp);
    void addShamanStars(int stars);
    void addBuilderDiscount(int discount);
    void addGathererDiscount(int discount);
    void addInventor(InventorType type);
    int getHuntersNumber();
    int getArtistsNumber();
    Map<CharacterRole, ArrayList<CharacterCard>> getPopulation();
    Map<InventorType, Integer> getInventorsPerType();
}
