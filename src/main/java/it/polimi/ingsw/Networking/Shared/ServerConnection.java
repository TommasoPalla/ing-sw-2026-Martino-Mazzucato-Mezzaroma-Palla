package it.polimi.ingsw.Networking.Shared;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Users.OccupiedTileException;

/**
 * This interface contains all the actions that the client can do.
 * These methods don't need player authentication, it will be handled
 * down the line, by the respective server. These methods are called
 * by the client, no matter what the networking protocol is.
 */
public interface ServerConnection {
    void connect();
    void disconnect();
    void setPlayerName(String playerName);
    void createGame(String playerName, int numPlayers);
    void joinGame(String playerName, int gameID);
    void leaveGame(String playerName, int gameID);
    void getActiveGames();
    void chooseTotem(Color totemColor);
    void chooseOfferTile(int index) throws OccupiedTileException;
    void drawCard(boolean fromTopRow, boolean fromBuildings, int index);
    void endTurn(String playerName);
}
