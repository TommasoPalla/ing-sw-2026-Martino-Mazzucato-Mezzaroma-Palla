package it.polimi.ingsw.Networking.Socket;

import it.polimi.ingsw.Enums.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClientHandler implements VirtualSocketClient, Runnable {
    //one instance for each client to be handled by Server: Virtual Client
    private final BufferedReader inStream;
    private final PrintWriter outStream;
    private final VirtualSocketServer server;

    public SocketClientHandler(Socket socket, VirtualSocketServer server) throws IOException {
        this.inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.outStream = new PrintWriter(socket.getOutputStream(), true);
        this.server = server;
    }

    @Override
    public void run() {
        server.connect(this);
        //while(legge input da tastiera){
        //  logica di parsing dei messaggi inviati dal client al server
        //  da qui poi si chiameranno i metodi di server parsando i messaggi
        //}
    }

    @Override
    public void showUpdate() throws IOException {
        outStream.println("Model state updated");
    }

    @Override
    public void reportError(String errorMessage) throws IOException {
        outStream.println("[ERROR]: " + errorMessage);
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
