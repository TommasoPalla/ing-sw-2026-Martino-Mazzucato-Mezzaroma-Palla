package it.polimi.ingsw.Networking.Socket;

import com.google.gson.Gson;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Networking.Shared.ClientNotifier;
import it.polimi.ingsw.Networking.Shared.PlayerRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * This class is located server-side and accepts methods invocation requests
 * via JSON messages
 */

public class SocketClientHandler implements ClientNotifier, Runnable {
    private final Gson gson = new Gson();
    private final BufferedReader inStream;
    private final PrintWriter outStream;
    private final VirtualSocketServer server;
    private PlayerRecord playerRecord;

    public SocketClientHandler(Socket socket, VirtualSocketServer server) throws IOException {
        this.inStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.outStream = new PrintWriter(socket.getOutputStream(), true);
        this.server = server;
    }

    /**
     * This run() method is the loop that accept messages from the client and
     * forwards them to the socket (TCP) server. It checks which function needs
     * to be called (written in the JSON message) and calls it with the parameters
     * contained in the same JSON message.
     */
    @Override
    public void run() {
        server.connect(this);
        try{
            String incomingMessage;
            //while(client connesso){
            //  logica di parsing dei messaggi inviati dal client al server
            //  da qui poi si chiameranno i metodi di server parsando i messaggi
            //}
            while((incomingMessage = inStream.readLine()) != null){
                SocketMessageDTO incomingCommand = gson.fromJson(incomingMessage, SocketMessageDTO.class);

                switch (incomingCommand.getCommandName()){
                    case "ConnectToGame":
                        String playerName = (String) incomingCommand.getParameters()[0];
                        int gameID = (int) incomingCommand.getParameters()[1];
                        this.playerRecord = new PlayerRecord(gameID, playerName);
                        server.joinGame(this);
                        break;
                    case "ChooseTotemColor":
                        Color totemColor = Color.valueOf((String) incomingCommand.getParameters()[0]) ;
                        server.chooseTotemColor(totemColor, this);
                        break;
                    case "DrawCard":
                        boolean fromTopRow = (boolean) incomingCommand.getParameters()[0];
                        boolean fromBuildings = (boolean) incomingCommand.getParameters()[1];
                        int index = (int) incomingCommand.getParameters()[2];
                        server.drawCard(fromTopRow, fromBuildings, index, this);
                        break;
                }


            }

        } catch (Exception e){}
    }

    public PlayerRecord getPlayerRecord(){return playerRecord;}

    //CALLBACKS
    @Override
    public void notifyDrawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index) {
        SocketMessageDTO message = new SocketMessageDTO("DrawnCard", playerName, fromTopRow, fromBuildings, index);
        outStream.println(gson.toJson(message));
    }

    @Override
    public void notifyTotemColor(String playerName, Color totemColor) {
        SocketMessageDTO message = new SocketMessageDTO("ChosenTotemColor", playerName, totemColor.toString());
        outStream.println(gson.toJson(message));
    }
}
