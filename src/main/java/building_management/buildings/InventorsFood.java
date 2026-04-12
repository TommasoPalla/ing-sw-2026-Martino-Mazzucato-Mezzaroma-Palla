package building_management.buildings;

import cards_and_deck.BuildingCard;
import cards_and_deck.CharacterCard;
import enums.CharacterRole;
import enums.GamePhase;
import enums.InventorType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

// non si può usare InventorsPerType di tribe? invece di fare una mappa
public class InventorsFood extends BuildingCard {
    Map<InventorType, Integer> playersInventors = new HashMap<>();
    public InventorsFood(int era, String name, String cardID, int cost, GamePhase activatedAt, String effectDescription, int prestige) {
        super(era, name, cardID, cost, activatedAt, effectDescription, prestige);
    }

    @Override
    public void effectOnPurchase() {
        ArrayList<CharacterCard> population = owner.getTribe().getPopulation();
        for(CharacterCard characterCard : population){
            if(characterCard.getRole() == CharacterRole.INVENTOR) {
                playersInventors.putIfAbsent(characterCard.getInventorType(), 0);
                playersInventors.put(characterCard.getInventorType(), playersInventors.get(characterCard.getInventorType()) + 1);
                // if the player already has that couple in his tribe, removes it from the map
                if(playersInventors.get(characterCard.getInventorType()) == 2) {
                    playersInventors.remove(characterCard.getInventorType());
                }
            }
        }
    }

    @Override
    public void applyEffect() {
        // ATT!! Si basa sul presupposto che la carta pescata si trovi all'ultimo posto nell'array population della tribe
        CharacterCard inventor = owner.getTribe().getPopulation().getLast();
        // if the drew card is an inventor, adds it to the map
        if(inventor.getRole() == CharacterRole.INVENTOR) {
            playersInventors.putIfAbsent(inventor.getInventorType(), 0);
            playersInventors.put(inventor.getInventorType(), playersInventors.get(inventor.getInventorType()) + 1);
            // if the player already had another inventor with that invention, then remove the invention from the map
            // and the player takes 3 Food token
            if(playersInventors.get(inventor.getInventorType()) == 2) {
                playersInventors.remove(inventor.getInventorType());
                owner.getTribe().modifyFood(3);
            }
        }
    }
}
