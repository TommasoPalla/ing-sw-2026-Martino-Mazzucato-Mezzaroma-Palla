package event_management;

import enums.Era;
import users.*;
import java.util.ArrayList;

public class CavePaintingsEvent implements EventStrategy{
    @Override
    public void apply(Era era, ArrayList<Player> players) {
        switch(era){
            case FIRST:
                for(Player player : players){
                    if(player.getTribe().getArtists() < 1) player.getTribe().addPrestigePoints(-2);
                    else player.getTryibe().addPrestigePoints( player.getTribe().getArtists() );
                }
                break;
            case SECOND:
                for(Player player : players){
                    if(player.getTribe().getArtists() <= 1) player.getTribe().addPrestigePoints(-2);
                    else player.getTryibe().addPrestigePoints( 2*player.getTribe().getArtists() );
                }
                break;
            case THIRD:
                for(Player player : players){
                    if(player.getTribe().getArtists() <= 2) player.getTribe().addPrestigePoints(-2);
                    else player.getTribe().addPrestigePoints( 3*player.getTribe().getArtists() );
                }
                break;
        }
    }
}
