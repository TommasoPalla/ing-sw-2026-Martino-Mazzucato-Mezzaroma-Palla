package it.polimi.ingsw.Model.Parser;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.InventorType;

public class CharacterCardDTO {
    public int era;
    public String cardID;
    public int numPlayersFlag;
    public CharacterRole role;
    public int prestigePoints;
    public int buildingDiscount;
    public int gathererDiscount;
    public boolean hunterIcon;
    public InventorType inventorType;
    public int shamanStars;
}