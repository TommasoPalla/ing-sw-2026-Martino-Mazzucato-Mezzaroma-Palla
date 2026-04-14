package game_board;

import building_management.BuildingManager;
import enums.GamePhase;
import users.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

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

    public int[] getTileModifier() {return tileModifier;}
    // ATT!!! in questo modo non si rispetta l'ordine dettato dal gioco in cui la turn tile si popola
    // progressivamente e il cibo è preso dal giocatore appena finisce di pescare le sue carte

    /*public ArrayList<Player> getTurnOrder(ArrayList<Player> turnOrder){
        ArrayList<Player> newTurns = new ArrayList<>();
        for(Player player : turnOrder){//creazione newTurns per riordinare i turni
            newTurns.add(player);
        }
        newTurns=newTurns.stream().sorted(Comparator.comparing( OfferTile :: getTileCode))
                .map(OfferTile::getCurrentOccupant).collect(Collectors.toCollection(ArrayList::new));
        return newTurns;
    }*/
    public ArrayList<Player> getTurnOrder(ArrayList<Player> turnOrder){

        return turnOrder.stream().sorted(Comparator.comparing(
                p -> p.getCurrentOfferTile().getTileCode()))
                .collect(Collectors.toCollection(ArrayList::new));
    }
    public void returnToStartingTile(ArrayList<Player> turnOrder, int j, BuildingManager buildingManager){
        int foodModifier = tileModifier[j];
        if(foodModifier > 0) buildingManager.useBuilding(GamePhase.RETURN_TO_TILE, turnOrder.get(j));
        turnOrder.get(j).getCurrentOfferTile().free();
        turnOrder.get(j).getTribe().modifyFood(tileModifier[j]);

    }

}
