package it.polimi.ingsw.Networking.Socket;

import it.polimi.ingsw.Enums.Color;

import java.io.IOException;
import java.rmi.RemoteException;

public interface VirtualSocketClient {
    void showUpdate() throws IOException;
    void reportError(String errorMessage) throws IOException;
    void chosenTotemColor(String playerName, Color totemColor) throws  IOException;
    void chosenTile(String playerName, int index) throws  IOException;
    void drawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index) throws  IOException;
    void gameStarted(String gameID, int numPlayers) throws  IOException;
    void updateFood(String playerName, int food) throws IOException;
    void updateShamansStars(String playerName, int stars) throws IOException;
    void updatePrestigePoints(String playerName, int points) throws IOException;
}
