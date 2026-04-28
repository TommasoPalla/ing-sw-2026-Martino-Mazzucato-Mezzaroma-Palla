package it.polimi.ingsw.Networking.Shared;

import it.polimi.ingsw.Controller.GameController;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Game.Game;

import java.util.concurrent.ConcurrentHashMap;

/**
 * This class has all the method of the model and some other
 * It is called by RMIServer and SocketServer, allowing them to modify
 * the model state without directly giving them access to it.
 */

public class ServerController {
    //questa classe deve inoltre essere in grado di notificare TUTTI i client,
    //indipendentemente dal protocollo, dei cambiamenti avvuti
    public record GameRecord(Game game, GameController gameController) {}
    private ConcurrentHashMap<String, GameRecord> activeGames;

    //public ServerController(){this.model = new Model();}    // il model del server e' inizializzato qui a una partita vuota e poi e' modificato giocando


    // le chiamate alle funzioni sono sempre dei blocchi con questa forma:
    /*
    public void f(x){
        synchronized(model){
            model.f(x);
        }
    }
    */
    public void addPlayerToGame(String gameID, String playerName, Color totemColor){
        try {
            GameController gameController = activeGames.get(gameID).gameController;
            gameController.addPlayer(playerName, totemColor);
        }
        catch (IllegalArgumentException e){

        }
    }

    /**
     * This method adds a new game to the list of active games
     *  and instantiates his game controller, so the game can start
    */
    public synchronized Game addNewGame(String firstPlayerName, Color totemColor, String gameID, int playerNum) {
        Game newGame = new Game(gameID, playerNum);
        this.addPlayerToGame(gameID, firstPlayerName, totemColor);
        GameController gameController = new GameController(newGame);
        activeGames.put(gameID, new GameRecord(newGame, gameController));
        return newGame;
    }


    /*
    ad esempio
    public void chooseOfferTile(){
        synchronized(model){
            model.chooseOfferTile();
        }
    }
     */
}
