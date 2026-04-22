package it.polimi.ingsw.Model.EventManagement;

import java.util.ArrayList;

import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Model.Users.Player;

public interface EventStrategy {
   void apply(EventCard eventCard, ArrayList<Player> players, BuildingManager buildingManager);
}
