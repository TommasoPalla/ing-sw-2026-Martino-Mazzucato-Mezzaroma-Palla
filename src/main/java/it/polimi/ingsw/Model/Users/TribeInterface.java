package it.polimi.ingsw.Model.Users;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.InventorType;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;

import java.util.ArrayList;
import java.util.Map;

/**
 * This interface contains methods that are used both in the model tribe and in the clients' light tribes, but with
 * different implementations to match their functionalities and requirements.
 */
public interface TribeInterface {
    /**
     * It adds the indicated amount of food tokens to the player's tribe. Can be negative.
     * @param food the amount of food tokens.
     */
    void modifyFood(int food);

    /**
     * It adds the indicated amount of prestige points to the player's tribe. Can be negative.
     * @param pp the amount of prestige points.
     */
    void modifyPrestigePoints(int pp);

    /**
     * It adds the amount of shamans stars shown on the drawn shaman card to the player's tribe.
     * @param stars the amount of stars.
     */
    void addShamansStars(int stars);

    /**
     * It adds the amount of builder's discount shown on the drawn builder card to the player's tribe.
     * @param discount the amount of builder's discount.
     */
    void addBuildersDiscount(int discount);

    /**
     * It adds the amount of gatherer's discount shown on the drawn gatherer card to the player's tribe.
     * @param discount the amount of gatherer's discount (always 3).
     */
    void addGathererDiscount(int discount);

    /**
     * It adds the inventor to the map of inventors, increasing the number of that invention's type.
     * @param type the {@link InventorType} shown on the inventor card.
     */
    void addInventor(InventorType type);

    /*
    * Getters
     */
    int getFoodReserve();
    int getPrestigePoints();
    int getPopulationSize();
    int getHuntersNumber();
    int getArtistsNumber();
    int getBuildersNumber();
    int getBuildersDiscount();
    int getGatherersNumber();
    int getGatherersDiscount();
    int getShamansNumber();
    int getShamansStars();
    Map<InventorType, Integer> getInventorsPerType();
    Map<CharacterRole, ArrayList<CharacterCard>> getPopulation();
    ArrayList<BuildingCard> getBuildings();
}
