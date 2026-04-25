package it.polimi.ingsw.Networking.RMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualRMIClient extends Remote {
    public void showUpdate() throws RemoteException;
    public void reportError(String errorMessage) throws RemoteException;
}
