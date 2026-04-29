package it.polimi.ingsw.Model.EventManagement;

import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.BuildingsManagement.EffectContext;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Enums.Parameters;
import it.polimi.ingsw.Model.Parser.EventCardDTO;
import it.polimi.ingsw.Model.Users.Player;

import java.util.ArrayList;

public class CavePaintingsEvent extends EventCard implements EventStrategy{
    private final int artistThreshold;  //under(<) this threshold prestigeMalus is applied
    private final int prestigeMalus;
    private final int prestigeBonus;

    public CavePaintingsEvent(int era, String cardID, int artistThreshold,
                              int prestigeBonus, int prestigeMalus){
        super(era, cardID, EventType.CAVE_PAINTINGS);
        this.artistThreshold = artistThreshold;
        this.prestigeMalus = prestigeMalus;
        this.prestigeBonus = prestigeBonus;
    }

    public CavePaintingsEvent(EventCardDTO eventData){
        super(eventData.era, eventData.cardID, EventType.CAVE_PAINTINGS);
        this.artistThreshold = eventData.artistThreshold;
        this.prestigeMalus = eventData.prestigeMalus;
        this.prestigeBonus = eventData.prestigeBonus;
    }

    //getters
    @Override
    public int getArtistThreshold(){
        return this.artistThreshold;
    }
    @Override
    public int getPrestigeMalus(){
        return this.prestigeMalus;
    }
    @Override
    public int getPrestigeBonus(){
        return this.prestigeBonus;
    }

    @Override
    public void apply(EventCard eventCard, ArrayList<Player> players, BuildingManager buildingManager) {
        for(Player player : players){

            if(player.getTribe().getArtistsNumber() < eventCard.getArtistThreshold()) {
                player.getTribe().modifyPrestigePoints(-eventCard.getPrestigeMalus());
            }
            else
                player.getTribe().modifyPrestigePoints(eventCard.getPrestigeBonus() * player.getTribe().getArtistsNumber());

            EffectContext context = new EffectContext(player);
            //senza la riga sotto chi non ha l'edificio quando si fa getParam(FOOD_BONUS) avra' NullPointerException
            context.putParam(Parameters.FOOD_BONUS, 0);
            buildingManager.useBuilding(GamePhase.ON_EVENT, context, CavePaintingsEvent.class);
            int buildingsFoodBonus = context.getParam(Parameters.FOOD_BONUS);
            context.getPlayer().getTribe().modifyFood(buildingsFoodBonus);
        }
    }
}
