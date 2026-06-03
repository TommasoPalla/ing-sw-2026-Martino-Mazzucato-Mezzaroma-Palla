package it.polimi.ingsw.Model.BuildingsManagement;

import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.EventManagement.EventStrategy;
import it.polimi.ingsw.Model.Users.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * This class serves as a manager for buildings activation. When a player purchases a building, the reference is stored
 * in a map based on his activation phase. When a certain {@link GamePhase} starts or an event occurs, the building
 * manager calls the overridden apply method of the respective buildings. Only the building of that game phase which
 * override the apply method with that exact signature will be activated. To activate the buildings of a certain type,
 * it uses two different type of useBuilding methods, one for normal buildings and one for building which are activated
 * during certain events.
 */
public class BuildingManager {

    /**
     * It maps from the {@link GamePhase} to another map. This second map contains the players' names as its
     * keys, and the values are arrays of {@link BuildingCard} which are in that player's tribe.
     */
    Map<GamePhase, Map<String, ArrayList<BuildingCard>>> buildingsMap = new HashMap<>();

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

    /**
     * When a new building is purchased it is added to the buildings map.
     * @param buildingCard the {@link BuildingCard} which has been purchased.
     * @param player the name of the player who purchased it.
     */
    public void addBuilding(BuildingCard buildingCard, Player player) {
        GamePhase gamePhase = buildingCard.getActivatedAt();
        buildingsMap.get(gamePhase).putIfAbsent(player.getName(), new ArrayList<>());
        buildingsMap.get(gamePhase).get(player.getName()).add(buildingCard);
    }

    /**
     * Activates a player's building when the specified game phase occurs.
     * @param gamePhase the {@link GamePhase} of activation.
     * @param player the name of the player whose buildings have to be activated.
     */
    public void useBuilding(GamePhase gamePhase, Player player) {
        Map<String, ArrayList<BuildingCard>> phaseMap = buildingsMap.get(gamePhase);
        if(phaseMap == null) return;
        ArrayList<BuildingCard> playersCurrPhaseBuildings = phaseMap.getOrDefault(player.getName(), new ArrayList<>());
        for (BuildingCard buildingCard : playersCurrPhaseBuildings) {
            buildingCard.applyEffect();
        }

    }

    /**
     * Activates a player's buildings which activation time is during events. It receives a {@link EffectContext} as
     * input to know which kind of parameters will be modified during that event.
     * @param gamePhase the {@link GamePhase} of activation.
     * @param context the {@link EffectContext} containing the player name and the parameters for that event.
     * @param eventType the strategy value indicating the {@link it.polimi.ingsw.Enums.EventType}
     */
    public void useBuilding(GamePhase gamePhase, EffectContext context, Class<? extends EventStrategy> eventType) {
        Player player = context.getPlayer();
        Map<String, ArrayList<BuildingCard>> phaseMap = buildingsMap.get(gamePhase);
        if(phaseMap == null) return;
        ArrayList<BuildingCard> playersCurrPhaseBuildings = phaseMap.getOrDefault(player.getName(), new ArrayList<>());
        for (BuildingCard buildingCard : playersCurrPhaseBuildings) {
            if (buildingCard.isUsedIn(eventType)) {
                System.out.println("[DEBUG] Building " + buildingCard.getCardID()  + " is used in event " + eventType.getSimpleName());
                buildingCard.applyEffect(context);
            }

        }
    }
}