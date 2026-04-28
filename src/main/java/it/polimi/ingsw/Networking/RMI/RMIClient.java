package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Users.Player;
import it.polimi.ingsw.Networking.Shared.ClientInterface;
import it.polimi.ingsw.View.ClientController;

import java.rmi.RemoteException;

public class RMIClient implements VirtualRMIClient, ClientInterface {

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
        // call to client controller update methods
        System.out.println("state model updated");
    }

    @Override
    public void reportError(String errorMessage) throws RemoteException {
        System.out.println(errorMessage);
    }

    @Override
    public void gameStarted(String gameID, int numPlayers) {
        controller.onGameStarted(gameID, numPlayers);
    }
    @Override
    public void choosenTotem(Player player, Color totemColor){
        controller.onChoosenTotemColor(player, totemColor);
    }
    @Override
    public void choosenTile(int index){}
    @Override
    public void drawnCard(boolean isTopRow, int index){}

    //bozza di messaggio di update dal server
    public void updateMessage(){
        controller.updateCurrentRound();
    }

}
