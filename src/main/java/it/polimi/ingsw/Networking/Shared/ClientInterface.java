package it.polimi.ingsw.Networking.Shared;

import java.rmi.Remote;
import java.rmi.RemoteException;

/*
* Methods offered by the client to the server to update his view or notify errors
 */
public interface ClientInterface extends Remote {
    void showUpdate() throws RemoteException;
    void reportError(String errorMessage) throws RemoteException;
}
