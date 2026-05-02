package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.Enums.Color;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualRMIClient extends Remote {
    void showUpdate() throws RemoteException;
    void reportError(String errorMessage) throws RemoteException;
    void chosenTotem(String playerName, Color totemColor) throws RemoteException;
    void chosenTile(String playerName, int index) throws RemoteException;
    void drawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index) throws RemoteException;
    void gameStarted(String gameID, int numPlayers) throws RemoteException;
}
