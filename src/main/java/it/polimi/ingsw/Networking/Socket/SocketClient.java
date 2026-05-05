package it.polimi.ingsw.Networking.Socket;

import it.polimi.ingsw.Controller.ClientController;
import it.polimi.ingsw.Enums.Color;

import java.io.IOException;

public class SocketClient implements VirtualSocketClient {
    /*final BufferedReader input;  potrebbe essere necessario un reader diverso: da definire
    final VirtualSocketServer server;   */

    //run(), runCli(), runGUI()
    private final ClientController controller;
    public SocketClient(ClientController controller){
        this.controller = controller;
    }


    @Override
    public void showUpdate() throws IOException {
        System.out.println("state model updated");
    }

    @Override
    public void reportError(String errorMessage) throws IOException {
        System.out.println("[ERROR]: " + errorMessage);
    }

    @Override
    public void chosenTotemColor(String playerName, Color totemColor) {
    }

    @Override
    public void chosenTile(String playerName, int index) {

    }

    @Override
    public void drawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index) {
        controller.updateCardDrawn(fromTopRow, fromBuildings, index, playerName);
    }

    @Override
    public void gameStarted(String gameID, int numPlayers) {

    }

    @Override
    public void updateFood(String playerName, int food) throws IOException {

    }

    @Override
    public void updateShamansStars(String playerName, int stars) throws IOException {

    }

    @Override
    public void updatePrestigePoints(String playerName, int points) throws IOException {

    }
}
