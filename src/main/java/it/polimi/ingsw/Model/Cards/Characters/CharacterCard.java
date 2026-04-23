package it.polimi.ingsw.Model.Cards.Characters;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.InventorType;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Visitor;
import it.polimi.ingsw.Model.Users.Player;

import java.util.ArrayList;

public abstract class CharacterCard extends Card {
    private final int numPlayersFlag;//3 stands for 3+, 4 for 4+. How many players are required to use the card
    private CharacterRole role;
//    private final CharacterRole role;
//    private final Integer prestigePoints;
//    private final InventorType inventorType;
//    private final Integer shamanStars;
//    private final Integer buildingDiscount;     //valued as a positive integer
//    private final Boolean hunterIcon;

    /* static factory pattern: different static method that call the constructor
        managing where to pass null as parameters */
//    public static CharacterCard createInventor(int era, String cardID, int numPlayersFlag,
//                                               CharacterRole role, InventorType T){
//        return new CharacterCard(era, cardID, numPlayersFlag, role,
//                null, T, null, null, null);
//    }
//    public static CharacterCard createBuilders(int era, String cardID, int numPlayersFlag,
//                                               CharacterRole role, Integer prestigePoints, Integer buildingDiscount){
//        return new CharacterCard(era, cardID, numPlayersFlag, role,
//                prestigePoints, InventorType.NONE, null, buildingDiscount, null);
//    }
//    public static CharacterCard createShaman(int era, String cardID, int numPlayersFlag,
//                                             CharacterRole role, Integer shamanStars){
//        return new CharacterCard(era, cardID, numPlayersFlag, role,
//                null, InventorType.NONE, shamanStars, null, null);
//    }
//    public static CharacterCard createHunter(int era, String cardID, int numPlayersFlag,
//                                             CharacterRole role, Boolean hunterIcon){
//        return new CharacterCard(era, cardID, numPlayersFlag, role,
//                null, InventorType.NONE, null, null, hunterIcon);
//    }
//    public static CharacterCard createArtistOrGatherer(int era, String cardID, int numPlayersFlag,
//                                                       CharacterRole role){
//        return new CharacterCard(era, cardID, numPlayersFlag, role,
//                null, InventorType.NONE, null, null, null);
//    }

    // constructor
    public CharacterCard(int era, String cardID, int numPlayersFlag, CharacterRole role) {
        super(era, cardID);
        this.numPlayersFlag = numPlayersFlag;
        this.role = role;
//        this.prestigePoints = prestigePoints;
//        this.inventorType = inventorType;
//        this.shamanStars = shamanStars;
//        this.buildingDiscount = buildingDiscount;
//        this.hunterIcon = hunterIcon;
    }

    // getters. A getter of a specific attribute normally returns 0 (or null) except if that getter is overridden in the specific character class
    public int getNumPlayersFlag() { return this.numPlayersFlag;}
    public CharacterRole getRole(){
        return role;
    }
    public int getPrestigePoints(){
        return 0;
    }
    public InventorType getInventorType(){ return null; }
    public int getShamansStars(){ return 0; }
    public int getBuildingDiscount(){ return 0; }
    public boolean isAlphaHunter() { return false; }
//    public Optional<Integer> getShamanStars(){
//        return Optional.ofNullable(this.shamanStars);
//    }
//    public Optional<Integer> getBuildingDiscount(){
//        return Optional.ofNullable(this.buildingDiscount);
//    }
//    public Optional<Boolean> isAlphaHunter(){
//        return Optional.ofNullable(this.hunterIcon);
//    }

    @Override
    public void accept(Visitor visitor, Player player){
        visitor.visitCard(this, player);
    }
    @Override
    public void accept(Visitor visitor, ArrayList<Card> array){
        visitor.visitCard(this, array);
    }

    public void applyEffect(Player player){}
}
