package event_management;

import building_management.BuildingManager;
import building_management.EffectContext;
import cards_and_deck.EventCard;
import enums.ContextParameters;
import enums.EventParam;
import enums.GamePhase;
import users.Player;
import java.util.ArrayList;

public class CavePaintingsEvent implements EventStrategy{
    @Override
    public void apply(EventCard eventCard, ArrayList<Player> players, BuildingManager buildingManager) {
        for(Player player : players){

            if(player.getTribe().getArtistsNumber() < eventCard.getEra())
                player.getTribe().modifyPrestigePoints( -eventCard.getParam(EventParam.PRESTIGE_MALUS) );
            else
                player.getTribe().modifyPrestigePoints( eventCard.getEra() * player.getTribe().getArtistsNumber() );

            EffectContext context = new EffectContext(player);
            //senza la riga sotto chi non ha l'edificio quando si fa getParam(FOOD_BONUS) avra' NullPointerException
            context.putParam(ContextParameters.FOOD_BONUS, 0);
            buildingManager.useBuilding(GamePhase.ON_EVENT, context, CavePaintingsEvent.class);
            int buildingsFoodBonus = context.getParam(ContextParameters.FOOD_BONUS);
        }
    }
}
