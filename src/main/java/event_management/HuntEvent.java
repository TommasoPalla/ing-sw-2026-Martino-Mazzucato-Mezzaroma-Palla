package event_management;

import building_management.BuildingManager;
import cards_and_deck.BuildingCard;
import cards_and_deck.EventCard;
import enums.EventParam;
import users.Player;

import java.util.ArrayList;

public class HuntEvent implements EventStrategy{
    @Override
    public void apply(EventCard eventCard, ArrayList<Player> players, BuildingManager buildingManager) {
        for(Player player : players){
            int huntersNumber = player.getTribe().getHuntersNumber();
            player.getTribe().modifyFood(huntersNumber * eventCard.getParam(EventParam.FOOD_BONUS) );
            player.getTribe().modifyPrestigePoints(huntersNumber * eventCard.getParam(EventParam.PRESTIGE_BONUS) );
        }
    }
}
