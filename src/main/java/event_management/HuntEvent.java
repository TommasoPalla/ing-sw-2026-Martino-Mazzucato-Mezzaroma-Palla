package event_management;

import users.Player;

import java.util.ArrayList;

public class HuntEvent implements EventStrategy{
    @Override
    public void apply(int era, ArrayList<Player> players) {
        for(Player player : players){
            int huntersNumber = player.getTribe().getHuntersNumber();
            player.getTribe().modifyFood(huntersNumber);
            player.getTribe().modifyPrestigePoints(era * huntersNumber);
        }
    }
}
