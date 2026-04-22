package it.polimi.ingsw.Model.EventManagement;

import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.BuildingsManagement.EffectContext;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Enums.Parameters;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Users.Player;

import java.util.ArrayList;

public class HuntEvent implements EventStrategy{
    @Override
    public void apply(EventCard eventCard, ArrayList<Player> players, BuildingManager buildingManager) {
        for(Player player : players){
            //inizializzo i bonus di cibo e di punti solo considerando i cacciatori nella tribe
            int huntersNumber = player.getTribe().getHuntersNumber();
            int initialPrestigeBonus = huntersNumber * eventCard.getParam(Parameters.PRESTIGE_BONUS);
            int initialFoodBonus = huntersNumber * eventCard.getParam(Parameters.FOOD_BONUS);

            //metto questi valori nel context
            EffectContext context = new EffectContext(player);
            context.putParam(Parameters.PRESTIGE_BONUS, initialPrestigeBonus);
            context.putParam(Parameters.FOOD_BONUS, initialFoodBonus);
            //chiedo al buildin manager di gestire eventuali edifici modificando il context
            buildingManager.useBuilding(GamePhase.ON_EVENT, context, HuntEvent.class);

            //prendo il context eventualmente modificato e applico i cambiamenti
            int finalPrestigeBonus = context.getParam(Parameters.PRESTIGE_BONUS);
            int finalFoodBonus = context.getParam(Parameters.FOOD_BONUS);
            player.getTribe().modifyPrestigePoints(finalPrestigeBonus);
            player.getTribe().modifyFood(finalFoodBonus);

        }
    }
}
