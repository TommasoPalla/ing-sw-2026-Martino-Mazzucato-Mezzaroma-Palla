package event_management;

import java.util.ArrayList;
import enums.Era;
import users.Player;

public interface EventStrategy {
   void apply(Era era, ArrayList<Player> players);
}
