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

    public void resolve(ArrayList<EventCard> incomingEvents, ArrayList<Player> players, BuildingManager buildingManager){

        ArrayList<EventCard> sustenance = new ArrayList<>();
        for(EventCard event : incomingEvents){
            if (event.getEventType() == EventType.SUSTENANCE){
                sustenance.add(event);
                incomingEvents.remove(event);
            }
        }
        incomingEvents.addAll(sustenance);

        for( EventCard event : incomingEvents ){
            EventStrategy eventStrategy = strategies.get(event.getEventType());
            if (eventStrategy != null ) eventStrategy.apply(event, players, buildingManager);
        }
    }
}