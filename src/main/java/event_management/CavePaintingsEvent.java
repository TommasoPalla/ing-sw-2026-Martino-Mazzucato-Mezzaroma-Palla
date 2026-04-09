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
                    if(player.getTribe().getArtistsNumber() < 1) player.getTribe().modifyPrestigePoints(-2);
                    else player.getTribe().modifyPrestigePoints( player.getTribe().getArtistsNumber() );
                }
                break;
            case SECOND:
                for(Player player : players){
                    if(player.getTribe().getArtistsNumber() <= 1) player.getTribe().modifyPrestigePoints(-2);
                    else player.getTribe().modifyPrestigePoints( 2*player.getTribe().getArtistsNumber() );
                }
                break;
            case THIRD:
                for(Player player : players){
                    if(player.getTribe().getArtistsNumber() <= 2) player.getTribe().modifyPrestigePoints(-2);
                    else player.getTribe().modifyPrestigePoints( 3*player.getTribe().getArtistsNumber() );
                }
                break;
        }
    }
}
