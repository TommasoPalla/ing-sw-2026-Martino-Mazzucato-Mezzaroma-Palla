package it.polimi.ingsw.Model.EventManagement;

import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Model.Users.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class manages the resolving of game events. It makes use of a Strategy design pattern: it holds a reference to
 * every type of event, each one overriding the method 'apply()', leaving to the context to delegate executing the right
 * method to the linked strategy event. So when a new event occurs, its {@link EventType} works with the strategy to
 * know what event apply method has to be called.
 */
public class EventManager {

    /**
     * It maps from the {@link EventType} to the {@link EventStrategy} containing the apply method, which is overridden
     * in all the different types of events classes.
     */
    private Map<EventType, EventStrategy> strategies = new HashMap<>();

    public EventManager(){
        strategies.put(EventType.CAVE_PAINTINGS, new CavePaintingsEvent(0, "CAVE_PAINTINGS_DUMMY", 0, 0, 0));
        strategies.put(EventType.HUNT, new HuntEvent(0, "HUNT_DUMMY", 0, 0));
        strategies.put(EventType.SHAMANIC_RITUAL, new ShamanicRitualEvent(0, "SHAMANIC_RITUAL_DUMMY", 0, 0));
        strategies.put(EventType.SUSTENANCE, new SustenanceEvent(0, "SUSTENANCE_DUMMY", 0, 0));
    }

    /**
     * It resolves the events of the Event cards contained in the array.
     * @param incomingEvents the list of incoming Event cards
     * @param players the list of players to apply the changes to their Prestige Points and Food reserves.
     * @param buildingManager a reference to the {@link BuildingManager} so the building with activation time during
     *                        a specific event can be activated.
     * @return a map containing the {@link PlayerEventResults} for every event resolved.
     */
    public Map<EventType, ArrayList<PlayerEventResults>> resolve(ArrayList<EventCard> incomingEvents, ArrayList<Player> players, BuildingManager buildingManager){

        if (incomingEvents.isEmpty()) {
            return Collections.emptyMap();
        }

        ArrayList<EventCard> sustenanceEvents = new ArrayList<>();
        for(EventCard event : incomingEvents){
            if (event.getEventType() == EventType.SUSTENANCE){
                sustenanceEvents.add(event);
            }
        }

        incomingEvents.removeAll(sustenanceEvents);
        incomingEvents.addAll(sustenanceEvents);

        Map<EventType, ArrayList<PlayerEventResults>> eventsResult = new HashMap<>();

        for( EventCard event : incomingEvents ){
            EventStrategy eventStrategy = strategies.get(event.getEventType());
            if (eventStrategy != null ) {
                // Capture state BEFORE event
                Map<String, int[]> stateBefore = new HashMap<>();
                for (Player player : players) {
                    stateBefore.put(player.getName(), new int[]{player.getTribe().getFoodReserve(), player.getTribe().getPrestigePoints()});
                }

                eventStrategy.apply(event, players, buildingManager);

                // Capture DELTA after event
                ArrayList<PlayerEventResults> playersResults = new ArrayList<>();
                for (Player player : players) {
                    int[] before = stateBefore.get(player.getName());
                    int foodDelta = player.getTribe().getFoodReserve() - before[0];
                    int ppDelta = player.getTribe().getPrestigePoints() - before[1];
                    
                    PlayerEventResults playerResults = new PlayerEventResults(player.getName(), new int[]{foodDelta, ppDelta});
                    playersResults.add(playerResults);
                }
                // NOTE: Still using map, so multiple events of same type will overwrite, but at least deltas are correct for the LAST event.
                // Fixing the Map overwrite would require changing many interfaces, let's focus on deltas for now as it's the main confusion.
                eventsResult.put(event.getEventType(), playersResults);
            }
        }
        return eventsResult;
    }
}