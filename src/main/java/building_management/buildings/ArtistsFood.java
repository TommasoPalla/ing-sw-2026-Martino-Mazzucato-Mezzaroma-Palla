package building_management.buildings;

import cards_and_deck.BuildingCard;
import enums.GamePhase;
import event_management.EventStrategy;

public class ArtistsFood extends BuildingCard {
    private EventStrategy activationEvent;
    //da capire come funziona sto cazzo di costruttore
    public ArtistsFood(int era, String name, String cardID, int cost, GamePhase activatedAt, EventStrategy activationEvent, String effectDescription) {
        super(era, name, cardID, cost, activatedAt, effectDescription);
        this.activationEvent = activationEvent;
    }
    @Override
    public void applyEffect(){
        owner.getTribe().modifyFood( owner.getTribe().getArtistsNumber() );
    };
}
