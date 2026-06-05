package it.polimi.ingsw.Model.EventManagement;

import java.util.ArrayList;

import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Model.Users.Player;

/**
 * This interface contains only one method: it is overridden by every Event Type class. It applies the Strategy pattern
 * design to the Event management, so the context decides every time which apply method has to be delegated to execute.
 */
public interface EventStrategy {
   void apply(EventCard eventCard, ArrayList<Player> players, BuildingManager buildingManager);
}
