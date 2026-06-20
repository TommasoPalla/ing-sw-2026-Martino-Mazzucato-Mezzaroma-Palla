package it.polimi.ingsw.Model.BuildingsManagement;

import it.polimi.ingsw.Enums.Parameters;
import it.polimi.ingsw.Model.Users.Player;

import java.util.EnumMap;

/**
 * This class is used to pass temporary result
 * of event resolutions to the BuildingManager.
 * This allows buildings' effects to change the
 * events' results before giving bonuses/maluses
 * to the players
 */
public class EffectContext {
    private Player player;
    private EnumMap<Parameters, Integer> parameters = new EnumMap<>(Parameters.class);

    public EffectContext(Player player){
        for(Parameters parameter : Parameters.values()){
            parameters.put(parameter, 0);
        }
        this.player = player;
    }

    public Player getPlayer() {return player;}
    public void putParam(Parameters parameterType, Integer value){parameters.put(parameterType, value);}
    public Integer getParam(Parameters parameterType){return (int) parameters.get(parameterType);}
}
