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

public class HuntEvent extends EventCard implements EventStrategy{
    private final int prestigeBonus;
    private final int foodBonus;

    public HuntEvent(int era, String cardID, int prestigeBonus, int foodBonus){
        super(era, cardID, EventType.HUNT);
        this.foodBonus = foodBonus;
        this.prestigeBonus = prestigeBonus;
    }
    public HuntEvent(EventCardDTO eventData){
        super(eventData.era, eventData.cardID, EventType.HUNT);
        this.foodBonus = eventData.foodBonus;
        this.prestigeBonus = eventData.prestigeBonus;
    }

    //getters
    @Override
    public int getFoodBonus(){
        return this.foodBonus;
    }
    public int getPrestigeBonus(){
        return this.prestigeBonus;
    }

    @Override
    public Map<String, String> getDisplayStats() {
        Map<String, String> stats = super.getDisplayStats();
        stats.put(TuiIcons.HUNTER, "-> +" +this.prestigeBonus + TuiIcons.PRESTIGE_BONUS + " +" + this.foodBonus + TuiIcons.FOOD_BONUS);
        return stats;
    }

    @Override
    public void apply(EventCard eventCard, ArrayList<Player> players, BuildingManager buildingManager) {
        for(Player player : players){
            int huntersNumber = player.getTribe().getHuntersNumber();
            int initialPrestigeBonus = huntersNumber * eventCard.getPrestigeBonus();
            int initialFoodBonus = huntersNumber * eventCard.getFoodBonus();

            EffectContext context = new EffectContext(player);
            context.putParam(Parameters.PRESTIGE_BONUS, initialPrestigeBonus);
            context.putParam(Parameters.FOOD_BONUS, initialFoodBonus);
            //chiedo al building manager di gestire eventuali edifici modificando il context
            buildingManager.useBuilding(GamePhase.ON_EVENT, context, HuntEvent.class);

            int finalPrestigeBonus = context.getParam(Parameters.PRESTIGE_BONUS);
            int finalFoodBonus = context.getParam(Parameters.FOOD_BONUS);
            player.getTribe().modifyPrestigePoints(finalPrestigeBonus);
            player.getTribe().modifyFood(finalFoodBonus);

        }
    }
}
