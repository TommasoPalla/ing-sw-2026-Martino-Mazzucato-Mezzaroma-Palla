package building_management.buildings;

import cards_and_deck.BuildingCard;
import enums.CharacterRole;
import enums.GamePhase;

import java.util.HashMap;
import java.util.Map;

public class ComboFood extends BuildingCard {
    long currentSetsNumber = 0;

    public ComboFood(int era, String cardID, int cost, GamePhase activatedAt, String effectDescription, int prestige) {
        super(era, cardID, cost, activatedAt, effectDescription, prestige);
    }

    // When the player purchases the building, initialises the variable currentSetsNumber
    // with the number of sets already completed in their tribe
    @Override
    public void effectOnPurchase() {
        Map<CharacterRole, Integer> occurrencesPerRole = new HashMap<>();
        for (CharacterRole role : owner.getTribe().getPopulation().keySet()) {
            occurrencesPerRole.put(role, owner.getTribe().getPopulation().get(role).size());
        }
        if(occurrencesPerRole.size() == 6) {
            this.currentSetsNumber = occurrencesPerRole.values()
                    .stream()
                    .min(Integer::compare)
                    .orElse(0);
        }
        else { this.currentSetsNumber = 0; }
    }

    @Override
    public void applyEffect() {
        // Maps every character's type to the number of its occurrences in the player's tribe
        Map<CharacterRole, Integer> occurrencesPerRole = new HashMap<>();
        for (CharacterRole role : owner.getTribe().getPopulation().keySet()) {
            occurrencesPerRole.put(role, owner.getTribe().getPopulation().get(role).size());
        }
        // if there's at least one occurrence of every character's role, it extracts the
        // value of the role with fewer occurrences (the number of sets),
        // and if the number of sets has increased, the player takes 5 Food tokens
        if(occurrencesPerRole.size() == 6) {
            long setsNumber = occurrencesPerRole.values()
                    .stream()
                    .min(Integer::compare)
                    .orElse(0);
            if(setsNumber > this.currentSetsNumber) {
                owner.getTribe().modifyFood(5);
                currentSetsNumber++;
            }
        }
    }
}

