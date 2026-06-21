package it.polimi.ingsw.Networking.Shared;

import it.polimi.ingsw.Controller.GameController;
import it.polimi.ingsw.CustomException.*;
import it.polimi.ingsw.CustomException.UIException.*;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Networking.RMI.RMIClientNotifier;
import it.polimi.ingsw.Networking.RMI.VirtualRMIClient;
import it.polimi.ingsw.Networking.Socket.SocketClientHandler;
import it.polimi.ingsw.View.GamePlayers;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class represents the only reference instanced of the server controller. This controller serves as the main
 * controller of all active games and of the communication between clients and their {@link GameController}.
 * It keeps trace of all the active games and of the clients connected to them, preserving a reference to their game
 * controllers to call their methods when needed. It has all the methods of the model and some other.
 * Its methods are accessed by RMIServer and SocketServer, allowing them to modify the model state without directly
 * giving them access to it. The reference of this server controller is created right after the
 * {@link it.polimi.ingsw.Apps.ServerApp} is run. It keeps open the communication both for RMI and Socket, and it also
 * saves a reference to a {@link LeaderboardDAO} which is used by this controller to communicate with the Mesos database.
 */
public class ServerController {

    /**
     * The list of clients using the RMI connection.
     */
    private ArrayList<VirtualRMIClient> RMIClients = new ArrayList<>();

    /**
     * The list of clients using the Socket TCP connection.
     */
    private ArrayList<SocketClientHandler> socketClients = new ArrayList<>();

    /**
     * A record containing the {@link Game} instance, associated to its {@link GameController}.
     * @param game the instance of a game.
     * @param gameController the instance of the controller of that game.
     */
    public record GameRecord(Game game, GameController gameController) {}

    /**
     * The map containing all the active games' IDs mapped to their respective {@link GameRecord}.
     */
    private final Map<Integer, GameRecord> activeGames = new ConcurrentHashMap<>();

    /**
     * A pool of threads used for sending notifications.
     */
    private final ExecutorService notificationThreads = Executors.newCachedThreadPool();

    /**
     * This value is increased every time a new game is created.
     */
    private static int nextGameID = 0;

    /**
     * True if a Mesos Database has been configured, false if not.
     */
    private boolean DBConnected = false;

    /**
     * It is used by the server controller to communicate via SQL queries with the MySQL Mesos database, which contains the
     * match history of all the Mesos games played.
     */
    private LeaderboardDAO leaderboardDAO;

    public void setDBConnected(boolean DBConnected) {
        this.DBConnected = DBConnected;
    }
    public void setLeaderboardDAO(LeaderboardDAO leaderBoardDAO) {
        this.leaderboardDAO = leaderBoardDAO;
    }

    /**
     * It removes a client from a game when he leaves the lobby.
     * @param playerRecord The record of that player, containing his name and the gameID of the game's lobby he was into.
     */
    public void removeClientFromGame(PlayerRecord playerRecord){
        try {
            String playerName = playerRecord.playerName();
            int gameID = playerRecord.gameID();
            GameController gameController = activeGames.get(gameID).gameController();
            System.out.println("[SERVER] Removing player '" + playerName + "' from game " + gameID);
            gameController.removeClient(playerName);
        } catch (IllegalArgumentException e){
            System.out.println("[SERVER] ERROR: could not remove player from game\n" + e.getMessage());
        }
    }

