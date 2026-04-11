package event_management;

import building_management.BuildingManager;
import building_management.EffectContext;
import cards_and_deck.EventCard;
import enums.EventParam;
import enums.GamePhase;
import users.Player;

import java.util.ArrayList;

public class SustenanceEvent implements EventStrategy{
    //private int buildingDiscount = 0;      maybe use this to include the buildings into the computation??
    @Override
    public void apply(EventCard eventCard, ArrayList<Player> players, BuildingManager buildingManager) {
        for(Player player : players){
            int necessaryFood = player.getTribe().getPopulation().size() * eventCard.getParam(EventParam.FOOD_MALUS);
            int gatherersDiscount = player.getTribe().getGatherersDiscount();
            int foodReserve = player.getTribe().getFoodReserve();

            EffectContext context = new EffectContext(player);
            buildingManager.useBuilding(GamePhase.ON_EVENT, context, ShamanicRitualEvent.class);

            int foodToPay = necessaryFood - gatherersDiscount;
            if(foodToPay > foodReserve){
                player.getTribe().modifyFood(-foodReserve);
                player.getTribe().modifyPrestigePoints((foodReserve - foodToPay) * eventCard.getParam(EventParam.PRESTIGE_MALUS));
                // (foodReserve - foodToPay) is already negative => prestige points reduced
            }
            else player.getTribe().modifyFood(-foodToPay);
        }

    }
}
