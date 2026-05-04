package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Networking.Shared.ClientNotifier;

import java.io.IOException;
import java.rmi.RemoteException;

public class RMIClientNotifier implements ClientNotifier {
    private final VirtualRMIClient clientStub;
    RMIClientNotifier(VirtualRMIClient stub) {this.clientStub = stub;}

    @Override
    public void notifyNewPlayerConnected(String playerName) {
        try {
            clientStub.playerJoinedGame(playerName);
        } catch (RemoteException e){
            System.out.println("ERROR: could not notify of new player joining\n" + e.getMessage());
        }
    }

    @Override
    public void notifyDrawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index) {
        try {
            clientStub.drawnCard(playerName, fromTopRow, fromBuildings, index);
        } catch (RemoteException e){
            System.out.println("ERROR: could not notify of card drawn choice\n" + e.getMessage());
        }
    }

    @Override
    public void notifyTotemColor(String playerName, Color totemColor) {
        try {
            clientStub.chosenTotem(playerName, totemColor);
        } catch (RemoteException e) {
            System.out.println("ERROR: could not notify of Totem Color choice\n" + e.getMessage());
        }

    }
}
