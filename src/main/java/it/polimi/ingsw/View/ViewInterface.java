package it.polimi.ingsw.View;

public interface ViewInterface {
    void runView();
    void notifyNameModified(String newName);
    void notifyNewAvailableGames();
    void notifyGameCreated(int gameID);
    void notifyPlayerJoinedLobby(int gameID, String playerName);
    void notifySuccessfullyJoinedGame(int gameID);
    void notifyPlayerLeftLobby(String playerName, int gameID);
}
