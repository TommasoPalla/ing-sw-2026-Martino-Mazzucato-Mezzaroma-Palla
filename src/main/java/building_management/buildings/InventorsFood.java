package building_management.buildings;

import cards_and_deck.BuildingCard;
import cards_and_deck.CharacterCard;
import enums.CharacterRole;
import enums.GamePhase;

public class InventorsFood extends BuildingCard {

    public InventorsFood(int era, String cardID, int cost, GamePhase activatedAt, String effectDescription, int prestige) {
        super(era, cardID, cost, activatedAt, effectDescription, prestige);
    }

    @Override
    public void applyEffect() {
        // ATT!! Si basa sul presupposto che la carta pescata si trovi all'ultimo posto nell'array population della tribe
        CharacterCard inventor = owner.getTribe().getPopulation().getLast();
        // if the drew card is an inventor, adds it to the map
        if(inventor.getRole() == CharacterRole.INVENTOR) {
            // if the player already had another inventor with that invention, then remove the invention from the map
            // and the player takes 3 Food token
            if(owner.getTribe().getInventorsPerType().get(inventor.getInventorType()) == 2) {
                owner.getTribe().modifyFood(3);
            }
        }
    }
}
