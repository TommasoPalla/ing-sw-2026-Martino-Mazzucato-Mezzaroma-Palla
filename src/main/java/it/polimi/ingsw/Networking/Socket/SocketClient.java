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
    public void updateChosenTotemColor(String playerName, Color totemColor) {
    }

    @Override
    public void updateChosenTile(String playerName, int index) {
        controller.updateChosenOfferTile(playerName, index);
    }

    @Override
    public void updateDrawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index) {
        controller.updateCardDrawn(fromTopRow, fromBuildings, index, playerName);
    }

    @Override
    public void updateGameStarted(String gameID, int numPlayers) {

    }

    @Override
    public void updateFood(String playerName, int food) throws IOException {
        controller.updateFoodReserve(playerName, food);
    }

    @Override
    public void updateShamansStars(String playerName, int stars) throws IOException {
        controller.updateShamansStars(playerName, stars);
    }

    @Override
    public void updatePrestigePoints(String playerName, int points) throws IOException {
        controller.updatePrestigePoints(playerName, points);
    }
}
