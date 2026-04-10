package event_management;

import cards_and_deck.EventCard;
import enums.EventParam;
import users.Player;
import java.util.ArrayList;

public class CavePaintingsEvent implements EventStrategy{
    @Override
    public void apply(EventCard eventCard, ArrayList<Player> players) {
        for(Player player : players){
            if(player.getTribe().getArtistsNumber() < eventCard.getEra())
                player.getTribe().modifyPrestigePoints( -eventCard.getParam(EventParam.PRESTIGE_MALUS) );
            else
                player.getTribe().modifyPrestigePoints( eventCard.getEra() * player.getTribe().getArtistsNumber() );
        }
    }
}
