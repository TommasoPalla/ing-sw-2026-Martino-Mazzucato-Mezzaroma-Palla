package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.CustomException.UIException.NotEnoughPlayersException;
import it.polimi.ingsw.CustomException.UIException.NotTheHostException;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.CustomException.IllegalDrawException;
import it.polimi.ingsw.CustomException.OccupiedTileException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Remote interface defining the actions a client can perform on the server via RMI.
 * Analogous to the Socket-based message system but using method invocation.
 */
public interface VirtualRMIServer extends Remote {
    /**
     * Establishes a connection with the server and registers the client's stub for callbacks.
     * @param clientStub the remote reference to the client.
     * @throws RemoteException if RMI communication fails.
     */
    void connect(VirtualRMIClient clientStub) throws RemoteException;

    /**
     * Disconnects the client from the server and unregisters its stub.
     * @param clientStub the remote reference of the client to disconnect.
     * @throws RemoteException if RMI communication fails.
     */
    void disconnect(VirtualRMIClient clientStub) throws RemoteException;

    /**
     * Selects an offer tile on the track.
     * @param client the stub of the client performing the action.
     * @param index the index of the chosen tile.
     * @throws OccupiedTileException if the tile is already taken by another player.
     * @throws RemoteException if RMI communication fails.
     */
    void chooseOfferTile(VirtualRMIClient client, int index) throws OccupiedTileException, RemoteException;

    /**
     * Draws a card from the offer track.
     * @param client the stub of the client performing the action.
     * @param fromTopRow true to draw from the top row, false for bottom.
     * @param fromBuildings true if drawing a building, false for character.
     * @param index the position of the card.
     * @throws RemoteException if RMI communication fails.
     */
    void drawCard(VirtualRMIClient client, boolean fromTopRow, boolean fromBuildings, int index) throws RemoteException;

    /**
     * When the player in turn still has remaining draws but has no more character cards to draw, and he doesn't want
     * to draw a building card, he can decide to skip his turn to the next player.
     * @param client the stub of the client performing the action.
     * @throws RemoteException if RMI communication fails.
     */
    void passTurn(VirtualRMIClient client) throws RemoteException;

    /**
     * Chooses a totem color for the current game.
     * @param client the stub of the client performing the action.
     * @param totemColor the selected Color.
     * @throws RemoteException if RMI communication fails.
     */
    void chooseTotemColor(VirtualRMIClient client, Color totemColor) throws  RemoteException;

    /**
     * Joins an existing game lobby.
     * @param client the stub of the client joining.
     * @param playerName the chosen name of the player.
     * @param gameID the ID of the game to join.
     * @throws RemoteException if RMI communication fails.
     */
    void joinGame(VirtualRMIClient client, String playerName, int gameID) throws  RemoteException;

    /**
     * Leaves the current game lobby.
     * @param playerName the name of the player leaving.
     * @param gameID the ID of the game being left.
     * @throws RemoteException if RMI communication fails.
     */
    void leaveGame(String playerName, int gameID) throws RemoteException;

    /**
     * Starts the game for all players in the lobby. Only the host can call this.
     * @param playerName the name of the player requesting the start.
     * @param gameID the ID of the game to start.
     * @throws NotTheHostException if the requester is not the game host.
     * @throws NotEnoughPlayersException if the lobby doesn't have the required number of players.
     * @throws RemoteException if RMI communication fails.
     */
    void startGame(String playerName, int gameID) throws NotTheHostException, NotEnoughPlayersException, RemoteException;

    /**
     * Creates a new game with a specified number of players.
     * @param client the stub of the client creating the game.
     * @param playerName the name of the first player (host).
     * @param numPlayers the intended number of players for the game.
     * @throws RemoteException if RMI communication fails.
     */
    void createGame(VirtualRMIClient client, String playerName, int numPlayers) throws RemoteException;

    /**
     * Heartbeat signal sent by the client to prove it is still alive.
     * @param client the stub of the client sending the ping.
     * @throws RemoteException if RMI communication fails.
     */
    void ping(VirtualRMIClient client) throws RemoteException;
}
