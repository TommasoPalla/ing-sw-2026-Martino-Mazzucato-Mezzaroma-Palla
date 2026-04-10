package event_management;

import java.util.ArrayList;

import cards_and_deck.EventCard;
import users.Player;

public interface EventStrategy {
   void apply(EventCard eventCard, ArrayList<Player> players);
}
