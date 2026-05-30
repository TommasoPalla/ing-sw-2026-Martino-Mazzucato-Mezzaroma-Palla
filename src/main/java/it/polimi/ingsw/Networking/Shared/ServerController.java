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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This class has all the method of the model and some other
 * It is called by RMIServer and SocketServer, allowing them to modify
 * the model state without directly giving them access to it.
 */

public class ServerController {
    private ArrayList<VirtualRMIClient> RMIClients = new ArrayList<>();
    private ArrayList<SocketClientHandler> socketClients = new ArrayList<>();

    //si potrebbe ottimizzare tenendo lista di clients non in partita (non giocatori) ma eviterebbe solo qualche aggiornamento inutile
    public record GameRecord(Game game, GameController gameController) {}
    private final Map<Integer, GameRecord> activeGames = new ConcurrentHashMap<>();

    private final ExecutorService notificationThreads = Executors.newCachedThreadPool();
    private static int nextGameID = 0;

    // logic handling connected players
    public void removeClientFromGame(PlayerRecord playerRecord){
        try {
            String playerName = playerRecord.playerName();
            int gameID = playerRecord.gameID();
            GameController gameController = activeGames.get(gameID).gameController();
            System.out.println("[SERVER] Removing player '" + playerName + "' from game " + gameID);
            gameController.removeClient(playerName);
        } catch (IllegalArgumentException e){
            System.out.println("[SERVER] ERROR: could not remove player from game\n" + e.getMessage());
            //probabilmente da cambiare con exception
        }
    }

    /**
     * This method creates a Player object in the corresponding game to which it is connected
     * @param playerRecord
     */
    //forse si può rimuovere e chiamare direttamente addClient di gameController negli usages, attenzione sincronizzazione
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
     * This method adds a new game to the list of active games
     *  and instantiates its game controller, so the game can start
     * @param firstPlayerName
     * @param playerNum
     * @return
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

        //addNotifierToGame(newPlayer, notifier);

        new Thread( () -> {
            try {
                notifier.notifyGameCreated(gameID, firstPlayerName, playerNum);
            } catch (Exception e){}
        }).start();

        notifyAvailableGames();
        nextGameID += 1;
        return nextGameID-1;
    }

    public void joinGame(ClientNotifier notifier, PlayerRecord newPlayer) {
        try {
            addClientToGame(newPlayer, notifier);
            notifyAvailableGames();
        } catch (NotJoinableGameException e) {
            throw new NotJoinableGameException(e.getMessage());
        }
    }

    public void leaveGame(PlayerRecord leavingPlayer) {
        if (activeGames.containsKey(leavingPlayer.gameID())) {
            System.out.println("[SERVER] Player '" + leavingPlayer.playerName() + "' leaving the game " + leavingPlayer.gameID());
            removeClientFromGame(leavingPlayer);
            if(activeGames.get(leavingPlayer.gameID()).gameController().getConnectedClients().isEmpty()) {
                System.out.println("[SERVER] Game " + leavingPlayer.gameID() + " is empty. Deleting game instance.");
                activeGames.remove(leavingPlayer.gameID());
            }
            notifyAvailableGames();
        }
    }

    public void startGame(String requestingPlayer, int gameID) {
        try {
            System.out.println("[SERVER] Start game " + gameID + " requested by host '" + requestingPlayer + "'");
            activeGames.get(gameID).gameController().startGame(requestingPlayer);
            notifyAvailableGames(); //superfluo probabilmente, quando si aggiunge l'ultimo player il game sarà già unavailable
        } catch (NotTheHostException e){
            throw new NotTheHostException(e.getMessage());
        } catch (NotEnoughPlayersException e){
            throw new NotEnoughPlayersException();
        } catch (TotemColorNotChosen e){
            throw new TotemColorNotChosen();
        }
    }

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
               } catch (Exception e) {}
            });
        }
        for(SocketClientHandler client : socketClients){
            notificationThreads.submit( () -> {
                client.notifyAvailableGames(gamesData);
            });
        }
    }

    // logic of methods that modify the model state
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

    public void drawCard(PlayerRecord playerRecord, boolean fromTopRow, boolean fromBuildings, int index){
        System.out.println("[GAME " + playerRecord.gameID() + "] Player '" + playerRecord.playerName() + "' drawing card from " + (fromTopRow ? "TOP" : "BOTTOM") + " row, index " + index);
        GameController currentController = activeGames.get(playerRecord.gameID()).gameController();
        String playerName = playerRecord.playerName();
        try{
            synchronized (currentController){
                currentController.handleDraw(playerName, fromTopRow, fromBuildings, index);
            }
        } catch (IllegalDrawException e){
            throw new IllegalDrawException();
        } catch (EndOfGameException e) {
            activeGames.remove(playerRecord.gameID());
        }
    }

    public void passTurn(PlayerRecord playerRecord){
        System.out.println("[SERVER] Game " + playerRecord.gameID() + ": Player '" + playerRecord.playerName() + " passing turn");
        GameController currentController = activeGames.get(playerRecord.gameID()).gameController();
        String playerName = playerRecord.playerName();
        synchronized (currentController){
            currentController.handlePassTurn(playerName);
        }
    }

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