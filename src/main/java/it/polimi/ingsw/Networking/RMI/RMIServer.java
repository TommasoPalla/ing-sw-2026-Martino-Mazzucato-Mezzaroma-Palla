package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.Model.Users.Illegal_Draw_Exception;
import it.polimi.ingsw.Model.Users.Occupied_Tile_Exception;
import it.polimi.ingsw.Networking.Shared.ServerConnection;
import it.polimi.ingsw.Networking.Shared.ServerController;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

public class RMIServer implements VirtualRMIServer {
    final ServerController serverController;
    final List<VirtualRMIClient> clients = new ArrayList<>();

    public RMIServer(ServerController serverController){
        this.serverController = serverController;
    }

    public void startServer(){
        try{
            VirtualRMIServer serverStub = (VirtualRMIServer) UnicastRemoteObject.exportObject(this, 0);
            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("MesosServer", serverStub);
        } catch (RemoteException e){
            System.out.println("Error during RMI server initialization\n" + e.getMessage());
        }
    }

    @Override
    public void connect(VirtualRMIClient clientStub) {
        this.clients.add(clientStub);
        System.out.println(clientStub + "added to RMI server");
    }

    @Override
    public void disconnect(VirtualRMIClient clientStub) {
        this.clients.remove(clientStub);
        System.out.println(clientStub + "removed from RMI server");
    }

    @Override
    public void chooseOfferTile() throws Occupied_Tile_Exception {

    }

    @Override
    public void drawCardFromBottom() throws Illegal_Draw_Exception {

    }

    @Override
    public void drawCardFromTop() throws Illegal_Draw_Exception {

    }
}
