package it.polimi.ingsw.Model.GameBoard;

import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Users.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

public class TurnTile {
    private int[] tileModifier;
    private ArrayList<Player> turnOrder;

    public TurnTile(int numPlayers){
        turnOrder = new ArrayList<>();
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

    public ArrayList<Player> initTurnOrder(ArrayList<Player> players){
        ArrayList<Player> turnOrder = new ArrayList<>(players);
        Collections.shuffle(turnOrder);
        return turnOrder;
    }

    public int[] getTileModifier() {return tileModifier;}
    // ATT!!! in questo modo non si rispetta l'ordine dettato dal gioco in cui la turn tile si popola
    // progressivamente e il cibo è preso dal giocatore appena finisce di pescare le sue carte
    // Questo errore qui sopra dovrebbe essere stato risolto con l'aggiunta di TurnTile
    // qualcuno che possa confermare elimini questi commenti se e' d'accordo

    public ArrayList<Player> getTurnOrder(ArrayList<Player> turnOrder){
        return turnOrder.stream().sorted(Comparator.comparing(
                p -> p.getCurrentOfferTile().getTileCode()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public void returnToStartingTile(ArrayList<Player> turnOrder, Player returningPlayer, BuildingManager buildingManager){
        int foodModifier = tileModifier[turnOrder.indexOf(returningPlayer)];
        if(foodModifier > 0) buildingManager.useBuilding(GamePhase.RETURN_TO_TILE, returningPlayer);
        returningPlayer.getCurrentOfferTile().free();
        returningPlayer.getTribe().modifyFood(foodModifier);
    }
}