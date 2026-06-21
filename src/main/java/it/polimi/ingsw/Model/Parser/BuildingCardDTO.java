package it.polimi.ingsw.Model.Parser;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;

/**BuildingCardDTO class is used to temporarily store data parsed from JSON file,
 * in order to then store buildingCard instances, each of its subclass.
 */
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