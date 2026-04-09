package event_management;

import java.util.ArrayList;

import users.Player;

public interface EventStrategy {
   void apply(int era, ArrayList<Player> players);
}
