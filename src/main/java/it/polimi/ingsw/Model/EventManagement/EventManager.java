package it.polimi.ingsw.Model.EventManagement;

import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Model.Users.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventManager {
    private Map<EventType, EventStrategy> strategies = new HashMap<>();

    public EventManager(){
        strategies.put(EventType.CAVE_PAINTINGS, new CavePaintingsEvent(0, "CAVE_PAINTINGS_DUMMY", 0, 0, 0));
        strategies.put(EventType.HUNT, new HuntEvent(0, "HUNT_DUMMY", 0, 0));
        strategies.put(EventType.SHAMANIC_RITUAL, new ShamanicRitualEvent(0, "SHAMANIC_RITUAL_DUMMY", 0, 0));
        strategies.put(EventType.SUSTENANCE, new SustenanceEvent(0, "SUSTENANCE_DUMMY", 0, 0));
    }

    public void resolve(ArrayList<EventCard> incomingEvents, ArrayList<Player> players, BuildingManager buildingManager){

        ArrayList<EventCard> sustenanceEvents = new ArrayList<>();
        for(EventCard event : incomingEvents){
            if (event.getEventType() == EventType.SUSTENANCE){
                sustenanceEvents.add(event);
            }
        }

        incomingEvents.removeAll(sustenanceEvents);
        incomingEvents.addAll(sustenanceEvents);

        for( EventCard event : incomingEvents ){
            EventStrategy eventStrategy = strategies.get(event.getEventType());
            if (eventStrategy != null ) eventStrategy.apply(event, players, buildingManager);
        }
    }
}