package building_management.buildings;

import cards_and_deck.BuildingCard;
import enums.CharacterRole;
import enums.GamePhase;

import java.util.HashMap;
import java.util.Map;

// At the end of the game, the owner gains 6 Prestige Points for each set of 6
// different Character cards in their tribe
public class ComboPoints extends BuildingCard {
    public ComboPoints(int era, String cardID, int cost, GamePhase activatedAt, String effectDescription, int prestige) {
        super(era, cardID, cost, activatedAt, effectDescription, prestige);
    }

    @Override
    public void applyEffect() {
        // Maps every character's type to the number of its occurrences in the player's tribe
        Map<CharacterRole, Integer> occurrencesPerRole = new HashMap<>();
        for (CharacterRole role : owner.getTribe().getPopulation().keySet()) {
            occurrencesPerRole.put(role, owner.getTribe().getPopulation().get(role).size());
        }
        // if there's at least one occurrence of every character's role, it extracts the
        // value of the role with fewer occurrences (the number of sets) and multiplies it by 6
        if(occurrencesPerRole.size() == 6) {
           Integer setsNumber = occurrencesPerRole.values()
                    .stream()
                    .min(Integer::compare)
                    .orElse(0);
           owner.getTribe().modifyPrestigePoints((int) (6 * setsNumber));
        }
    }
}