    /**
     * This method creates adds a player to the list of connected clients in the game controller of that game.
     * @param playerRecord the {@link PlayerRecord} containing the player's name and the gameID of the lobby he just
     *                     joined.
     * @param notifier the {@link ClientNotifier} interface connected to the player.
     */
    public synchronized void addClientToGame(PlayerRecord playerRecord, ClientNotifier notifier){
        try {
            String playerName = playerRecord.playerName();
            int gameID = playerRecord.gameID();
            System.out.println("[SERVER] Adding player '" + playerName + "' to game " + gameID);
            GameController gameController = activeGames.get(gameID).gameController();
            gameController.addClient(playerName, notifier);
        }
        catch (NotJoinableGameException e){
            System.err.println("[SERVER] Join failed: " + e.getMessage());
            throw new NotJoinableGameException(e.getMessage());
        }
    }
    /**
     * This method adds a new game to the list of active games and instantiates its game controller, so the
     * game can start.
     * @param notifier the {@link ClientNotifier} interface connected to the player who created the game.
     * @param firstPlayerName the name of the player who created the game.
     * @param playerNum the number of players needed to start the game, it is decided by the creator.
     * @return the ID of the game which was just created.
     */
    public synchronized int createNewGame(ClientNotifier notifier, String firstPlayerName, int playerNum) {
        int gameID = nextGameID;
        System.out.println("[SERVER] Creating new game (ID: " + gameID + ") for " + playerNum + " players. Host: " + firstPlayerName);
        Game newGame = new Game(gameID, playerNum);
        GameController gameController = new GameController(newGame);
        newGame.setController(gameController);
        
        activeGames.put(gameID, new GameRecord(newGame, gameController));

        PlayerRecord newPlayer = new PlayerRecord(gameID, firstPlayerName);
        addClientToGame(newPlayer, notifier);

        new Thread( () -> {
            try {
                notifier.notifyGameCreated(gameID, firstPlayerName, playerNum);
            } catch (Exception e){
                //TODO handle exception
            }
        }).start();

        notifyAvailableGames();
        nextGameID += 1;
        return nextGameID-1;
    }

    /**
     * Called when a player wants to join an existing game's lobby. It throws {@link NotJoinableGameException} if the
     * lobby was not joinable for some reason.
     * @param notifier the {@link ClientNotifier} interface of the player.
     * @param newPlayer the {@link PlayerRecord} of the player, containing its name and the gameID of the game he wants
     *                  to join.
     */
    public void joinGame(ClientNotifier notifier, PlayerRecord newPlayer) {
        int gameID = newPlayer.gameID();
        GameRecord gameRecord = activeGames.get(gameID);
        if (gameRecord == null) {
            throw new NotJoinableGameException("[ERROR]: game " + gameID + " is not joinable");
        }
        try {
            addClientToGame(newPlayer, notifier);
            notifyAvailableGames();
        } catch (NotJoinableGameException e) {
            throw new NotJoinableGameException(e.getMessage());
        }
    }

    /**
     * Called when a player wants to leave the game's lobby he is in
     * @param leavingPlayer the {@link PlayerRecord} of the player, containing its name and the gameID of the game's
     *                      lobby he wants to leave.
     */
    public void leaveGame(PlayerRecord leavingPlayer) {
        if (activeGames.containsKey(leavingPlayer.gameID())) {
            System.out.println("[SERVER] Player '" + leavingPlayer.playerName() + "' leaving the game " + leavingPlayer.gameID());
            removeClientFromGame(leavingPlayer);
            if(activeGames.get(leavingPlayer.gameID()).gameController().getConnectedClients().isEmpty() || activeGames.get(leavingPlayer.gameID()).gameController().getGameModel().isStarted()) {
                System.out.println("[SERVER] Game " + leavingPlayer.gameID() + " is empty. Deleting game instance.");
                activeGames.remove(leavingPlayer.gameID());
            }
            notifyAvailableGames();
        }
    }

