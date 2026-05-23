package it.polimi.ingsw.Model.EventManagement;

import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.BuildingsManagement.EffectContext;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Enums.Parameters;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Parser.EventCardDTO;
import it.polimi.ingsw.Model.Users.Player;
import it.polimi.ingsw.View.TUIView.TuiIcons;

import java.util.ArrayList;
import java.util.Map;

public class SustenanceEvent extends EventCard implements EventStrategy{
    //private int buildingDiscount = 0;      maybe use this to include the buildings into the computation??
    private final int prestigeMalus;
    private final int foodMalus;

    public SustenanceEvent(int era, String cardID, int foodMalus, int prestigeMalus){
        super(era, cardID, EventType.SUSTENANCE);
        this.prestigeMalus = prestigeMalus;
        this.foodMalus = foodMalus;
    }
    public SustenanceEvent(EventCardDTO eventData){
        super(eventData.era, eventData.cardID, EventType.SUSTENANCE);
        this.prestigeMalus = eventData.prestigeMalus;
        this.foodMalus = eventData.foodMalus;
    }

    //getters
    @Override
    public int getPrestigeMalus(){
        return this.prestigeMalus;
    }
    @Override
    public int getFoodMalus(){
        return this.foodMalus;
    }

    @Override
    public Map<String, String> getDisplayStats() {
        Map<String, String> stats = super.getDisplayStats();
        //stats.put(TuiIcons.FOOD_MALUS, "-" + this.foodMalus + "*" + TuiIcons.POPULATION);
        stats.put(TuiIcons.POPULATION, "-> -" + this.foodMalus + TuiIcons.FOOD_MALUS);
        return stats;
    }

    @Override
    public void apply(EventCard eventCard, ArrayList<Player> players, BuildingManager buildingManager) {
        for(Player player : players){
            int necessaryFood = player.getTribe().getPopulationSize() * eventCard.getFoodMalus();
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
                player.getTribe().modifyPrestigePoints((foodReserve - finalFoodToPay) * eventCard.getPrestigeMalus());
                // (foodReserve - finalFoodToPay) is already negative => prestige points reduced
            }
            else player.getTribe().modifyFood(-finalFoodToPay);
        }

    }
}
