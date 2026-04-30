package it.polimi.ingsw.Networking.Socket;

import it.polimi.ingsw.Enums.Color;

import java.io.IOException;

public class SocketClient implements VirtualSocketClient {
    /*final BufferedReader input;  potrebbe essere necessario un reader diverso: da definire
    final VirtualSocketServer server;   */

    //run(), runCli(), runGUI()


    @Override
    public void showUpdate() throws IOException {
        System.out.println("state model updated");
    }

    @Override
    public void reportError(String errorMessage) throws IOException {
        System.out.println("[ERROR]: " + errorMessage);
    }

    @Override
    public void choosenTotem(String playerName, Color totemColor) {

    }

    @Override
    public void choosenTile(int index) {

    }

    @Override
    public void drawnCard(boolean isTopRow, int index) {

    }

    @Override
    public void gameStarted(String gameID, int numPlayers) {

    }
}
