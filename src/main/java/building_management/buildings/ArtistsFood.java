package building_management.buildings;

import cards_and_deck.BuildingCard;
import enums.Era;
import enums.GamePhase;

import java.util.ArrayList;

public class ArtistsFood extends BuildingCard {
    //da capire come funziona sto cazzo di costruttore
    public ArtistsFood(Era era, String name, String cardID, int cost, GamePhase activatedAt, String effectDescription) {
        super(era, name, cardID, cost, activatedAt, effectDescription);
    }
    @Override
    public void applyEffect(){
        owner.getTribe().addFood( owner.getTribe().getArtists() );
    };
}
