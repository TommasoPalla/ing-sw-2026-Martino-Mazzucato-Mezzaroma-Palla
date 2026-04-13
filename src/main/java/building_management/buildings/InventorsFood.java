package building_management.buildings;

import cards_and_deck.BuildingCard;
import cards_and_deck.CharacterCard;
import enums.CharacterRole;
import enums.GamePhase;

public class InventorsFood extends BuildingCard {
    int inventorsNumber;
    public InventorsFood(int era, String cardID, int cost, GamePhase activatedAt, String effectDescription, int prestige) {
        super(era, cardID, cost, activatedAt, effectDescription, prestige);
    }

    @Override
    public void effectOnPurchase() {
        inventorsNumber = owner.getTribe().getPopulation().get(CharacterRole.INVENTOR).size();
    }

    @Override
    public void applyEffect() {
        // ATT!! Si basa sul presupposto che la carta pescata si trovi all'ultimo posto nell'array population della tribe
        // if the drew card is an inventor...
        if(owner.getTribe().getPopulation().get(CharacterRole.INVENTOR).size() > inventorsNumber) {
            inventorsNumber++;
            CharacterCard inventor = owner.getTribe().getPopulation().get(CharacterRole.INVENTOR).getLast();
            if(owner.getTribe().getInventorsPerType().get(inventor.getInventorType()) % 2 == 0) {
                owner.getTribe().modifyFood(3);
            }
        }
    }
}
