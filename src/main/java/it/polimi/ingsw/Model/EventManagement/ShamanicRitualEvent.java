package it.polimi.ingsw.Model.EventManagement;

import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.BuildingsManagement.EffectContext;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Enums.Parameters;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Users.Player;

import java.util.ArrayList;
import java.util.OptionalInt;
import java.util.stream.Collectors;

public class ShamanicRitualEvent implements EventStrategy{
    @Override
    public void apply(EventCard eventCard, ArrayList<Player> players, BuildingManager buildingManager){
        OptionalInt maxShamanStars = players.stream()
                .mapToInt(p -> p.getTribe().getShamansStars()).max();

        OptionalInt minShamanStars = players.stream()
                .mapToInt(p -> p.getTribe().getShamansStars()).min();

        if(maxShamanStars.isEmpty() || minShamanStars.isEmpty()) return;

        ArrayList<Player> eventWinners = players.stream()
                .filter(p -> p.getTribe().getShamansStars() == maxShamanStars.getAsInt())
                .collect(Collectors.toCollection(ArrayList::new));

        ArrayList<Player> eventLosers = players.stream()
            .filter(p -> p.getTribe().getShamansStars() == minShamanStars.getAsInt())
            .collect(Collectors.toCollection(ArrayList::new));

        for(Player player : eventWinners){
            //inizializzo il context: player corrente con bonus di punti che dipende dalla carta evento (Era)
            EffectContext context = new EffectContext(player);
            context.putParam(Parameters.PRESTIGE_BONUS, eventCard.getParam(Parameters.PRESTIGE_BONUS));

            //qui chiedo al building manager di fare le sue cose (nello specifico di raddoppiare i punti per chi vince)
            buildingManager.useBuilding(GamePhase.ON_EVENT, context, ShamanicRitualEvent.class);

            //prendo i punti bonus dal context che e' stato modificato dal building manager e li do al player
            int finalBonusPoints = context.getParam(Parameters.PRESTIGE_BONUS);
            player.getTribe().modifyPrestigePoints(finalBonusPoints);
        }
        //stessa identica cosa per i loser
        for(Player player : eventLosers){
            EffectContext context = new EffectContext(player);
            context.putParam(Parameters.PRESTIGE_MALUS, eventCard.getParam(Parameters.PRESTIGE_MALUS));

            buildingManager.useBuilding(GamePhase.ON_EVENT, context, ShamanicRitualEvent.class);

            int finalMalusPoints = context.getParam(Parameters.PRESTIGE_MALUS);
            player.getTribe().modifyPrestigePoints(-finalMalusPoints);
        }
    }
}
