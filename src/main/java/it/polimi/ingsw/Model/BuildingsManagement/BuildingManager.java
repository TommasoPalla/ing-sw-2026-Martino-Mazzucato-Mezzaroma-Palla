package it.polimi.ingsw.Model.BuildingsManagement;

import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.EventManagement.EventStrategy;
import it.polimi.ingsw.Model.Users.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BuildingManager {
    Map<GamePhase, Map<String, ArrayList<BuildingCard>>> buildingsMap = new HashMap<>();
    ArrayList<Player> players = new ArrayList<>();

    public BuildingManager(ArrayList<Player> players) {
        for (GamePhase gamePhase : GamePhase.values()) {
            buildingsMap.put(gamePhase, new HashMap<>());
            for (Player player : players) {
                buildingsMap.get(gamePhase).put(player.getName(), new ArrayList<>());
            }
        }
    }

    // Getter
    public Map<GamePhase, Map<String, ArrayList<BuildingCard>>> getBuildingsMap() { return buildingsMap; }

    public void addBuilding(BuildingCard buildingCard, Player player) {
        GamePhase gamePhase = buildingCard.getActivatedAt();
        buildingsMap.get(gamePhase).putIfAbsent(player.getName(), new ArrayList<>());
        buildingsMap.get(gamePhase).get(player.getName()).add(buildingCard);
    }

    //FORSE questi due metodi si possono unire se tutti quanti gli edifici usano context invece di player (che dovrebbe tra l'altro essere meglio)
    //da capire dove va chiamata questa roba e se serve un altro metodo in BuildingManager per chiamare tutti gli useBuilding() non ON_EVENT
    //a quel punto cambiare anche l'altra useBuilding in useEventBuilding() e non serve dirgli che siamo in ON_EVENT (duh)
    public void useBuilding(GamePhase gamePhase, Player player) {
        Map<String, ArrayList<BuildingCard>> phaseMap = buildingsMap.get(gamePhase);
        if(phaseMap == null) return;
        ArrayList<BuildingCard> playersCurrPhaseBuildings = phaseMap.getOrDefault(player.getName(), new ArrayList<>());
        for (BuildingCard buildingCard : playersCurrPhaseBuildings) {
            buildingCard.applyEffect();
        }

    }

    public void useBuilding(GamePhase gamePhase, EffectContext context, Class<? extends EventStrategy> eventType) {
        Player player = context.getPlayer();
        Map<String, ArrayList<BuildingCard>> phaseMap = buildingsMap.get(gamePhase);
        if(phaseMap == null) return;
        ArrayList<BuildingCard> playersCurrPhaseBuildings = phaseMap.getOrDefault(player.getName(), new ArrayList<>());
        for (BuildingCard buildingCard : playersCurrPhaseBuildings) {
            if (buildingCard.isUsedIn(eventType)) buildingCard.applyEffect(context);

        }
    }
}