    /**
     * Called by the host of a game to start it. It throws {@link NotTheHostException} if the player requesting to start
     * is not the host, {@link NotEnoughPlayersException} if there are not enough players in the lobby to start the game,
     * and {@link TotemColorNotChosen} if not all players have chosen the totem color.
     * @param requestingPlayer the name of the player requesting to start the game.
     * @param gameID the ID of the game to be started.
     */
    public void startGame(String requestingPlayer, int gameID) {
        try {
            System.out.println("[SERVER] Start game " + gameID + " requested by host '" + requestingPlayer + "'");
            activeGames.get(gameID).gameController().startGame(requestingPlayer);
            notifyAvailableGames();
        } catch (NotTheHostException e){
            throw new NotTheHostException(e.getMessage());
        } catch (NotEnoughPlayersException e){
            throw new NotEnoughPlayersException();
        } catch (TotemColorNotChosen e){
            throw new TotemColorNotChosen();
        }
    }

    /**
     * It notifies all clients connected to the server of the available active games.
     */
    public void notifyAvailableGames(){
        Map<Integer, GamePlayers> gamesData = new HashMap<>();
        synchronized (activeGames) {
            for (int ID : activeGames.keySet()) {
                int currentNumPlayers = activeGames.get(ID).gameController().getConnectedClients().size();
                int numPlayersThreshold = activeGames.get(ID).gameController().getGameModel().getNumPlayer();
                if(currentNumPlayers < numPlayersThreshold){
                    GamePlayers playersInfo = new GamePlayers(numPlayersThreshold,
                            activeGames.get(ID).gameController().getConnectedClients());
                    gamesData.put(ID, playersInfo);
                }
            }
        }

        for (VirtualRMIClient client : RMIClients){
            notificationThreads.submit( () -> {
               try {
                   RMIClientNotifier notifier = new RMIClientNotifier(client);
                   notifier.notifyAvailableGames(gamesData);
               } catch (Exception e) {
                   //TODO handle exception
               }
            });
        }
        for(SocketClientHandler client : socketClients){
            notificationThreads.submit( () -> client.notifyAvailableGames(gamesData));
        }
    }

    /*
     * logic of methods that modify the model state
     */

    /**
     * Request by a player to choose a totem color. It throws {@link UnavailableColorException} if the color
     * has already been picked by another player.
     * @param playerRecord the {@link PlayerRecord} of the player, containing its name and the gameID.
     * @param totemColor the {@link Color} value of the totem color chosen.
     */
    public void chooseTotemColor(PlayerRecord playerRecord, Color totemColor){
        System.out.println("[SERVER] Game " + playerRecord.gameID() + ": Player '" + playerRecord.playerName() + "' choosing totem color " + totemColor);
        GameController currentController = activeGames.get(playerRecord.gameID()).gameController();
        synchronized (currentController){
            try {
                currentController.chooseTotemColor(playerRecord.playerName(), totemColor);
            } catch(UnavailableColorException e) {
                throw new UnavailableColorException(totemColor);
            }
        }
    }

    /**
     * Forwards to the game controller the request by the player to draw a card. It catches and rethrows a
     * {@link IllegalDrawException} if the card was not drawable. It also catches a {@link EndOfGameException} threw
     * by the game Controller when the game is ended.
     * @param playerRecord the {@link PlayerRecord} of the player, containing its name and the gameID.
     * @param fromTopRow true if the card drawn is from top row, false if from bottom.
     * @param fromBuildings true if the card drawn is from the Building cards row, false if otherwise.
     * @param index the index of the array of cards from where the cards have been drawn.
     */
    public void drawCard(PlayerRecord playerRecord, boolean fromTopRow, boolean fromBuildings, int index) {
        System.out.println("[GAME " + playerRecord.gameID() + "] Player '" + playerRecord.playerName() + "' drawing card from " + (fromTopRow ? "TOP" : "BOTTOM") + " row, index " + index);
        GameController currentController = activeGames.get(playerRecord.gameID()).gameController();
        String playerName = playerRecord.playerName();
        try{
            synchronized (currentController){
                currentController.handleDraw(playerName, fromTopRow, fromBuildings, index);
            }
        } catch (IllegalDrawException e){
            throw new IllegalDrawException(e.getMessage());
        } catch (EndOfGameException e) {
            endGame(currentController);
        }
    }

