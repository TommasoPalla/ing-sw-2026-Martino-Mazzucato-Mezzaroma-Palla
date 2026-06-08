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
import java.util.OptionalInt;
import java.util.stream.Collectors;

/**
 * This class manages the calculation of players' prestige points and food during the Shamanic Ritual event. It overrides
 * the {@link EventStrategy} apply method.
 */
public class ShamanicRitualEvent extends EventCard implements EventStrategy{
    private final int prestigeBonus;
    private final int prestigeMalus;

    public ShamanicRitualEvent(int era, String cardID, int prestigeBonus, int prestigeMalus){
        super(era, cardID, EventType.SHAMANIC_RITUAL);
        this.prestigeMalus = prestigeMalus;
        this.prestigeBonus = prestigeBonus;
    }
    public ShamanicRitualEvent(EventCardDTO eventData){
        super(eventData.era, eventData.cardID, EventType.SHAMANIC_RITUAL);
        this.prestigeMalus = eventData.prestigeMalus;
        this.prestigeBonus = eventData.prestigeBonus;
    }

    @Override
    public int getPrestigeBonus(){
        return this.prestigeBonus;
    }
    @Override
    public int getPrestigeMalus(){
        return this.prestigeMalus;
    }

    @Override
    public Map<String, String> getDisplayStats() {
        Map<String, String> stats = super.getDisplayStats();
        stats.put(TuiIcons.WIN_SHAMAN_EVENT, " -> +" + this.prestigeBonus + TuiIcons.PRESTIGE_BONUS);
        stats.put(TuiIcons.LOSE_SHAMAN_EVENT, " -> -" + this.prestigeMalus + TuiIcons.PRESTIGE_MALUS);
        return stats;
    }

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
            context.putParam(Parameters.PRESTIGE_BONUS, eventCard.getPrestigeBonus());
            context.putParam(Parameters.PRESTIGE_MALUS, 0);

            //qui chiedo al building manager di fare le sue cose (nello specifico di raddoppiare i punti per chi vince)
            System.out.println("before using building: " + player.getName() + " PP: " + context.getParam(Parameters.PRESTIGE_BONUS));
            buildingManager.useBuilding(GamePhase.ON_EVENT, context, ShamanicRitualEvent.class);
            System.out.println("after using building: " + player.getName() + " PP: " + context.getParam(Parameters.PRESTIGE_BONUS));

            //prendo i punti bonus dal context che e' stato modificato dal building manager e li do al player
            int finalBonusPoints = context.getParam(Parameters.PRESTIGE_BONUS);
            player.getTribe().modifyPrestigePoints(finalBonusPoints);
        }
        //stessa identica cosa per i loser
        for(Player player : eventLosers){
            EffectContext context = new EffectContext(player);
            context.putParam(Parameters.PRESTIGE_BONUS, 0);
            context.putParam(Parameters.PRESTIGE_MALUS, eventCard.getPrestigeMalus());

            System.out.println("before using building: " + player.getName() + " PP: " + context.getParam(Parameters.PRESTIGE_BONUS));
            buildingManager.useBuilding(GamePhase.ON_EVENT, context, ShamanicRitualEvent.class);
            System.out.println("after using building: " + player.getName() + " PP: " + context.getParam(Parameters.PRESTIGE_BONUS));

            int finalMalusPoints = context.getParam(Parameters.PRESTIGE_MALUS);
            player.getTribe().modifyPrestigePoints(-finalMalusPoints);
        }
    }
}
