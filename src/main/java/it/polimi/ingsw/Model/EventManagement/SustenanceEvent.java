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
            int populationSize = player.getTribe().getPopulationSize();
            int foodMalusPerPerson = eventCard.getFoodMalus();
            int totalNecessaryFood = populationSize * foodMalusPerPerson;
            
            int gatherersDiscount = player.getTribe().getGatherersDiscount();
            int foodToPay = Math.max(0, totalNecessaryFood - gatherersDiscount);

            EffectContext context = new EffectContext(player);
            context.putParam(Parameters.FOOD_MALUS, foodToPay);
            buildingManager.useBuilding(GamePhase.ON_EVENT, context, SustenanceEvent.class);

            int finalFoodToPay = Math.max(0, context.getParam(Parameters.FOOD_MALUS));
            int currentFood = player.getTribe().getFoodReserve();

            if (finalFoodToPay > currentFood) {
                int missingFood = finalFoodToPay - currentFood;
                player.getTribe().modifyFood(-currentFood); // pay all available food
                player.getTribe().modifyPrestigePoints(-(missingFood * eventCard.getPrestigeMalus()));
            } else {
                player.getTribe().modifyFood(-finalFoodToPay);
            }
        }
    }
}
