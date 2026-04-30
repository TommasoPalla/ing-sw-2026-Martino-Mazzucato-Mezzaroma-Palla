package it.polimi.ingsw.Networking.Shared;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Users.Player;

import java.rmi.Remote;
import java.rmi.RemoteException;

/*
* Methods offered by the client to the server to update his view or notify errors
 */
//  ^
//  |   intendi da server al client?
//  |
public interface ClientInterface extends Remote {

    public void showUpdate() throws RemoteException;

    public void reportError(String errorMessage) throws RemoteException;

    public void choosenTotem(String playerName, Color totemColor);

    public void choosenTile(int index);

    public void drawnCard(boolean isTopRow, int index);

    public void gameStarted(String gameID, int numPlayers);

    //void giveUp();

}
