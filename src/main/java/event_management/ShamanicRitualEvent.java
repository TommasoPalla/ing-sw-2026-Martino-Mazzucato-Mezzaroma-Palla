package event_management;

import users.Player;

import java.util.ArrayList;
import java.util.OptionalInt;
import java.util.stream.Collectors;


public class ShamanicRitualEvent implements EventStrategy{
    @Override
    public void apply(int era, ArrayList<Player> players){
        /* finds the player(s) with the highest number of shamanic stars and puts them in a winners' list.
        * Same procedure is applied to the players with the lowest value.
         */
        OptionalInt maxShamanStars = players.stream()
                .mapToInt(p -> p.getTribe().getShamansStars()).max();

        OptionalInt minShamanStars = players.stream()
                .mapToInt(p -> p.getTribe().getShamansStars()).min();

        if(maxShamanStars.isEmpty() || minShamanStars.isEmpty()) return;

        ArrayList<Player> eventWinners = players.stream()
                .filter(p -> p.getTribe().getShamansStars() == maxShamanStars.getAsInt())
                .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<Player> eventLosers = players.stream()
            .filter(p -> p.getTribe().getShamansStars() == minShamanStars.getAsInt())
            .collect(Collectors.toCollection(ArrayList::new));
        /* Based on the era of the specific event, it gives prestige points to the event winners and detracts points
        * from the losers
         */
        for(Player player : eventWinners){
            player.getTribe().modifyPrestigePoints(5 * era);
        }
        for(Player player : eventLosers){
            player.getTribe().modifyPrestigePoints(-1 * (1 + 2 * era));
        }
    }
}
