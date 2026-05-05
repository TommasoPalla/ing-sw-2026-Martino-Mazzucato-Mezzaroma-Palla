package it.polimi.ingsw.Networking.Socket;

import it.polimi.ingsw.Enums.Color;

import java.io.IOException;

public interface VirtualSocketClient {
    void showUpdate() throws IOException;
    void reportError(String errorMessage) throws IOException;
    void updateChosenTotemColor(String playerName, Color totemColor) throws  IOException;
    void updateChosenTile(String playerName, int index) throws  IOException;
    void updateDrawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index) throws  IOException;
    void updateGameStarted(String gameID, int numPlayers) throws  IOException;
    void updateFood(String playerName, int food) throws IOException;
    void updateShamansStars(String playerName, int stars) throws IOException;
    void updatePrestigePoints(String playerName, int points) throws IOException;
}
