package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Users.IllegalDrawException;
import it.polimi.ingsw.Model.Users.OccupiedTileException;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * this interface has all the methods that need to be called from the client
 * (analogous of VirtualSocketServer)
 */

public interface VirtualRMIServer extends Remote {
    void connect(VirtualRMIClient clientStub) throws RemoteException;
    void disconnect(VirtualRMIClient clientStub) throws RemoteException;
    void chooseOfferTile(VirtualRMIClient client, int index) throws OccupiedTileException, RemoteException;
    void drawCard(VirtualRMIClient client, boolean fromTopRow, boolean fromBuildings, int index) throws IllegalDrawException, RemoteException;
    void chooseTotemColor(VirtualRMIClient client, Color totemColor) throws  RemoteException;
    void joinGame(VirtualRMIClient client, String playerName, int gameID) throws  RemoteException;
    void createGame(String playerName, int numPlayers) throws RemoteException;
}
