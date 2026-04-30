package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.GameBoard.OfferTile;
import it.polimi.ingsw.Model.GameBoard.OfferTrack;
import it.polimi.ingsw.Model.Users.*;

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
    private final Game gameInstance;

    // Da definire il client handler di un player
   private Map<String, ClientController> connectedClients;

    /**
     * GameController's constructor is called in the GameManager when a new game is added
     */
    public GameController(Game gameInstance) {
        this.gameInstance = gameInstance;
        this.connectedClients = new HashMap<>();
        // da aggiungere gli handler, non so come
    }

    public Game getGameModel() {
        return gameInstance;
    }

    public ArrayList<String> getConnectedClients() {
        return new ArrayList<>(connectedClients.keySet());
    }

    /*
    * This method calls the respective method in Game to add a player while in Lobby State
     */
    public void addPlayer(String playerName, Color color) {
        if(gameInstance.getPlayersNames().contains(playerName)) throw new IllegalArgumentException("Player already exists");
        gameInstance.addPlayer(playerName, color);
    }

    public void addClient(String playerName, ClientController clientController) {
        connectedClients.put(playerName, clientController);
    }
    public void removeClient(String playerName) {
        connectedClients.remove(playerName);
    }

    /**
     * checks if the action is done during the right game phase
     */
    public synchronized boolean checkPhase(GamePhase phase) throws Illegal_Action_Phase_Exception {
        return phase == gameInstance.getGamePhase();
    }

    public synchronized Player setNextPlayer() {
        try {
            Player nextPlayer =  gameInstance.setNextPlayer();
            for (ClientController client : connectedClients.values()) {
                client.updateCurrentPlayer(nextPlayer.getName());
            }
            return nextPlayer;
        }
        catch (Last_Player_ofTurn_Exception e) {
            return null;
        }
    }

    // chiamata da parte client quando il player vuole startare il game.
    // catcha l'eccezione se cerca di far partire il game senza che tutti i giocatori siano entrati
    // (fase del game = INLOBBY)
    public void startGame() {
        try {
            gameInstance.startGame();
        }
        catch (Illegal_Action_Phase_Exception e) {

        }
    }

    /*
    * When a new round starts, after all the events are resolved and the rows are repopulated
    * @param turnOrder: the current order for placing totems
     */
    // ANCORA IN BOZZA. NO PLAYGAME() IN GAME MA FLOW DEL GAME DA ATTURARE TRAMITE CHIAMATE DI METODI NEL GAME CONTROLLER
    public void startTurn(ArrayList<Player> turnOrder) {
        try {
            int newRound = gameInstance.getNextRound();
            for (ClientController client : connectedClients.values()) {
                //inoltra chiamata a server controller per update round a tutti i player
                client.updateCurrentPlayer(turnOrder.getFirst().getName());
            }
            this.playTurn(turnOrder.getFirst());
        }
        catch (Last_Round_Exception e) {
            //throw EndOfGame_Exception();
        }
    }

    public void playTurn(Player player) {
        //????
    }

    public synchronized void handleChooseOfferTile (Player player, int index, OfferTrack offerTrack) {
        try {
            OfferTile chosen = player.chooseOfferTile(index, offerTrack);
            for(ClientController client : connectedClients.values()) {
                client.updateCurrentOfferTile(player.getName(), index);
            }
        }
        catch (Occupied_Tile_Exception e) {
            // qui bisogna notificare l'errore al player e richiedergli di riselezionare un'altra tile
        }
    }

    public synchronized void handleDraw(boolean fromTopRow, boolean fromBuilding, int index, String playerName){
            Player currPlayer = gameInstance.getPlayerByName(playerName);
            OfferTrack offerTrack = gameInstance.getOfferTrack();
            boolean cardIsDrawable = currPlayer.drawable(fromTopRow, fromBuilding, index, offerTrack);

            if(cardIsDrawable){
                currPlayer.drawCard(fromTopRow, fromBuilding, index, this.getGameModel().getOfferTrack());
            }
    }

    public synchronized void repopulateTopRow (Player player) {
        // Sarebbe sensato fare un try catch con un eccezione per quando finisce il gioco?
        ArrayList<Card> newTopRow = gameInstance.getOfferTrack().repopulateTopRow();
        for(ClientController client : connectedClients.values()) {
            client.updateTopRow(newTopRow);
        }
    }
}
