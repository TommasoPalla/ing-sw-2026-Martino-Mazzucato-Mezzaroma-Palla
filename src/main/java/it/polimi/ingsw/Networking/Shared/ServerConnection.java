package it.polimi.ingsw.Networking.Shared;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.CustomException.OccupiedTileException;

/**
 * Interface defining the actions a client can perform to interact with the game server.
 * Implementations handle the specifics of the networking protocol (e.g., RMI, Sockets).
 */
public interface ServerConnection {
    /**
     * Establishes a connection to the server.
     */
    void connect();

    /**
     * Closes the connection to the server.
     */
    void disconnect();

    /**
     * Creates a new game lobby.
     * @param playerName the name of the player creating the game.
     * @param numPlayers the number of players for the game.
     */
    void createGame(String playerName, int numPlayers);

    /**
     * Joins an existing game lobby.
     * @param playerName the name of the player joining.
     * @param gameID the ID of the game to join.
     */
    void joinGame(String playerName, int gameID);

    /**
     * Starts the game. Typically only the host can perform this action.
     * @param playerName the name of the player requesting to start.
     * @param gameID the ID of the game to start.
     */
    void startGame(String playerName, int gameID);

    /**
     * Leaves the current game lobby or active game.
     * @param playerName the name of the player leaving.
     * @param gameID the ID of the game to leave.
     */
    void leaveGame(String playerName, int gameID);

    /**
     * Chooses a totem color for the player.
     * @param totemColor the selected Color.
     */
    void chooseTotem(Color totemColor);

    /**
     * Selects an offer tile on the track.
     * @param index the index of the tile to choose.
     * @throws OccupiedTileException if the tile is already taken.
     */
    void chooseOfferTile(int index) throws OccupiedTileException;

    /**
     * Draws a card from the offer track.
     * @param fromTopRow true if drawing from the top row, false otherwise.
     * @param fromBuildings true if drawing a building card, false for character.
     * @param index the index of the card to draw.
     */
    void drawCard(boolean fromTopRow, boolean fromBuildings, int index);

    /**
     * When the player in turn still has remaining draws but has no more character cards to draw, and he doesn't want
     * to draw a building card, he can decide to skip his turn to the next player.
     */
    void passTurn();
    /**
     * Ends the player's current turn.
     * @param playerName the name of the player ending the turn.
     */
    void endTurn(String playerName);
}