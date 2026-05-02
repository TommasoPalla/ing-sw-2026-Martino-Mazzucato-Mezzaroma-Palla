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
    private final ConcurrentHashMap<Integer, GameRecord> activeGames = new ConcurrentHashMap<>();
    private static int nextGameID = 0;

    public void removePlayerFromGame(PlayerRecord playerRecord){
        try {
            String playerName = playerRecord.playerName();
            int gameID = playerRecord.gameID();
            GameController gameController = activeGames.get(gameID).gameController;
            gameController.removeClient(playerName);
        } catch (IllegalArgumentException e){
            System.out.println("ERROR: could not remove player from game\n" + e.getMessage());
        }
    }

    public void addPlayerToGame(PlayerRecord playerRecord){
        try {
            String playerName = playerRecord.playerName();
            int gameID = playerRecord.gameID();
            GameController gameController = activeGames.get(gameID).gameController;
            gameController.addPlayer(playerName);
        }
        catch (IllegalArgumentException e){
            System.out.println("ERROR: could not add player to game\n" + e.getMessage());
        }
    }

    /**
     * This method adds a new game to the list of active games
     *  and instantiates his game controller, so the game can start
    */
    public synchronized Game crateNewGame(String firstPlayerName, int playerNum) {
        int gameID = nextGameID;
        Game newGame = new Game(gameID, playerNum);
        PlayerRecord newPlayer = new PlayerRecord(gameID, firstPlayerName);
        addPlayerToGame(newPlayer);
        GameController gameController = new GameController(newGame);
        activeGames.put(gameID, new GameRecord(newGame, gameController));

        nextGameID += 1;
        return newGame;
    }


    // le chiamate alle funzioni sono sempre dei blocchi con questa forma:
    /*
    public void f(x){
        synchronized(model){
            model.f(x);
        }
    }

    ad esempio
    public void chooseOfferTile(){
        synchronized(model){
            model.chooseOfferTile();
        }
    }
     */

    public void chooseTotemColor(PlayerRecord playerRecord, Color totemColor){
        GameController currentController = activeGames.get(playerRecord.gameID()).gameController();
        synchronized (currentController){
            currentController.chooseTotemColor(playerRecord, totemColor);
        }

    }

    public void drawCard(PlayerRecord playerRecord, boolean fromTopRow, boolean fromBuildings, int index){
        GameController currentController = activeGames.get(playerRecord.gameID()).gameController();
        synchronized (currentController){
            currentController.handleDraw(playerRecord, fromTopRow, fromBuildings, index);
        }
    }
}
