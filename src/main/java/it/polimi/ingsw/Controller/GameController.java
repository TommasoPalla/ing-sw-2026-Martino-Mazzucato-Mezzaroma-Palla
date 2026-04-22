package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.GameBoard.OfferTrack;
import it.polimi.ingsw.Model.Users.Illegal_Draw_Exception;
import it.polimi.ingsw.Model.Users.Occupied_Tile_Exception;
import it.polimi.ingsw.Model.Users.Player;

/*
* Game controller of a single game instance, used to extract
* information from the View, such as player inputs, and
* to route them to the associated game model by the calls of its methods
 */
public class GameController {
    /*
    * This is the attributed of the game model associated to the controller
     */
    Game gameModel;

    // Da definire il client handler di un player
   // private final Map<Player, ClientHandler> connectedClients;

    // costruttore del controller
    public GameController(Game gameModel) {
        this.gameModel = gameModel;
    }

    public Game getGameModel() {
        return gameModel;
    }

    //public ArrayList<Player> getConnectedClients() {
    //return new ArrayList<>(connectedClients.keyset());
    //}

    // esempi di metodi del controller
    public synchronized void handleChooseOfferTile (Player player, int index, OfferTrack offerTrack) {
        try {
            player.chooseOfferTile(index, offerTrack);
        }
        catch (Occupied_Tile_Exception e) {
            // qui bisogna notificare l'errore al player e richiedergli di riselezionare un'altra tile
        }
    }

    public synchronized void handleDrawFromTopRow (Player player, int index, OfferTrack offerTrack) {
        try {
            player.drawFromTopRow(index, offerTrack);
        }
        catch (Illegal_Draw_Exception e) {
            // messaggio di errore per carta evento non pescabile
        }
    }


}
