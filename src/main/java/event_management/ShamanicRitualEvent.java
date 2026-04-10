package event_management;

import cards_and_deck.EventCard;
import enums.EventParam;
import users.Player;

import java.util.ArrayList;
import java.util.OptionalInt;
import java.util.stream.Collectors;

public class ShamanicRitualEvent implements EventStrategy{
    @Override
    public void apply(EventCard eventCard, ArrayList<Player> players){
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

        for(Player player : eventWinners){
            player.getTribe().modifyPrestigePoints( eventCard.getParam(EventParam.PRESTIGE_BONUS) );
            //Game.getInstance().getBuildingManager().useBuilding(GamePhase.ON_EVENT, player);
            player.setRitualWinnerBonus( eventCard.getParam(EventParam.PRESTIGE_BONUS) );
        }
        for(Player player : eventLosers){
            player.getTribe().modifyPrestigePoints( -eventCard.getParam(EventParam.PRESTIGE_MALUS) );
        }
    }
}
