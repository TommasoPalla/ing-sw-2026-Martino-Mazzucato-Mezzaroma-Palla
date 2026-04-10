package event_management;

import cards_and_deck.EventCard;
import enums.EventParam;
import users.Player;

import java.util.ArrayList;

public class SustenanceEvent implements EventStrategy{
    //private int buildingDiscount = 0;      maybe use this to include the buildings into the computation??
    @Override
    public void apply(EventCard eventCard, ArrayList<Player> players) {
        for(Player player : players){
            int necessaryFood = player.getTribe().getPopulation().size() * eventCard.getParam(EventParam.FOOD_MALUS);
            int gatherersDiscount = player.getTribe().getGatherersDiscount();
            int foodToPay = necessaryFood - gatherersDiscount;
            if(foodToPay < 0){
                player.getTribe().modifyFood(-player.getTribe().getFoodReserve());
                player.getTribe().modifyPrestigePoints(foodToPay * eventCard.getParam(EventParam.PRESTIGE_MALUS));
                // foodToPay is already negative. foodToPay * PRESTIGE_MALUS will be negative => PP reduced
            }
            else player.getTribe().modifyFood(-foodToPay);
        }

    }
}
