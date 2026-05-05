package it.polimi.ingsw.Networking.Shared;

import it.polimi.ingsw.Enums.Color;

import java.io.IOException;

public interface ClientNotifier {
    /*
    void showUpdate() throws IOException;
    void reportError(String errorMessage) throws IOException;
    void chosenTile(String playerName, int index) throws  IOException;
    void gameStarted(String gameID, int numPlayers) throws  IOException;
     */
    void notifyTotemColor(String playerName, Color totemColor);
    void notifyDrawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index);
    void notifyNewPlayerConnected(String playerName);
    void notifyChosenTile(String playerName, int index);
    void notifyFoodToAdd(String playerName, int food);
    void notifyShamansStarsToAdd(String playerName, int food);
    void notifyPrestigePointsToAdd(String playerName, int food);
}
