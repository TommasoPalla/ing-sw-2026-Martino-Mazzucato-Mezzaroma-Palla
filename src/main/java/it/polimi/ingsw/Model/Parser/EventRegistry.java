package it.polimi.ingsw.Model.Parser;

import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Model.EventManagement.CavePaintingsEvent;
import it.polimi.ingsw.Model.EventManagement.HuntEvent;
import it.polimi.ingsw.Model.EventManagement.ShamanicRitualEvent;
import it.polimi.ingsw.Model.EventManagement.SustenanceEvent;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;

public class EventRegistry {
    private static final EnumMap<EventType, Class<? extends EventCard>> registry = new EnumMap<>(EventType.class);

    public EventRegistry(){
        /*registry.put(EventType.CAVE_PAINTINGS, CavePaintingsEvent.class);
        registry.put(EventType.HUNT, HuntEvent.class);
        registry.put(EventType.SHAMANIC_RITUAL, ShamanicRitualEvent.class);
        registry.put(EventType.SUSTENANCE, SustenanceEvent.class);*/
    }
    public void addEventType(EventType type, Class<? extends EventCard> eventClass) {
        registry.put(type, eventClass);
    }

    public EventCard createEvent(EventCardDTO eventData) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<? extends EventCard> eventType = registry.get(eventData.type);
        if(eventType == null){
            throw new IllegalArgumentException("Event not identified");
        }
        Constructor<? extends EventCard> constructor = eventType.getConstructor(EventCardDTO.class);
        return constructor.newInstance(eventData);
    }
}