package event_management;

import enums.Era;
import users.Player;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.OptionalInt;
import java.util.stream.Collectors;

public class ShamanicRitualEvent implements EventStrategy{
    @Override
    public void apply(Era era, ArrayList<Player> players){
        /* finds the player(s) with the highest number of shamanic stars and puts them in a winners' list.
        * Same procedure is applied to the players with the lowest value.
         */
        OptionalInt maxShamanStars = players.stream()
                .mapToInt(Player::getTribe().getShamansStars)
                .max();

        if (maxShamanStars.isPresent()){
            ArrayList<Player> eventWinners = players.stream()
                    .filter(p -> p.getTribe().getShamansStars() == maxShamanStars.getAsInt())
                    .toList();
        }
        OptionalInt minShamanStars = players.stream()
                .mapToInt(Player::getTribe().getShamansStars)
                .min();

        if (maxShamanStars.isPresent()){
            ArrayList<Player> eventLosers = players.stream()
                    .filter(p -> p.getTribe().getShamansStars() == minShamanStars.getAsInt())
                    .toList();
        }
        /* Based on the era of the specific event, it gives prestige points to the event winners and detracts points
        * from the losers
         */
        switch(era){
            case FIRST:
                for(Player player : eventWinners){
                    player.getTribe().addPrestigePoints(5);
                }
                for(Player player : eventLosers){
                    player.getTribe().addPrestigePoints(-3);
                }
                break;
            case SECOND:
                for(Player player : eventWinners){
                    player.getTribe().addPrestigePoints(10);
                }
                for(Player player : eventLosers){
                    player.getTribe().addPrestigePoints(-5);
                }
                break;
            case THIRD:
                for(Player player : eventWinners){
                    player.getTribe().addPrestigePoints(15);
                }
                for(Player player : eventLosers){
                    player.getTribe().addPrestigePoints(-7);
                }
                break;
        }
    }
}
