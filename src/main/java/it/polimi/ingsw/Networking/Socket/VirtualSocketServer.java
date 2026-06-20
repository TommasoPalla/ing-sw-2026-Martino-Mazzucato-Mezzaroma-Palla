package it.polimi.ingsw.Networking.Socket;

import it.polimi.ingsw.CustomException.UIException.NotEnoughPlayersException;
import it.polimi.ingsw.CustomException.UIException.NotTheHostException;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.CustomException.IllegalDrawException;
import it.polimi.ingsw.CustomException.OccupiedTileException;
import it.polimi.ingsw.Networking.Shared.ClientNotifier;
import it.polimi.ingsw.Networking.Shared.PlayerRecord;

import java.rmi.RemoteException;

/**
 * Remote interface defining the actions a client can perform on the server via TCP.
 * Analogous to the RMI-based message system but using method invocation.
 */

public interface VirtualSocketServer {
    /**
     * Establishes a connection with the server and registers the client's handler for callbacks.
     * @param handler the remote reference to the client.
     */
    void connect(SocketClientHandler handler);

    /**
     * Selects an offer tile on the track.
     * @param clientHandler the handler of the client performing the action.
     * @param index the index of the chosen tile.
     * @throws OccupiedTileException if the tile is already taken by another player.
     */
    void chooseOfferTile(int index, SocketClientHandler clientHandler) throws OccupiedTileException;

    /**
     * Draws a card from the offer track.
     * @param clientHandler the handler of the client performing the action.
     * @param fromTopRow true to draw from the top row, false for bottom.
     * @param fromBuildings true if drawing a building, false for character.
     * @param index the position of the card.
     */
    void drawCard(boolean fromTopRow, boolean fromBuildings, int index, SocketClientHandler clientHandler);

    /**
     * Chooses a totem color for the current game.
     * @param clientHandler the handler of the client performing the action.
     * @param totemColor the selected Color.
     */
    void chooseTotemColor(Color totemColor, SocketClientHandler clientHandler);

    /**
     * Allows a player that cannot draw any card to pass his turn
     * @param clientHandler the client that requested it
     */
    void passTurn(SocketClientHandler clientHandler);

    /**
     * Joins an existing game lobby.
     * @param clientHandler the stub of the client joining.
     */
    void joinGame(SocketClientHandler clientHandler);

    /**
     * Leaves the current game lobby.
     * @param leavingPlayer the name of the player leaving.
     */
    void leaveGame(PlayerRecord leavingPlayer);

    void createGame(ClientNotifier notifier, String playerName, int numPlayers);

    /**
     * Starts the game for all players in the lobby. Only the host can call this.
     * @param requestingPlayerName the name of the player requesting the start.
     * @param gameID the ID of the game to start.
     * @throws NotTheHostException if the requester is not the game host.
     * @throws NotEnoughPlayersException if the lobby doesn't have the required number of players.
     */
    void startGame(String requestingPlayerName, int gameID);

    /**
     * Heartbeat signal sent by the client to prove it is still alive.
     * @param client the stub of the client sending the ping.
     */
    void ping(SocketClientHandler client);
}
