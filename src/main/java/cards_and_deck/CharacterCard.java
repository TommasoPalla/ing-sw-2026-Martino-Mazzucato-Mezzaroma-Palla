package cards_and_deck;

import java.util.Optional;
import enums.*;

public class CharacterCard extends Card {
    private final CharacterRole role;
    private final Integer prestigePoints;
    private final InventorType inventorType;
    private final Integer shamanStars;
    private final Integer buildingDiscount;
    private final Boolean hunterMeat;

    /* static factory pattern: different static method that call the constructor
        managing where to pass null as parameters */
    public static CharacterCard createInventor(Era era, String name, String cardID, CharacterRole role,
                                               InventorType T){
        return new CharacterCard(era, name, cardID, role,
                null, T, null, null, null);
    }
    public static CharacterCard createBuilders(Era era, String name, String cardID, CharacterRole role,
                                               Integer prestigePoints, Integer buildingDiscount){
        return new CharacterCard(era, name, cardID, role,
                prestigePoints, null, null, buildingDiscount, null);
    }
    // constructor
    public CharacterCard(Era era, String name, String cardID, CharacterRole role,
                         Integer prestigePoints, InventorType inventorType, Integer shamanStars,
                         Integer buildingDiscount, Boolean hunterMeat){
        super(era, name, cardID);
        this.role = role;
        if (Optional.ofNullable(prestigePoints)){
        this.prestigePoints = prestigePoints;
        this.buildingDiscount = discount;
    }
    // constructor called for inventors
    // constructor called for

    // getters
    public int

}
