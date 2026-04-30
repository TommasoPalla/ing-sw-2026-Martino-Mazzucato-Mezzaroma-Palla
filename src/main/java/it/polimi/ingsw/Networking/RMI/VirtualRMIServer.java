package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.Model.Users.Illegal_Draw_Exception;
import it.polimi.ingsw.Model.Users.Occupied_Tile_Exception;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * this interface has all the methods that need to be called from the client
 * (analogous of VirtualSocketServer)
 */

public interface VirtualRMIServer extends Remote {
    public void connect(VirtualRMIClient clientStub) throws RemoteException;
    public void disconnect(VirtualRMIClient clientStub) throws RemoteException;
    public void chooseOfferTile() throws Occupied_Tile_Exception, RemoteException;
    public void drawCardFromBottom() throws Illegal_Draw_Exception, RemoteException;
    public void drawCardFromTop() throws Illegal_Draw_Exception, RemoteException;
}
