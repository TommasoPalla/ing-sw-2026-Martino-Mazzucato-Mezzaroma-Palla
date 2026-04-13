package building_management.buildings;

import building_management.BuildingManager;
import enums.GamePhase;
import users.Player;

import java.util.ArrayList;

public class TurnTile {
    private int[] tileModifier;
    private ArrayList<Player> turnOrder;

    public TurnTile(int numPlayers){
        switch (numPlayers){
            case 2:
                tileModifier = new int[]{1, -1};
                break;
            case 3:
                tileModifier = new int[]{2, 0, -1};
                break;
            case 4:
                tileModifier = new int[]{2, 1, 0, -1};
                break;
            case 5:
                tileModifier = new int[]{3, 1, 0, 0, -1};

        }
    }

    public void foodReward(ArrayList<Player> turnOrder, BuildingManager buildingManager){
        for(Player player : turnOrder){
            int foodModifier = tileModifier[turnOrder.indexOf(player)];
            if(foodModifier > 0) buildingManager.useBuilding(GamePhase.END_TURN, player);
            player.getTribe().modifyFood(foodModifier);
        }
    }
}
