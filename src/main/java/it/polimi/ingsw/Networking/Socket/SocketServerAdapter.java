package it.polimi.ingsw.Networking.Socket;

import it.polimi.ingsw.Model.Users.Illegal_Draw_Exception;
import it.polimi.ingsw.Model.Users.Occupied_Tile_Exception;
import it.polimi.ingsw.Networking.Shared.ServerConnection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketServerAdapter implements ServerConnection {

    private Socket socket;
    private PrintWriter outStream;
    private Thread listenerThread;
    private SocketClient client;

    private final int port;
    private final String host;

    public SocketServerAdapter(String host, int port){
        this.host = host;
        this.port = port;
    }

    @Override
    public void connect() {
        try{
            socket = new Socket(host, port);
            outStream = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            client = new SocketClient();
            SocketServerHandler handler = new SocketServerHandler(inStream, client);
            listenerThread = new Thread(handler);
            listenerThread.setDaemon(true);
            listenerThread.start();

            System.out.println("Connected to TCP server");

        } catch (Exception e){
            System.out.println("Error during connection to TCP server: " + e.getMessage());
        }
    }

    @Override
    public void disconnect() {
        //qui si invia un messaggio che dice al server che si vuole chiudere la connessione
        try {
            socket.close();
            listenerThread.interrupt();
        } catch (IOException e) {
            System.out.println("[ERROR]: " + e.getMessage());
        }
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
