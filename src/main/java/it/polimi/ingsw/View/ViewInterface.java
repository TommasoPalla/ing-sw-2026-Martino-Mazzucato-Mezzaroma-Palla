package it.polimi.ingsw.View;

import it.polimi.ingsw.Enums.ClientState;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.Card;

import java.util.ArrayList;
import java.util.Map;

public interface ViewInterface {
    void runView();
    void notifyNameModified(String newName);
    void notifyNewAvailableGames();
    void notifyGameCreated(int gameID);
    void notifyGameStarted();
    void notifyPlayerJoinedLobby(String playerName);
    void notifySuccessfullyJoinedGame(int gameID, ArrayList<String> playerNames, Map<String, Color> totemColors);
    void notifyPlayerLeftLobby(String playerName);

    void notifyStartRound(int round);
    void notifyChosenTotemColor(String playerName, Color totemColor);
    void notifyCardDrawn(String player, Card card, boolean topRow);
    void notifyGiveInitialFood(Map<String,Integer> initialFood);
    void notifyTileChosen(String playerName, int index);
    void notifyNewCurrentPlayer(String playerName, ClientState clientState);
    void notifyNewGamePhase(GamePhase newGamePhase);
}
