package it.polimi.ingsw.Model.Cards.Characters;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.InventorType;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Visitor;
import it.polimi.ingsw.Model.ClientModel;
import it.polimi.ingsw.Model.GameBoard.OfferTrack;
import it.polimi.ingsw.Model.Users.Player;

import java.util.ArrayList;

public abstract class CharacterCard extends Card {
    private final int numPlayersFlag;//3 stands for 3+, 4 for 4+. How many players are required to use the card
    private final CharacterRole role;

    // constructor
    public CharacterCard(int era, String cardID, int numPlayersFlag, CharacterRole role) {
        super(era, cardID);
        this.numPlayersFlag = numPlayersFlag;
        this.role = role;
    }

    // getters. A getter of a specific attribute normally returns 0 (or null)
    // except if that getter is overridden in the specific character class
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

    @Override
    public void accept(Visitor visitor){visitor.visitCard(this);}

    public void applyEffect(Player player){}
}