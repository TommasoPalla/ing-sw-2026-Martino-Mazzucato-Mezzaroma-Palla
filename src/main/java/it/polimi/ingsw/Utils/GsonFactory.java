package it.polimi.ingsw.Utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.*;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.*;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Model.EventManagement.CavePaintingsEvent;
import it.polimi.ingsw.Model.EventManagement.HuntEvent;
import it.polimi.ingsw.Model.EventManagement.ShamanicRitualEvent;
import it.polimi.ingsw.Model.EventManagement.SustenanceEvent;

import java.util.List;

/**
 * This class is used to reconstruct the correct subclass of {@link Card} and of {@link BuildingCard}
 * from JSON when passing them through the network
 */

public class GsonFactory {

    private static final List<Class<? extends BuildingCard>> buildingTypes = List.of(
            ArtistsFood.class,
            BonusPoints.class,
            BonusStars.class,
            ComboFood.class,
            ComboHunters.class,
            ComboPoints.class,
            DrawAdditionalCard.class,
            InventorsFood.class,
            MultiBonusRitual.class,
            MultiPointsBuilder.class,
            NoMalusRitual.class,
            PointsPerRole.class,
            SustenanceDiscount.class,
            TotemFood.class
    );

    private static final List<Class<? extends  CharacterCard>> characterTypes = List.of(
            Artist.class,
            Builder.class,
            Gatherer.class,
            Hunter.class,
            Inventor.class,
            Shaman.class
    );

    private static final List<Class<? extends  EventCard>> eventTypes = List.of(
            CavePaintingsEvent.class,
            HuntEvent.class,
            ShamanicRitualEvent.class,
            SustenanceEvent.class
    );

    public static Gson create() {
        RuntimeTypeAdapterFactory<Card> cardAdapter = RuntimeTypeAdapterFactory
                .of(Card.class, "cardType")
                .registerSubtype(EventCard.class, "eventCard")
                .registerSubtype(CharacterCard.class, "characterCard")
                .registerSubtype(BuildingCard.class, "buildingCard");

        RuntimeTypeAdapterFactory<BuildingCard> buildingAdapter = RuntimeTypeAdapterFactory.of(BuildingCard.class, "cardType");
        RuntimeTypeAdapterFactory<CharacterCard> characterAdapter = RuntimeTypeAdapterFactory.of(CharacterCard.class, "cardType");
        RuntimeTypeAdapterFactory<EventCard> eventAdapter = RuntimeTypeAdapterFactory.of(EventCard.class, "cardType");

        GsonBuilder builder = new GsonBuilder();

        for (Class<? extends BuildingCard> buildingType : buildingTypes) {
            String label = buildingType.getSimpleName().toUpperCase();
            cardAdapter.registerSubtype(buildingType, label);
            buildingAdapter.registerSubtype(buildingType, label);
        }

        for (Class<? extends CharacterCard> characterType : characterTypes){
            String label = characterType.getSimpleName().toUpperCase();
            cardAdapter.registerSubtype(characterType, label);
            characterAdapter.registerSubtype(characterType, label);
        }

        for(Class<? extends EventCard> eventType : eventTypes){
            String label = eventType.getSimpleName().toUpperCase();
            cardAdapter.registerSubtype(eventType, label);
            eventAdapter.registerSubtype(eventType, label);
        }

        builder.registerTypeAdapterFactory(cardAdapter);
        builder.registerTypeAdapterFactory(buildingAdapter);
        builder.registerTypeAdapterFactory(characterAdapter);
        builder.registerTypeAdapterFactory(eventAdapter);

        return builder.create();
    }
}
