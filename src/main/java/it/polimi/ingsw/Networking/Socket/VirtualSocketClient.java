package it.polimi.ingsw.Networking.Socket;

import it.polimi.ingsw.Enums.Color;

import java.io.IOException;

public interface VirtualSocketClient {
    void showUpdate() throws IOException;
    void reportError(String errorMessage) throws IOException;
    public void choosenTotem(String playerName, Color totemColor) throws  IOException;
    public void choosenTile(String playerName, int index) throws  IOException;
    public void drawnCard(String playerName, boolean isTopRow, int index) throws  IOException;
    public void gameStarted(String gameID, int numPlayers) throws  IOException;
}
