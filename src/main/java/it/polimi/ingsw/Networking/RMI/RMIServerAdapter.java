package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.Model.Users.Illegal_Draw_Exception;
import it.polimi.ingsw.Model.Users.Occupied_Tile_Exception;
import it.polimi.ingsw.Networking.Shared.ServerConnection;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RMIServerAdapter implements ServerConnection {
    private VirtualRMIClient clientStub;
    private VirtualRMIServer serverStub;
    private final RMIClient client;

    private final String host;
    private final int port;

    public RMIServerAdapter(String host, int port){
        this.host = host;
        this.port = port;
        this.client = new RMIClient();
    }

    @Override
    public void connect() {
        try {
            System.setProperty("java.rmi.server.hostname", "127.0.0.1");    //forces the server to use 127.0.0.1 as localhost
            Registry registry = LocateRegistry.getRegistry(host, port);
            serverStub = (VirtualRMIServer) registry.lookup("MesosServer");
            clientStub = (VirtualRMIClient) UnicastRemoteObject.exportObject(client, 0);
            serverStub.connect(clientStub);
        } catch (RemoteException e){
            System.out.println("Error during connection to RMI server\n" + e.getMessage());
        } catch (NotBoundException e){
            System.out.println("Error during RMI server lookup\n" + e.getMessage());
        }

    }

    @Override
    public void disconnect() {
        serverStub.disconnect(clientStub);
    }

    @Override
    public void chooseOfferTile() throws Occupied_Tile_Exception {

    }

    @Override
    public void drawCardFromTop() throws Illegal_Draw_Exception {

    }

    @Override
    public void drawCardFromBottom() throws Illegal_Draw_Exception {

    }

}
