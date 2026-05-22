package it.polimi.ingsw.Networking.Socket;

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
     * Disconnects the client from the server and unregisters its handler.
     * @param handler the remote reference of the client to disconnect.
     */
    void disconnect(SocketClientHandler handler);   //forse da togliere siccome la disconnessione avviene diversamente per socket

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
     * @throws IllegalDrawException if the draw action violates game rules.
     */
    void drawCard(boolean fromTopRow, boolean fromBuildings, int index, SocketClientHandler clientHandler) throws IllegalDrawException;

    /**
     * Chooses a totem color for the current game.
     * @param clientHandler the handler of the client performing the action.
     * @param totemColor the selected Color.
     */
    void chooseTotemColor(Color totemColor, SocketClientHandler clientHandler);

    /**
     * Joins an existing game lobby.
     * @param clientHandler the stub of the client joining.
     */
    void joinGame(SocketClientHandler clientHandler);

    void leaveGame(PlayerRecord leavingPlayer);

    void createGame(ClientNotifier notifier, String playerName, int numPlayers);

    void startGame(String requestingPlayerName, int gameID);


    void ping(SocketClientHandler client);
}
