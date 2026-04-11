package building_management;

import enums.ContextParameters;
import users.Player;

import java.util.HashMap;
import java.util.Map;

public class EffectContext {
    private Player player;
    private Map<ContextParameters, Integer> parameters = new HashMap<>();

    public EffectContext(Player player){
        this.player = player;
    }

    public Player getPlayer() {return player;}
    public void putParam(ContextParameters parameterType, Integer value){parameters.put(parameterType, value);}
    public Integer getParam(ContextParameters parameterType){return parameters.get(parameterType);}
}
