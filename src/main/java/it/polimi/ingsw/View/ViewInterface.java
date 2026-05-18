package it.polimi.ingsw.View;

import it.polimi.ingsw.Enums.ClientState;
import it.polimi.ingsw.Enums.Color;

import java.util.ArrayList;
import java.util.Map;

public interface ViewInterface {
    void runView();
    void notifyNameModified(String newName);
    void notifyNewAvailableGames();
    void notifyGameCreated(int gameID) ;
    void notifyGameStarted();
    void notifyPlayerJoinedLobby(String playerName);
    void notifySuccessfullyJoinedGame(int gameID, ArrayList<String> playerNames, Map<String, Color> totemColors);
    void notifyPlayerLeftLobby(String playerName);
    void notifyChosenTotemColor(String playerName, Color totemColor);
    void notifyGiveInitialFood(Map<String,Integer> initialFood);
    void notifyTileChosen(String playerName, int index);
    void notifyNewCurrentPlayer(String playerName, ClientState clientState);
}
