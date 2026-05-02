package it.polimi.ingsw.Networking.Shared;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Users.Illegal_Draw_Exception;
import it.polimi.ingsw.Model.Users.Occupied_Tile_Exception;

import java.io.IOException;

/**
 * This interface contains all the actions that the client can do.
 * These methods don't need player authentication, it will be handled
 * down the line, by the respective server. These methods are called
 * by the client, no matter what the networking protocol is.
 */
public interface ServerConnection {
    public void connect();
    public void disconnect();
    public void setPlayerName(String playerName);
    public void startGame(int numPlayers);
    public void joinGame(String gameId);
    public void getActiveGames();
    public void chooseTotem(Color totemColor);
    public void chooseOfferTile(int index) throws Occupied_Tile_Exception;
    public void drawCard(boolean fromTopRow, boolean fromBuildings, int index);
}
