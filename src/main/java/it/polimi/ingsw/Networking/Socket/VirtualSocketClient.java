package it.polimi.ingsw.Networking.Socket;

import it.polimi.ingsw.Enums.Color;

import java.io.IOException;

public interface VirtualSocketClient {
    void showUpdate() throws IOException;
    void reportError(String errorMessage) throws IOException;
    public void choosenTotem(String playerName, Color totemColor);
    public void choosenTile(int index);
    public void drawnCard(boolean isTopRow, int index);
    public void gameStarted(String gameID, int numPlayers);
}
