package event_management;

import users.Player;

import java.util.ArrayList;

public class SustenanceEvent implements EventStrategy{
    @Override
    public void apply(int era, ArrayList<Player> players) {
        for(Player player : players){
            int necessaryFood = player.getTribe().getPopulation().size();
            int gatherersDiscount = player.getTribe().getGatherersDiscount();
        }

    }
}
