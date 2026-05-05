package it.polimi.ingsw.Networking.Socket;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Users.IllegalDrawException;
import it.polimi.ingsw.Model.Users.OccupiedTileException;

/**
 * this interface has all the methods that need to be called from the client
 * (analogous of VirtualRMIServer)
 */

public interface VirtualSocketServer {
    void connect(SocketClientHandler handler);
    void disconnect(SocketClientHandler handler);
    void chooseOfferTile() throws OccupiedTileException;
    void drawCard(boolean fromTopRow, boolean fromBuildings, int index, SocketClientHandler clientHandler) throws IllegalDrawException;
    void chooseTotemColor(Color totemColor, SocketClientHandler clientHandler);
    void joinGame(SocketClientHandler clientHandler);
    void createGame(String playerName, int numPlayers);
}
