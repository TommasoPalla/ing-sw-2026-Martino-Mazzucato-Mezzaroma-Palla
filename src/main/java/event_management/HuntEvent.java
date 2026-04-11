package event_management;

import building_management.BuildingManager;
import building_management.EffectContext;
import cards_and_deck.BuildingCard;
import cards_and_deck.EventCard;
import enums.ContextParameters;
import enums.EventParam;
import enums.GamePhase;
import users.Player;

import java.util.ArrayList;

public class HuntEvent implements EventStrategy{
    @Override
    public void apply(EventCard eventCard, ArrayList<Player> players, BuildingManager buildingManager) {
        for(Player player : players){
            //inizializzo i bonus di cibo e di punti solo considerando i cacciatori nella tribe
            int huntersNumber = player.getTribe().getHuntersNumber();
            int initialPrestigeBonus = huntersNumber * eventCard.getParam(EventParam.PRESTIGE_BONUS);
            int initialFoodBonus = huntersNumber * eventCard.getParam(EventParam.FOOD_BONUS);

            //metto questi valori nel context
            EffectContext context = new EffectContext(player);
            context.putParam(ContextParameters.PRESTIGE_BONUS, initialPrestigeBonus);
            context.putParam(ContextParameters.FOOD_BONUS, initialFoodBonus);
            //chiedo al buildin manager di gestire eventuali edifici modificando il context
            buildingManager.useBuilding(GamePhase.ON_EVENT, context, HuntEvent.class);

            //prendo il context eventualmente modificato e applico i cambiamenti
            int finalPrestigeBonus = context.getParam(ContextParameters.PRESTIGE_BONUS);
            int finalFoodBonus = context.getParam(ContextParameters.FOOD_BONUS);
            player.getTribe().modifyPrestigePoints(finalPrestigeBonus);
            player.getTribe().modifyFood(finalFoodBonus);

        }
    }
}
