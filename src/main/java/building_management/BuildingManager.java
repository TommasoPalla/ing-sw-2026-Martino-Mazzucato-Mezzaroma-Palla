package building_management;

import cards_and_deck.BuildingCard;
import enums.GamePhase;
import event_management.EventStrategy;
import users.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BuildingManager {
    Map<GamePhase, Map<Player, ArrayList<BuildingCard>>> buildingsMap = new HashMap<>();

    public BuildingManager() {
        for (GamePhase gamePhase : GamePhase.values()) {
            buildingsMap.put(gamePhase, new HashMap<>());
        }
    }

    public void addBuilding(BuildingCard buildingCard, Player player) {
        GamePhase gamePhase = buildingCard.getActivatedAt();
        buildingsMap.get(gamePhase).putIfAbsent(player, new ArrayList<>());
        buildingsMap.get(gamePhase).get(player).add(buildingCard);
    }

    //FORSE questi due metodi si possono unire se tutti quanti gli edifici usano context invece di player (che dovrebbe tra l'altro essere meglio)
    //da capire dove va chiamata questa roba e se serve un altro metodo in BuildingManager per chiamare tutti gli useBuilding() non ON_EVENT
    //a quel punto cambiare anche l'altra useBuilding in useEventBuilding() e non serve dirgli che siamo in ON_EVENT (duh)
    public void useBuilding(GamePhase gamePhase, Player player) {
        ArrayList<BuildingCard> playersCurrPhaseBuildings = buildingsMap.get(gamePhase).get(player);
        for (BuildingCard buildingCard : playersCurrPhaseBuildings) {
            buildingCard.applyEffect();
        }

    }

    public void useBuilding(GamePhase gamePhase, EffectContext context, Class<? extends EventStrategy> eventType) {
        Player player = context.getPlayer();
        ArrayList<BuildingCard> playersCurrPhaseBuildings = buildingsMap.get(gamePhase).get(player);
        for (BuildingCard buildingCard : playersCurrPhaseBuildings) {
            if (buildingCard.isUsedIn(eventType)) buildingCard.applyEffect(context);

        }
    }
}