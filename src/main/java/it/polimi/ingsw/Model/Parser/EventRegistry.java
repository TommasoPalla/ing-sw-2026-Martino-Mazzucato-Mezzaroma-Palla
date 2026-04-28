package it.polimi.ingsw.Model.Parser;

import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Cards.EventCard;
import jdk.jfr.Event;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;

public class EventRegistry {
    private static final EnumMap<EventType, Class<? extends EventCard>> registry = new EnumMap<>(EventType.class);

    public EventCard createEvent(EventCardDTO eventData) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<? extends EventCard> eventType = registry.get(eventData.getType());
        if(eventType == null){
            throw new IllegalArgumentException("Event not identified");
        }
        Constructor<? extends EventCard> constructor = eventType.getConstructor(EventCardDTO.class);
        return constructor.newInstance(eventData);
    }
}
