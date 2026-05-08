package it.polimi.ingsw.Networking.Shared;

import it.polimi.ingsw.Controller.GameController;
import it.polimi.ingsw.CustomException.IllegalDrawException;
import it.polimi.ingsw.CustomException.UIException.NotEnoughPlayersException;
import it.polimi.ingsw.CustomException.UIException.NotJoinableGameException;
import it.polimi.ingsw.CustomException.UIException.NotTheHostException;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.CustomException.OccupiedTileException;
import it.polimi.ingsw.CustomException.UnavailableColorException;
import it.polimi.ingsw.View.GamePlayers;

import java.util.HashMap;
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

    // logic handling connected players
    public void removePlayerFromGame(PlayerRecord playerRecord){
        try {
            String playerName = playerRecord.playerName();
            int gameID = playerRecord.gameID();
            GameController gameController = activeGames.get(gameID).gameController;
            gameController.removePlayer(playerName); // old: gameController.removeClient(playerName), ma già fatto in removeNotifierFromGame()
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
        catch (NotJoinableGameException e){
            throw new NotJoinableGameException(e.getMessage());
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
        GameController gameController = new GameController(newGame);
        activeGames.put(gameID, new GameRecord(newGame, gameController));

        PlayerRecord newPlayer = new PlayerRecord(gameID, firstPlayerName);
        addPlayerToGame(newPlayer);
        //gameController.addPlayer(firstPlayerName); TODO: capire se e' da fare anche questo
        addNotifierToGame(newPlayer, notifier);

        notifier.notifyGameCreated(gameID, playerNum);

        nextGameID += 1;
        return newGame;
    }

    public void joinGame(ClientNotifier notifier, PlayerRecord newPlayer) {
        try {
            addPlayerToGame(newPlayer);
            addNotifierToGame(newPlayer, notifier);
            //notifier.notifyPlayerJoined(newPlayer);
        } catch (NotJoinableGameException e) {
            throw new NotJoinableGameException(e.getMessage());
        }
    }

    public void leaveGame(PlayerRecord leftingPlayer) {
        removeNotifierFromGame(leftingPlayer);
        removePlayerFromGame(leftingPlayer);
    }

    public void startGame(String requestingPlayer, int gameID) {
        try {
            activeGames.get(gameID).gameController().startGame(requestingPlayer);
        }
        catch (NotTheHostException e){
            throw new NotTheHostException(e.getMessage());
        }
        catch (NotEnoughPlayersException e){
            throw new NotEnoughPlayersException(e.getMessage());
        }
    }
    public Map<Integer, GamePlayers> getActiveGames(){
        Map<Integer, GamePlayers> gamesData = new HashMap<>();
        for (int ID: activeGames.keySet()){
            //andrebbe sincronizzato
            GamePlayers playersInfo = new GamePlayers(activeGames.get(ID).game().getNumPlayer(),
                    activeGames.get(ID).game().getPlayersNames());
            gamesData.put(ID, playersInfo);
        }
        return gamesData;
    }

    // logic of methods that modify the model state
    public void chooseTotemColor(PlayerRecord playerRecord, Color totemColor){
        GameController currentController = activeGames.get(playerRecord.gameID()).gameController();
        synchronized (currentController){
            try {
                currentController.chooseTotemColor(playerRecord, totemColor);
            }catch(UnavailableColorException e) {
                throw new UnavailableColorException(totemColor);
                }
            }
        }

    public void drawCard(PlayerRecord playerRecord, boolean fromTopRow, boolean fromBuildings, int index){
        GameController currentController = activeGames.get(playerRecord.gameID()).gameController();
        try{
            synchronized (currentController){
                currentController.handleDraw(playerRecord, fromTopRow, fromBuildings, index);
            }
        } catch (IllegalDrawException e){
            throw new IllegalDrawException();
        }
    }

    public void chooseOfferTile(PlayerRecord playerRecord, int index){
        GameController currentController = activeGames.get(playerRecord.gameID()).gameController();
        try {
            synchronized (currentController) {
                currentController.handleChooseOfferTile(playerRecord, index);
            }
        }
        catch(OccupiedTileException e){
            throw new OccupiedTileException();
        }
    }
}
