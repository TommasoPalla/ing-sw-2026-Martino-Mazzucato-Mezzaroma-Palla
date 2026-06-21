package it.polimi.ingsw.Model.BuildingsManagement.Buildings;

import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Parser.BuildingCardDTO;
import it.polimi.ingsw.Model.Users.TribeInterface;

/**
 * Starting from when the player purchases this building, every time they obtain a pair of identical inventors (with
 * the same invention icon), they take 3 food tokens. they do not take Food for pairs already owned at the time of
 * acquiring the building.
 */
public class InventorsFood extends BuildingCard {
    private int inventorsNumber;
    private final int foodBonus;

    public InventorsFood(int era, String cardID, int cost, GamePhase activatedAt, Effect effect,
                         String effectDescription, int prestige, int foodBonus) {
        super(era, cardID, cost, activatedAt, effect, effectDescription, prestige);
        this.inventorsNumber = 0;
        this.foodBonus = foodBonus;
    }

    public InventorsFood(BuildingCardDTO buildingData){
        super(buildingData.era, buildingData.cardID, buildingData.cost,
                buildingData.activatedAt, buildingData.effect, buildingData.effectDescription,
                buildingData.prestige);
        this.foodBonus = buildingData.foodBonus;
        this.inventorsNumber = 0;
    }

    @Override
    public int getFoodBonus(){
        return this.foodBonus;
    }

    /**
     * When purchased, it saves the number of inventors already possessed by the player. If the last card
     * drawn was an inventor, the variable is increased.
     * @param tribe the {@link TribeInterface} containing the tribe methods.
     */
    @Override
    public void effectOnPurchase(TribeInterface tribe) {
        this.inventorsNumber = tribe.getPopulation().get(CharacterRole.INVENTOR).size();
    }

    @Override
    public void applyEffect() {
        // ATT!! Si basa sul presupposto che la carta pescata si trovi all'ultimo posto nell'array population della tribe
        // if the drawn card is an inventor...
        if(this.getOwner().getTribe().getPopulation().get(CharacterRole.INVENTOR).size() > this.inventorsNumber) {
            this.inventorsNumber++;
            CharacterCard inventor = this.getOwner().getTribe().getPopulation().get(CharacterRole.INVENTOR).getLast();
            if(this.getOwner().getTribe().getInventorsPerType().get(inventor.getInventorType()) % 2 == 0) {
                this.getOwner().getTribe().modifyFood(this.foodBonus);
            }
        }
    }
}
