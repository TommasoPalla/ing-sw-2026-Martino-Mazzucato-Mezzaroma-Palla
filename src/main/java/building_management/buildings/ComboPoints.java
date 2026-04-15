package building_management.buildings;

import cards_and_deck.BuildingCard;
import enums.CharacterRole;
import enums.Effect;
import enums.GamePhase;

import java.util.HashMap;
import java.util.Map;

// At the end of the game, the owner gains 6 Prestige Points for each set of 6
// different Character cards in their tribe
public class ComboPoints extends BuildingCard {
    private final int prestigeBonus;

    public ComboPoints(int era, String cardID, int cost, GamePhase activatedAt, Effect effect,
                       String effectDescription, int prestige, int prestigeBonus) {
        super(era, cardID, cost, activatedAt, effect, effectDescription, prestige);
        this.prestigeBonus = prestigeBonus;
    }

    @Override
    public int getPrestigeBonus(){
        return this.prestigeBonus;
    }
    @Override
    public void applyEffect() {
        // Maps every character's type to the number of its occurrences in the player's tribe
        Map<CharacterRole, Integer> occurrencesPerRole = new HashMap<>();
        for (CharacterRole role : this.getOwner().getTribe().getPopulation().keySet()) {
            occurrencesPerRole.put(role, this.getOwner().getTribe().getPopulation().get(role).size());
        }
        // if there's at least one occurrence of every character's role, it extracts the
        // value of the role with fewer occurrences (the number of sets) and multiplies it by 6
        if(occurrencesPerRole.size() == 6) {
           Integer setsNumber = occurrencesPerRole.values()
                    .stream()
                    .min(Integer::compare)
                    .orElse(0);
           this.getOwner().getTribe().modifyPrestigePoints((int) (this.prestigeBonus * setsNumber));
        }
    }
}
