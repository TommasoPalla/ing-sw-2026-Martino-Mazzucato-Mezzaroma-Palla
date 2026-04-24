package it.polimi.ingsw.Networking.Shared;

import it.polimi.ingsw.Enums.Color;

import java.rmi.Remote;
import java.rmi.RemoteException;

/*
* Methods offered by the client to the server to update his view or notify errors
 */
//  ^
//  |   intendi da server al client?
//  |
public interface ClientInterface extends Remote {
    void showUpdate() throws RemoteException;
    void reportError(String errorMessage) throws RemoteException;

    void choosenTotem(Color totemColor);

    void choosenTile(int index);

    void drawnCard(boolean isTopRow, int index);

    //void giveUp();

}
