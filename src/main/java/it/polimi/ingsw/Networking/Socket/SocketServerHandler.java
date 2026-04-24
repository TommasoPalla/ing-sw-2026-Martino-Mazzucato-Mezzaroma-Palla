package it.polimi.ingsw.Networking.Socket;

import java.io.BufferedReader;

public class SocketServerHandler implements Runnable{
    private final BufferedReader inStream;
    private final VirtualSocketClient client;

    public SocketServerHandler(BufferedReader inStream, VirtualSocketClient client) {
        this.inStream = inStream;
        this.client = client;
    }

    @Override
    public void run() {
        //while(legge input da stdin){
        //  logica di parsing dei messaggi inviati dal server al client
        //  da qui poi si chiameranno i metodi del client parsando i messaggi
        //}
    }
}
