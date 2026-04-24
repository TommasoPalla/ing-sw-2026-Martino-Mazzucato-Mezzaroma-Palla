package it.polimi.ingsw.Networking.RMI;


import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Networking.Shared.ClientInterface;
import it.polimi.ingsw.Networking.Shared.ServerInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIClient extends UnicastRemoteObject implements ClientInterface {
    final ServerInterface server;
    public RMIClient(ServerInterface server) throws RemoteException {
        this.server = server;//rmi server
    }
    public void run() throws RemoteException{}
    public void runCli() throws RemoteException{}
    public void runGUI() throws RemoteException{}
    public void showUpdate() throws RemoteException{

    }
    public void reportError(String errorMessage) throws RemoteException{

    }
    public void choosenTotem(Color totemColor){}
    public void choosenTile(int index){}
    public void drawnCard(boolean isTopRow, int index){}



}
