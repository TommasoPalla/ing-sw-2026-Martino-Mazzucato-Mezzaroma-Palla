package it.polimi.ingsw.Model.GameBoard;

import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Users.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

/**TurnTile class defines food modifiers based on players' positions
 * on the turn tile and the turn order. It has methods to manage turn order
 * and to re-allocate players to starting position, after their turn is finished.
 */
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

    public int[] getTileModifier() {return tileModifier;}

    /**initTurnOrder method is called only when the game is started,
     * unlike the TurnTile class constructor which is invoked right after
     * the OfferTrack class is instantiated by the Game class constructor
     * i.e. when the game is created and players are still in the lobby.
     * @param players list of players
     * @return the initial turn order, randomly selected.
     */
    //si potrebbe controllare che il numero di giocatori sia corretto, ma andrebbe passato il game per parametro
    //prima ad offertrack e poi qua, non vale la pena
    public ArrayList<Player> initTurnOrder(ArrayList<Player> players){
        turnOrder = new ArrayList<>(players);
        Collections.shuffle(turnOrder);
        return turnOrder;
    }

    //This function is used only in testing because the true one is non-deterministic
    public ArrayList<Player> initTurnOrderUnshuffled(ArrayList<Player> players){
        turnOrder = new ArrayList<>(players);
        return players;
    }

    public ArrayList<Player> getTurnOrder(){
        return turnOrder;
    }

    /**updateTurnOrder method sorts the players in the same order
     * they appear in the OfferTiles, starting from turn tile.
     * @return the new turnOrder
     */
    public ArrayList<Player> updateTurnOrder(){
        turnOrder = turnOrder.stream().sorted(Comparator.comparing(
                        p -> p.getCurrentOfferTile().getTileCode()))
                .collect(Collectors.toCollection(ArrayList::new));
        return turnOrder;
    }

    /**returnToStartingTile method assigns to player which has ended their turn
     * the correct food modifier based on their position in the turn tile,
     * then if the player has any buildings that activate in this phase,
     * the method applies the effect.
     * Finally, player is re-allocated to starting position.
     * @param returningPlayer player which has just ended their turn.
     * @param buildingManager to apply buildings' effects.
     */
    public void returnToStartingTile(Player returningPlayer, BuildingManager buildingManager){
        int foodModifier = tileModifier[turnOrder.indexOf(returningPlayer)];
        if(foodModifier > 0) buildingManager.useBuilding(GamePhase.RETURN_TO_TILE, returningPlayer);
        returningPlayer.freeOfferTile();
        returningPlayer.getTribe().modifyFood(foodModifier);
    }
}