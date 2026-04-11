package building_management.buildings;

import cards_and_deck.BuildingCard;
import enums.CharacterRole;
import enums.GamePhase;
import event_management.EventStrategy;

public class SustenanceDiscount extends BuildingCard {
    private final EventStrategy activationEvent;
    private final CharacterRole role;

    public SustenanceDiscount(int era, String name, String cardID, int cost, GamePhase activatedAt,
                               String effectDescription, int prestige,
                               EventStrategy activationEvent, CharacterRole role) {
        super(era, name, cardID, cost, activatedAt, effectDescription, prestige);
        this.activationEvent = activationEvent;
        this.role = role;
    }

    // Calculates the number of Character Cards of the specific role and adds it
    // to the Food discount
    @Override
    public void applyEffect() {
        int characterNumber = Math.toIntExact(owner.getTribe().getPopulation().stream()
                .filter(card -> card.getRole() == role)
                .count());
        // owner.getTribe().modifyFoodDiscount( charachterNumber) ??????? DA VEDERE COME FARE
    }
}
