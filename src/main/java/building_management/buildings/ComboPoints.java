package building_management.buildings;

import cards_and_deck.BuildingCard;
import cards_and_deck.CharacterCard;
import enums.CharacterRole;
import enums.GamePhase;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

// At the end of the game, the owner gains 6 Prestige Points for each set of 6
// different Character cards in their tribe
public class ComboPoints extends BuildingCard {
    public ComboPoints(int era, String name, String cardID, int cost, GamePhase activatedAt, String effectDescription, int prestige) {
        super(era, name, cardID, cost, activatedAt, effectDescription, prestige);
    }

    @Override
    public void applyEffect() {
        // Maps the tribe's population to the characters' types, then it maps every role to
        // its number of occurrences
        Map<CharacterRole, Long> occurrencesPerRole = owner.getTribe().getPopulation().stream()
                .map(CharacterCard::getRole)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        // if there's at least one occurrence of every character's role, it extracts the
        // value of the role with fewer occurrences (the number of sets) and multiplies it by 6
        if(occurrencesPerRole.size() == 6) {
           Long setsNumber = occurrencesPerRole.values()
                    .stream()
                    .min(Long::compare)
                    .orElse(0L);
           owner.getTribe().modifyPrestigePoints((int) (6 * setsNumber));
        }
    }
}
