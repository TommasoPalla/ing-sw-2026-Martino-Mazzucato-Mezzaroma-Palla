package it.polimi.ingsw.Model.Cards.Buildings;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;

public class BuildingCardDTO {
    public int era;
    public String cardID;
    public int cost;
    public GamePhase activatedAt;
    public Effect effect;
    public String effectDescription;
    public int prestige;
    public CharacterRole roleEffect;
    public int foodBonus;
    public int prestigeBonus;
    public int starBonus;
    public int multiplier;
    public int foodDiscount;
}
