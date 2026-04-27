package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Users.Player;
import it.polimi.ingsw.Networking.Shared.ClientInterface;
import it.polimi.ingsw.View.ClientController;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class RMIClient implements VirtualRMIClient, ClientInterface {
    /*public class RMIClient extends UnicastRemoteObject implements VirtualRMIClient {
        final ServerConnection server;
        public RMIClient(ServerConnection server) throws RemoteException {
            this.server = server;
        }
         */

    private final ClientController controller;
    public RMIClient(ClientController controller) {
        this.controller = controller;
    }

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

    @Override
    public void gameStarted(String gameID, int numPlayers) {
        controller.onGameStarted(gameId, numPlayers);
    }
    @Override
    public void choosenTotem(Player player, Color totemColor){
        controller.onChoosenTotemColor(player, totemColor);
    }
    @Override
    public void choosenTile(int index){}
    @Override
    public void drawnCard(boolean isTopRow, int index){}

}
