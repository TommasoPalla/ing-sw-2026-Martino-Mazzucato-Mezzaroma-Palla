package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.GameBoard.OfferTile;
import it.polimi.ingsw.Model.GameBoard.OfferTrack;
import it.polimi.ingsw.Model.Users.Illegal_Action_Phase_Exception;
import it.polimi.ingsw.Model.Users.Illegal_Draw_Exception;
import it.polimi.ingsw.Model.Users.Occupied_Tile_Exception;
import it.polimi.ingsw.Model.Users.Player;
import it.polimi.ingsw.View.ClientController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Game controller of a single game instance, used to extract
 * information from the View, such as player inputs, and
 * to route them to the associated game model by the calls of its methods
 */
public class GameController {
    /*
    * This is the attributed of the game model associated to the controller
     */
    private final Game gameModel;

    // Da definire il client handler di un player
   private Map<Player, ClientController> connectedClients;

    /**
     * GameController's constructor is called in the GameManager when a new game is added
     */
    public GameController(Game gameModel) {
        this.gameModel = gameModel;
        this.connectedClients = new HashMap<>();
        // da aggiungere gli handler, non so come

        for (Player player : gameModel.getPlayers()) {
            new ClientController(this, player);
        }
        gameModel.startGame();
    }

    public Game getGameModel() {
        return gameModel;
    }

    public ArrayList<Player> getConnectedClients() {
        return new ArrayList<>(connectedClients.keySet());
    }

    public void addClient(Player player, ClientController clientController) {
        connectedClients.put(player, clientController);
    }
    public void removeClient(Player player) {
        connectedClients.remove(player);
    }

    /**
     * checks if the action is done during the right game phase
     */
    public synchronized boolean checkPhase(GamePhase phase) throws Illegal_Action_Phase_Exception {
        return phase == gameModel.getGamePhase();
    }

    public synchronized Player setNextPlayer() {return null;}
    public synchronized void handleChooseOfferTile (Player player, int index, OfferTrack offerTrack) {
        try {
            OfferTile chosen = player.chooseOfferTile(index, offerTrack);
            for(ClientController client : connectedClients.values()) {
                client.updateCurrentOfferTile(chosen);
            }
        }
        catch (Occupied_Tile_Exception e) {
            // qui bisogna notificare l'errore al player e richiedergli di riselezionare un'altra tile
        }
    }

    public synchronized void handleDrawFromTopRow (Player player, int index, OfferTrack offerTrack) {
        try {
            Card card = player.drawFromTopRow(index, offerTrack);
            CharacterCard cardDrawn = (CharacterCard) card;
            for(ClientController client : connectedClients.values()) {
                client.updateCardDrawn(cardDrawn);
                client.updateTopRow(gameModel.getOfferTrack().getTopRow());
            }
        }
        catch (Illegal_Draw_Exception e) {
            // messaggio di errore per carta evento non pescabile
        }
    }

    public synchronized void handleDrawFromBottomRow (Player player, int index, OfferTrack offerTrack) {
        try {
            Card card = player.drawFromBottomRow(index, offerTrack);
            CharacterCard cardDrawn = (CharacterCard) card;
            for(ClientController client : connectedClients.values()) {
                client.updateCardDrawn(cardDrawn);
                client.updateBottomRow(gameModel.getOfferTrack().getTopRow());
            }
        }
        catch (Illegal_Draw_Exception e) {
            // messaggio di errore per carta evento non pescabile
        }
    }

    public synchronized void repopulateTopRow (Player player) {
        // Sarebbe sensato fare un try catch con un eccezione per quando finisce il gioco?
        ArrayList<Card> newTopRow = gameModel.getOfferTrack().repopulateTopRow();
        for(ClientController client : connectedClients.values()) {
            client.updateTopRow(newTopRow);
        }
    }
}
