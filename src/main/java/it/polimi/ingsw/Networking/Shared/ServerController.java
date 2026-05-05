package it.polimi.ingsw.Networking.Shared;

import it.polimi.ingsw.Controller.GameController;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.Users.OccupiedTileException;
import it.polimi.ingsw.Model.Users.UnavailableColorException;
import it.polimi.ingsw.Networking.RMI.RMIClientNotifier;
import it.polimi.ingsw.Networking.RMI.VirtualRMIClient;

import java.util.Map;
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
    private final Map<Integer, GameRecord> activeGames = new ConcurrentHashMap<>();
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

    /**
     * This method creates a Player object in the corresponding game to which it is connected
     * @param playerRecord
     */
    public void addPlayerToGame(PlayerRecord playerRecord){
        try {
            String playerName = playerRecord.playerName();
            int gameID = playerRecord.gameID();
            GameController gameController = activeGames.get(gameID).gameController();
            gameController.addPlayer(playerName);
        }
        catch (IllegalArgumentException e){
            System.out.println("ERROR: could not add player to game\n" + e.getMessage());
        }
    }

    public void addNotifierToGame(PlayerRecord playerRecord, ClientNotifier clientNotifier){
        GameController controller = activeGames.get(playerRecord.gameID()).gameController();
        controller.addClient(playerRecord.playerName(), clientNotifier);
    }
    public void removeNotifierFromGame(PlayerRecord playerRecord){
        GameController controller = activeGames.get(playerRecord.gameID()).gameController();
        controller.removeClient(playerRecord.playerName());
    }

    /**
     * This method adds a new game to the list of active games
     *  and instantiates its game controller, so the game can start
     * @param firstPlayerName
     * @param playerNum
     * @return
     */
    public synchronized Game createNewGame(ClientNotifier notifier, String firstPlayerName, int playerNum) {
        int gameID = nextGameID;
        Game newGame = new Game(gameID, playerNum);
        PlayerRecord newPlayer = new PlayerRecord(gameID, firstPlayerName);
        GameController gameController = new GameController(newGame);
        activeGames.put(gameID, new GameRecord(newGame, gameController));
        addPlayerToGame(newPlayer);
        addNotifierToGame(newPlayer, notifier);

        nextGameID += 1;
        return newGame;
    }

    public void chooseTotemColor(PlayerRecord playerRecord, Color totemColor){
        GameController currentController = activeGames.get(playerRecord.gameID()).gameController();
        synchronized (currentController){
            try {
                currentController.chooseTotemColor(playerRecord, totemColor);
            }catch(UnavailableColorException e) {
                throw new UnavailableColorException("colore già scelto");
                }
            }
        }



    public void drawCard(PlayerRecord playerRecord, boolean fromTopRow, boolean fromBuildings, int index){
        GameController currentController = activeGames.get(playerRecord.gameID()).gameController();
        synchronized (currentController){
            currentController.handleDraw(playerRecord, fromTopRow, fromBuildings, index);
        }
    }

    public void chooseOfferTile(PlayerRecord playerRecord, int index){
        try {
            GameController currentController = activeGames.get(playerRecord.gameID()).gameController();
            synchronized (currentController) {
                currentController.handleChooseOfferTile(playerRecord, index);
            }
        }
        catch(OccupiedTileException e){
            throw new OccupiedTileException();
        }
    }
}
