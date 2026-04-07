package event_management;

import cards_and_deck.EventCard;
import enums.Era;
import enums.EventType;
import users.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class EventManager {
    private Map<EventType, EventStrategy> strategies = new HashMap<>();

    public void resolve(ArrayList<EventCard> incomingEvents, ArrayList<Player> players){
        for( EventCard event : incomingEvents ){
            EventStrategy eventStrategy = event.getType(); // da correggere
            Era era = event.getEra();
            eventStrategy.apply(era, players);
        }
    }
}