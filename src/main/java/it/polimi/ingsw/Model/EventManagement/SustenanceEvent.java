package it.polimi.ingsw.Model.EventManagement;

import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.BuildingsManagement.EffectContext;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Enums.Parameters;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Users.Player;

import java.util.ArrayList;

public class SustenanceEvent implements EventStrategy{
    //private int buildingDiscount = 0;      maybe use this to include the buildings into the computation??
    @Override
    public void apply(EventCard eventCard, ArrayList<Player> players, BuildingManager buildingManager) {
        for(Player player : players){
            int necessaryFood = player.getTribe().getPopulation().size() * eventCard.getParam(Parameters.FOOD_MALUS);
            int gatherersDiscount = player.getTribe().getGatherersDiscount();
            int initialFoodToPay = necessaryFood - gatherersDiscount;

            EffectContext context = new EffectContext(player);
            context.putParam(Parameters.FOOD_MALUS, initialFoodToPay);
            buildingManager.useBuilding(GamePhase.ON_EVENT, context, SustenanceEvent.class);

            int finalFoodToPay = context.getParam(Parameters.FOOD_MALUS);
            if(finalFoodToPay < 0) finalFoodToPay = 0;

            int foodReserve = player.getTribe().getFoodReserve();
            if(finalFoodToPay > foodReserve){
                player.getTribe().modifyFood(-foodReserve);
                player.getTribe().modifyPrestigePoints((foodReserve - finalFoodToPay) * eventCard.getParam(Parameters.PRESTIGE_MALUS));
                // (foodReserve - finalFoodToPay) is already negative => prestige points reduced
            }
            else player.getTribe().modifyFood(-finalFoodToPay);
        }

    }
}
