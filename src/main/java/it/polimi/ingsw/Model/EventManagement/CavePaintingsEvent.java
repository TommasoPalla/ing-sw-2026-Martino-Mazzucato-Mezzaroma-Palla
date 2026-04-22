package it.polimi.ingsw.Model.EventManagement;

import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.BuildingsManagement.EffectContext;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Enums.Parameters;
import it.polimi.ingsw.Model.Users.Player;
import java.util.ArrayList;

public class CavePaintingsEvent implements EventStrategy{
    @Override
    public void apply(EventCard eventCard, ArrayList<Player> players, BuildingManager buildingManager) {
        for(Player player : players){

            if(player.getTribe().getArtistsNumber() < eventCard.getParam(Parameters.ARTIST_NUM)) {
                player.getTribe().modifyPrestigePoints(-eventCard.getParam(Parameters.PRESTIGE_MALUS));
            }
            else
                player.getTribe().modifyPrestigePoints(eventCard.getParam(Parameters.PRESTIGE_BONUS) * player.getTribe().getArtistsNumber());

            EffectContext context = new EffectContext(player);
            //senza la riga sotto chi non ha l'edificio quando si fa getParam(FOOD_BONUS) avra' NullPointerException
            context.putParam(Parameters.FOOD_BONUS, 0);
            buildingManager.useBuilding(GamePhase.ON_EVENT, context, CavePaintingsEvent.class);
            int buildingsFoodBonus = context.getParam(Parameters.FOOD_BONUS);
            context.getPlayer().getTribe().modifyFood(buildingsFoodBonus);
        }
    }
}
