package it.polimi.ingsw.Networking.RMI;


import it.polimi.ingsw.Networking.Shared.ClientInterface;
import it.polimi.ingsw.Networking.Shared.ServerInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIClient extends UnicastRemoteObject implements ClientInterface {
    final ServerInterface server;
    public RMIClient(ServerInterface server) throws RemoteException {
        this.server = server;
    }
    public void run() throws RemoteException{}
    public void runCli() throws RemoteException{}
    public void runGUI() throws RemoteException{}
}
