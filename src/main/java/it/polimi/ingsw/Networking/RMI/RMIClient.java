package it.polimi.ingsw.Networking.RMI;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIClient implements VirtualRMIClient {
    /*public class RMIClient extends UnicastRemoteObject implements VirtualRMIClient {
        final ServerConnection server;
        public RMIClient(ServerConnection server) throws RemoteException {
            this.server = server;
        }
         */
    public void run() throws RemoteException{}
    public void runCli() throws RemoteException{}
    public void runGUI() throws RemoteException{}

    @Override
    //verosimilmente si dovra' passare per parametro il model e poi chiamare System.out.println(model.toString()) o simile
    public void showUpdate() throws RemoteException {
        System.out.println("state model updated");
    }

    @Override
    public void reportError(String errorMessage) throws RemoteException {
        System.out.println(errorMessage);
    }
    public void choosenTotem(Color totemColor){}
    public void choosenTile(int index){}
    public void drawnCard(boolean isTopRow, int index){}

}
