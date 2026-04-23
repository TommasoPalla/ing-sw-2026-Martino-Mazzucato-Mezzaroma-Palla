package it.polimi.ingsw.Networking.Shared;

import it.polimi.ingsw.Model.Users.Illegal_Draw_Exception;
import it.polimi.ingsw.Model.Users.Occupied_Tile_Exception;

import java.io.IOException;

/*
 * Interface offered by the server to the client
 */
public interface ServerInterface {
    //void connect(ClientInterface client) throws RemoteException;

    public void connect();
    public void disconnect();
    public void chooseOfferTile() throws Occupied_Tile_Exception;
    public void drawCardFromTop() throws Illegal_Draw_Exception;
    public void drawCardFromBottom() throws Illegal_Draw_Exception;
}
