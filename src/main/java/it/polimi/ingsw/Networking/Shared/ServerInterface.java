package it.polimi.ingsw.Networking.Shared;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Users.Illegal_Draw_Exception;
import it.polimi.ingsw.Model.Users.Occupied_Tile_Exception;

import java.io.IOException;

/*
 * Interface offered by the server to the client
 */

//client chiede cose al server
public interface ServerInterface {
    //void connect(ClientInterface client) throws RemoteException;

    public void connect(String nickName);
    public void disconnect(String nickName);
    public void chooseTotem(Color totemColor);
    public void chooseOfferTile(int index) throws Occupied_Tile_Exception;
    public void drawCardFromTop(int index) throws Illegal_Draw_Exception;
    public void drawCardFromBottom(int index) throws Illegal_Draw_Exception;
}
//tutti e due i server useranno questa interfaccia overridandola