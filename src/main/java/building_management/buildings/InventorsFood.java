package building_management.buildings;

import cards_and_deck.BuildingCard;
import cards_and_deck.CharacterCard;
import enums.CharacterRole;
import enums.Effect;
import enums.GamePhase;

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
    public void effectOnPurchase() {
        this.inventorsNumber = owner.getTribe().getPopulation().get(CharacterRole.INVENTOR).size();
    }

    @Override
    public void applyEffect() {
        // ATT!! Si basa sul presupposto che la carta pescata si trovi all'ultimo posto nell'array population della tribe
        // if the drew card is an inventor...
        if(owner.getTribe().getPopulation().get(CharacterRole.INVENTOR).size() > this.inventorsNumber) {
            this.inventorsNumber++;
            CharacterCard inventor = owner.getTribe().getPopulation().get(CharacterRole.INVENTOR).getLast();
            if(owner.getTribe().getInventorsPerType().get(inventor.getInventorType()) % 2 == 0) {
                owner.getTribe().modifyFood(3);
            }
        }
    }
}
