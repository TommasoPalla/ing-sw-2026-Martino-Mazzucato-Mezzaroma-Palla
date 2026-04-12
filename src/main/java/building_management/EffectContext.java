package building_management;

import enums.Parameters;
import users.Player;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public class EffectContext {
    private Player player;
    private EnumMap<Parameters, Integer> parameters = new EnumMap<>(Parameters.class);

    public EffectContext(Player player){
        this.player = player;
    }

    public Player getPlayer() {return player;}
    public void putParam(Parameters parameterType, Integer value){parameters.put(parameterType, value);}
    public Integer getParam(Parameters parameterType){return (int) parameters.get(parameterType);}
}
