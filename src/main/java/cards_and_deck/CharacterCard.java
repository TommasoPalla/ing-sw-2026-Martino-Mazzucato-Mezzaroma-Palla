package cards_and_deck;

import java.util.Optional;
import enums.*;

public class CharacterCard extends Card {
    private final CharacterRole role;
    private final Integer prestigePoints;
    private final InventorType inventorType;
    private final Integer shamanStars;
    private final Integer buildingDiscount;
    private final Boolean hunterIcon;

    /* static factory pattern: different static method that call the constructor
        managing where to pass null as parameters */
    public static CharacterCard createInventor(int era, String name, String cardID, CharacterRole role,
                                               InventorType T){
        return new CharacterCard(era, name, cardID, role,
                null, T, null, null, null);
    }
    public static CharacterCard createBuilders(int era, String name, String cardID, CharacterRole role,
                                               Integer prestigePoints, Integer buildingDiscount){
        return new CharacterCard(era, name, cardID, role,
                prestigePoints, InventorType.NONE, null, buildingDiscount, null);
    }
    public static CharacterCard createShaman(int era, String name, String cardID, CharacterRole role,
                                             Integer shamanStars){
        return new CharacterCard(era, name, cardID, role,
                null, InventorType.NONE, shamanStars, null, null);
    }
    public static CharacterCard createHunter(int era, String name, String cardID, CharacterRole role,
                                             Boolean hunterIcon){
        return new CharacterCard(era, name, cardID, role,
                null, InventorType.NONE, null, null, hunterIcon);
    }
    public static CharacterCard createArtistOrGatherer(int era, String name, String cardID, CharacterRole role){
        return new CharacterCard(era, name, cardID, role,
                null, InventorType.NONE, null, null, null);
    }

    // constructor
    public CharacterCard(int era, String name, String cardID, CharacterRole role,
                         Integer prestigePoints, InventorType inventorType, Integer shamanStars,
                         Integer buildingDiscount, Boolean hunterIcon){
        super(era, name, cardID);
        this.role = role;
        this.prestigePoints = prestigePoints;
        this.inventorType = inventorType;
        this.shamanStars = shamanStars;
        this.buildingDiscount = buildingDiscount;
        this.hunterIcon = hunterIcon;
    }

    // getters
    public CharacterRole getRole(){
        return this.role;
    }
    public Optional<Integer> getPrestigePoints(){
        return Optional.ofNullable(this.prestigePoints);
    }
    public InventorType getInventorType(){
        return this.inventorType;
    }
    public Optional<Integer> getShamanStars(){
        return Optional.ofNullable(this.shamanStars);
    }
    public Optional<Integer> getBuildingDiscount(){
        return Optional.ofNullable(this.buildingDiscount);
    }
    public Optional<Boolean> isAlphaHunter(){
        return Optional.ofNullable(this.hunterIcon);
    }
}