    /**
     * Forwards to the game controller the request by the player to pass his drawing turn if there are no more
     * available Character cards to draw and the player doesn't want to draw a Building card. It catches a
     * {@link EndOfGameException} threw by the game Controller when the game is ended.
     * @param playerRecord the {@link PlayerRecord} of the player, containing its name and the gameID.
     */
    public void passTurn(PlayerRecord playerRecord) {
        System.out.println("[SERVER] Game " + playerRecord.gameID() + ": Player '" + playerRecord.playerName() + " passing turn");
        GameController currentController = activeGames.get(playerRecord.gameID()).gameController();
        String playerName = playerRecord.playerName();
        try{
            synchronized (currentController){
                currentController.handlePassTurn(playerName);
            }
        } catch (EndOfGameException e) {
            endGame(currentController);
        }
    }

    /**
     * Forwards to the game controller the request by the player to place his totem on an Offer Tile. It throws a
     * {@link InvalidSelectionException} if the player was not in turn or if the Offer Tile was already occupied by
     * another player.
     * @param playerRecord the {@link PlayerRecord} of the player, containing its name and the gameID.
     * @param index the index of the Offer Tile on the Offer Track.
     */
    public void chooseOfferTile(PlayerRecord playerRecord, int index) {
        GameController currentController = activeGames.get(playerRecord.gameID()).gameController();
        try {
            synchronized (currentController) {
                currentController.handleChooseOfferTile(playerRecord.playerName(), index);
            }
        } catch(IllegalActionPhaseException | IllegalActionTurnException | OccupiedTileException e) {
            throw new InvalidSelectionException(e);
        }
    }

    /**
     * It manages the final phase of a game. It takes every player's final score and saves it as a new record in the
     * external Mesos match history database. Finally, it notifies every player with the leaderboard of games with the
     * same number of players and their position in the leaderboard.
     * @param controller the {@link GameController} of the ending game.
     */
    private void endGame(GameController controller) {
        if (DBConnected) {
            Map<String,Integer> finalRanking = controller.getFinalRanking();
            int playersNum = finalRanking.size();
            try {
                for (String player : finalRanking.keySet()) {
                    leaderboardDAO.saveMatchResult(player, finalRanking.get(player), playersNum);
                }
                List<String> leaderboard = leaderboardDAO.getLeaderboard(playersNum);
                for (String player : finalRanking.keySet()) {
                    int playerPosition = leaderboardDAO.getPlayerPosition(playersNum, player);
                    controller.getConnectedClientsNotifiers().get(player).notifyLeaderboardInfo(leaderboard, playerPosition);
                }
            } catch (SQLException e1) {
                System.out.println("[ERROR]  Unable to connect to MySQL mesos database:");
                e1.printStackTrace();
            }
        }
        else {
            controller.notifyAll( n -> { n.notifyLeaderboardInfo(Collections.emptyList(),0);});
        }
        activeGames.remove(controller.getGameModel().getGameID());
    }

    public void handleDisconnection(PlayerRecord playerRecord){
        int gameID = playerRecord.gameID();
        GameRecord gameRecord = activeGames.get(gameID);
        if(gameRecord != null){
            if(gameRecord.game().isStarted()){
                System.err.println("[SERVER] Handling critical disconnection for '" + playerRecord.playerName() + "' in game " + gameID);
                gameRecord.gameController().handleCriticalDisconnection(playerRecord.playerName());
                activeGames.remove(gameID);
            }
            else{
                System.out.println("[SERVER] Handling lobby disconnection for '" + playerRecord.playerName() + "' in game " + gameID);
                leaveGame(playerRecord);
            }
            notifyAvailableGames();
        }
    }

    public void updateRMIClients(ArrayList<VirtualRMIClient> clients){
        RMIClients = clients;
    }

    public void updateSocketClients(ArrayList<SocketClientHandler> clients){
        socketClients = clients;
    }
}