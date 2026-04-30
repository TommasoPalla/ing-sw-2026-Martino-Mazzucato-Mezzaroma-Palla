package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.Enums.Color;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualRMIClient extends Remote {
    public void showUpdate() throws RemoteException;
    public void reportError(String errorMessage) throws RemoteException;
    public void choosenTotem(String playerName, Color totemColor) throws RemoteException;
    public void choosenTile(String playerName, int index) throws RemoteException;
    public void drawnCard(String playerName, boolean isTopRow, int index) throws RemoteException;
    public void gameStarted(String gameID, int numPlayers) throws RemoteException;

}
