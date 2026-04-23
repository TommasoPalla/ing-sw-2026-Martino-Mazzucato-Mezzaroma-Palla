package it.polimi.ingsw.Model.Game;

import it.polimi.ingsw.Enums.Color;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/*
* This classes manages different games instances. It's uses a Singleton design pattern. When a new game is added,
* a game ID, and a list of players (mapped from nickname to totem color) are passed as parameters
 */
public class GameManager {
    private static GameManager instance;
    private ConcurrentHashMap<String, Game> activeGames;

    public GameManager() {
        activeGames = new ConcurrentHashMap<>();
    }

    public static synchronized GameManager getInstance() {
        if (instance == null)
            instance = new GameManager();
        return instance;
    }

    public synchronized ConcurrentHashMap<String, Game> getActiveGames() {
        return activeGames;
    }

    public Game getActiveGameByID(String gameID) {
        return activeGames.get(gameID);
    }

    public synchronized Game addNewGame(String gameID, Map<String, Color> players) {
        Game newGame = new Game(gameID, players);
        activeGames.put(gameID, newGame);
        return newGame;
    }

    public Game endGame(String gameID) {
        return activeGames.remove(gameID);
    }
}
