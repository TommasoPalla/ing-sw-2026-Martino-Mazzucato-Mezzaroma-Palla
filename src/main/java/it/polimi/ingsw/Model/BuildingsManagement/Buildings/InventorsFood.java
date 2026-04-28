package it.polimi.ingsw.Model.BuildingsManagement.Buildings;

import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;

public class InventorsFood extends BuildingCard {
    private int inventorsNumber;
    private final int foodBonus;

    public InventorsFood(int era, String cardID, int cost, GamePhase activatedAt, Effect effect,
                         String effectDescription, int prestige, int foodBonus) {
        super(era, cardID, cost, activatedAt, effect, effectDescription, prestige);
        this.inventorsNumber = 0;
        this.foodBonus = foodBonus;
    }

    @Override
    public int getFoodBonus(){
        return this.foodBonus;
    }

    @Override
    public void effectOnPurchase() {
        this.inventorsNumber = this.getOwner().getTribe().getPopulation().get(CharacterRole.INVENTOR).size();
    }

    @Override
    public void applyEffect() {
        // ATT!! Si basa sul presupposto che la carta pescata si trovi all'ultimo posto nell'array population della tribe
        // if the drew card is an inventor...
        if(this.getOwner().getTribe().getPopulation().get(CharacterRole.INVENTOR).size() > this.inventorsNumber) {
            this.inventorsNumber++;
            CharacterCard inventor = this.getOwner().getTribe().getPopulation().get(CharacterRole.INVENTOR).getLast();
            if(this.getOwner().getTribe().getInventorsPerType().get(inventor.getInventorType()) % 2 == 0) {
                this.getOwner().getTribe().modifyFood(this.foodBonus);
            }
        }
    }
}
