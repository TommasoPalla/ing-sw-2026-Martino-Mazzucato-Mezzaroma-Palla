package event_management;

import cards_and_deck.EventCard;
import enums.EventType;
import users.Player;

import javax.swing.border.EmptyBorder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventManager {
    private Map<EventType, EventStrategy> strategies = new HashMap<>();

    public void resolve(ArrayList<EventCard> incomingEvents, ArrayList<Player> players){
        ArrayList<EventCard> sustenance = new ArrayList<>();
        for(EventCard event : incomingEvents){
            if (event.getType() == EventType.SUSTENANCE){
                sustenance.add(event);
                incomingEvents.remove(event);
            }
        }
        incomingEvents.addAll(sustenance);
        for( EventCard event : incomingEvents ){
            EventStrategy eventStrategy = strategies.get(event.getType());
            int era = event.getEra();
            eventStrategy.apply(era, players);
        }
    }
}