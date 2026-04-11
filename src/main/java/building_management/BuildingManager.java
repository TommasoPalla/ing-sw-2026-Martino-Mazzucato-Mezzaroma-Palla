package building_management;

import cards_and_deck.BuildingCard;
import enums.GamePhase;
import event_management.EventStrategy;
import users.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BuildingManager {
    /*
    Map<Player, ArrayList<BuildingCard>> onDrawBuildings = new HashMap<>();
    Map<Player, ArrayList<BuildingCard>> endGameBuildings = new HashMap<>();
    Map<Player, ArrayList<BuildingCard>> onEventBuildings = new HashMap<>();
    Map<Player, ArrayList<BuildingCard>> endTurnBuildings = new HashMap<>();
    Map<Player, ArrayList<BuildingCard>> startTurnBuildings = new HashMap<>();
     */
    Map<GamePhase, Map<Player, ArrayList<BuildingCard>>> buildingsMap = new HashMap<>();

    public BuildingManager(){
        for(GamePhase gamePhase : GamePhase.values()){
            buildingsMap.put(gamePhase, new HashMap<>());
        }
    }

    public void addBuilding(BuildingCard buildingCard, Player player){
        GamePhase gamePhase = buildingCard.getActivatedAt();
        buildingsMap.get(gamePhase).putIfAbsent(player, new ArrayList<>());
        buildingsMap.get(gamePhase).get(player).add(buildingCard);
    }


    // BuildingCard o cardID in input????
    // Building is added to the player's list in the specific map based on the building activation time
    // called in Tribe
    /*
    public void addBuilding(BuildingCard buildingCard, Player player) {
        switch (buildingCard.getActivatedAt()) {
            case ON_DRAW:
                onDrawBuildings.putIfAbsent(player, new ArrayList<>());
                onDrawBuildings.get(player).add(buildingCard);
                //questo if e' quanto di piu' lontano ci sia dal "questo e' il progetto di ingegneria del software"
                if(buildingCard.getCardID().equals("loremipsum")) {
                    buildingCard.effectOnPurchase();
                }
                break;
            case END_GAME:
                endGameBuildings.putIfAbsent(player, new ArrayList<>());
                endGameBuildings.get(player).add(buildingCard);
                break;
            case ON_EVENT:
                onEventBuildings.putIfAbsent(player, new ArrayList<>());
                onEventBuildings.get(player).add(buildingCard);
                break;
            case END_TURN:
                endTurnBuildings.putIfAbsent(player, new ArrayList<>());
                endTurnBuildings.get(player).add(buildingCard);
                break;
            case START_TURN:
                startTurnBuildings.putIfAbsent(player, new ArrayList<>());
                startTurnBuildings.get(player).add(buildingCard);
                break;
        }
    }
     */

    //FORSE questi due metodi si possono unire se tutti quanti gli edifici usano context invece di player (che dovrebbe tra l'altro essere meglio)
    public void useBuilding(GamePhase gamePhase, Player player){
        ArrayList<BuildingCard> playersCurrPhaseBuildings = buildingsMap.get(gamePhase).get(player);
        for(BuildingCard buildingCard : playersCurrPhaseBuildings){
            buildingCard.applyEffect();
        }

    }
    public void useBuilding(GamePhase gamePhase, EffectContext context, Class<? extends EventStrategy> eventType){
        Player player = context.getPlayer();
        ArrayList<BuildingCard> playersCurrPhaseBuildings = buildingsMap.get(gamePhase).get(player);
        for(BuildingCard buildingCard : playersCurrPhaseBuildings){
            if(buildingCard.isUsedIn(eventType)) buildingCard.applyEffect(context);

        }
    }

    // Called in Game, during every game phase, to activate only the buildings in the player's list
    // of that specific game phase
    /*
    public void useBuilding(GamePhase gamePhase, Player player) {
        switch (gamePhase) {
            case ON_DRAW:
                for (BuildingCard building : onDrawBuildings.get(player)) {
                    building.applyEffect();
                }
                break;
            case END_GAME:
                for (BuildingCard building: endGameBuildings.get(player)) {
                    building.applyEffect();
                }
                break;
            case ON_EVENT:
                for (BuildingCard building : onEventBuildings.get(player)) {
                    building.applyEffect();
                }
                break;
            case END_TURN:
                for (BuildingCard building: endTurnBuildings.get(player)) {
                    building.applyEffect();
                }
                break;
            case START_TURN:
                for (BuildingCard building: startTurnBuildings.get(player)) {
                    building.applyEffect();
                }
                break;
        }

    }
     */
}
