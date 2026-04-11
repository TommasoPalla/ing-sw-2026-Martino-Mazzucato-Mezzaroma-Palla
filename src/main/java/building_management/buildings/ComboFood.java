package building_management.buildings;

import cards_and_deck.BuildingCard;
import cards_and_deck.CharacterCard;
import enums.CharacterRole;
import enums.GamePhase;
import users.Tribe;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ComboFood extends BuildingCard {
    long currentSetsNumber = 0;

    public ComboFood(int era, String name, String cardID, int cost, GamePhase activatedAt, String effectDescription, int prestige) {
        super(era, name, cardID, cost, activatedAt, effectDescription, prestige);
    }

    // When the player purchases the building, initialises the variable currentSetsNumber
    // with the number of sets already completed in their tribe
    @Override
    public void effectOnPurchase() {
        Map<CharacterRole, Long> occurrencesPerRole = owner.getTribe().getPopulation().stream()
                .map(CharacterCard::getRole)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        if(occurrencesPerRole.size() == 6) {
            this.currentSetsNumber = occurrencesPerRole.values()
                    .stream()
                    .min(Long::compare)
                    .orElse(0L);
        }
        else { this.currentSetsNumber = 0; }
    }

    @Override
    public void applyEffect() {
        // Maps the tribe's population to the characters' types, then it maps every role to
        // its number of occurrences
        Map<CharacterRole, Long> occurrencesPerRole = owner.getTribe().getPopulation().stream()
                .map(CharacterCard::getRole)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        // if there's at least one occurrence of every character's role, it extracts the
        // value of the role with fewer occurrences (the number of sets),
        // and if the number of sets has increased, the player takes 5 Food tokens
        if(occurrencesPerRole.size() == 6) {
            long setsNumber = occurrencesPerRole.values()
                    .stream()
                    .min(Long::compare)
                    .orElse(0L);
            owner.getTribe().modifyFood(5);
            currentSetsNumber++;
        }
    }
}

