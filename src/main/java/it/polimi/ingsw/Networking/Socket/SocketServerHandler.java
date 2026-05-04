package it.polimi.ingsw.Networking.Socket;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Networking.Shared.PlayerRecord;

import java.io.BufferedReader;
import com.google.gson.Gson;

public class SocketServerHandler implements Runnable{
    private final BufferedReader inStream;
    private final Gson gson = new Gson();
    private final VirtualSocketClient client;

    public SocketServerHandler(BufferedReader inStream, VirtualSocketClient client) {
        this.inStream = inStream;
        this.client = client;
    }

    @Override
    public void run() {
        String incomingMessage;
        //while(legge input da stdin){
        //  logica di parsing dei messaggi inviati dal server al client
        //  da qui poi si chiameranno i metodi del client parsando i messaggi
        //}
        try{
            while((incomingMessage = inStream.readLine()) != null){
                SocketMessageDTO incomingCommand = gson.fromJson(incomingMessage, SocketMessageDTO.class);

                switch (incomingCommand.getCommandName()){
                    case "ConnectedToGame":
                        //client.connectedToGame() or something
                    case "ChosenTotemColor":
                    {
                        String playerName = (String) incomingCommand.getParameters()[0];
                        Color totemColor = Color.valueOf((String) incomingCommand.getParameters()[1]) ;
                        client.chosenTotemColor(playerName, totemColor);
                    }
                    case "DrawnCard":
                    {
                        String playerName = (String) incomingCommand.getParameters()[0];
                        boolean fromTopRow = (boolean) incomingCommand.getParameters()[1];
                        boolean fromBuildings = (boolean) incomingCommand.getParameters()[2];
                        int index = (int) incomingCommand.getParameters()[3];
                        client.drawnCard(playerName, fromTopRow, fromBuildings, index);
                    }

                }
            }
        } catch (Exception e){}
    }
}
