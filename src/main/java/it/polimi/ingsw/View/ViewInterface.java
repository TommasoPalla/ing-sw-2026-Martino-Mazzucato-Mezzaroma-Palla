package it.polimi.ingsw.View;

import it.polimi.ingsw.Enums.ClientState;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.Card;

import java.util.ArrayList;
import java.util.Map;

public interface ViewInterface {
    void runView();
    void notifyNameSet(String newName);
    void notifyNewAvailableGames();
    void notifyGameCreated(int gameID);
    void notifyGameStarted();
    void notifyPlayerJoinedLobby(String playerName);
    void notifySuccessfullyJoinedGame(int gameID, ArrayList<String> playerNames, Map<String, Color> totemColors);
    void notifyPlayerLeftLobby(String playerName, Color oldColor);
    void notifyForceQuit(String disconnectedPlayerName, Color oldColor);
    void notifyPlayerDisconnected(String playerName,  boolean hadColor);
    void notifyNewHost();

    void notifyStartRound(int round);
    void notifyChosenTotemColor(String playerName, Color totemColor);
    void notifyCardDrawn(String player, Card card, boolean topRow, boolean fromBuildings);
    void notifyGiveInitialFood(Map<String,Integer> initialFood);
    void notifyTileChosen(String playerName, int index);
    void notifyNewCurrentPlayer(String playerName, ClientState clientState);
    void notifyNewGamePhase(GamePhase newGamePhase);
    void notifyFood(String playerName, int food);
    void notifyPrestigePoints(String playerName, int pp);
    void notifyEvent(EventType eventType, int foodModified, int ppModified);
}
