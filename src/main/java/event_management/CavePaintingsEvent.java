package event_management;

import users.*;
import java.util.ArrayList;

public class CavePaintingsEvent implements EventStrategy{
    @Override
    public void apply(int era, ArrayList<Player> players) {
        for(Player player : players){
            if(player.getTribe().getArtistsNumber() < era) player.getTribe().modifyPrestigePoints(-2);
            else player.getTribe().modifyPrestigePoints( era*player.getTribe().getArtistsNumber() );
        }
    }
}
