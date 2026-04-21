package event_management;

import java.util.ArrayList;

import building_management.BuildingManager;
import cards_and_deck.EventCard;
import users.Player;

public interface EventStrategy {
   void apply(EventCard eventCard, ArrayList<Player> players, BuildingManager buildingManager);
}
