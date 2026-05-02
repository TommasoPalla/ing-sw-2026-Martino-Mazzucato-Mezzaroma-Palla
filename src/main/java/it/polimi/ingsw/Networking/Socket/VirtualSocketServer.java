package it.polimi.ingsw.Networking.Socket;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Users.Illegal_Draw_Exception;
import it.polimi.ingsw.Model.Users.Occupied_Tile_Exception;

/**
 * this interface has all the methods that need to be called from the client
 * (analogous of VirtualRMIServer)
 */

public interface VirtualSocketServer {
    void connect(SocketClientHandler handler);
    void disconnect(SocketClientHandler handler);
    public void chooseOfferTile() throws Occupied_Tile_Exception;
    public void drawCard(boolean fromTopRow, boolean fromBuildings, int index, SocketClientHandler clientHandler) throws Illegal_Draw_Exception;
    public void chooseTotemColor(Color totemColor, SocketClientHandler clientHandler);
}
