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

        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapterFactory(cardAdapter);

        for (Class<? extends BuildingCard> buildingType : buildingTypes) {
            cardAdapter.registerSubtype(buildingType, buildingType.getSimpleName().toUpperCase());
            builder.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of((Class<BuildingCard>) buildingType, "cardType").registerSubtype(buildingType, buildingType.getSimpleName().toUpperCase()));
        }

        for (Class<? extends CharacterCard> characterType : characterTypes){
            cardAdapter.registerSubtype(characterType, characterType.getSimpleName().toUpperCase());
        builder.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of((Class<CharacterCard>) characterType, "cardType").registerSubtype(characterType, characterType.getSimpleName().toUpperCase()));
        }

        for(Class<? extends EventCard> eventType : eventTypes){
            cardAdapter.registerSubtype(eventType, eventType.getSimpleName().toUpperCase());
            builder.registerTypeAdapterFactory(RuntimeTypeAdapterFactory.of((Class<EventCard>)eventType, "cardType").registerSubtype(eventType, eventType.getSimpleName().toUpperCase()));
        }

        return builder.create();
    }
}
