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
    void showNameSet(String newName);
    void showNewAvailableGames();
    void showGameCreated(int gameID);
    void showGameStarted();
    void showPlayerJoinedLobby(String playerName);
    void showSuccessfullyJoinedGame(int gameID, ArrayList<String> playerNames, Map<String, Color> totemColors);
    void showPlayerLeftLobby(String playerName, Color oldColor);
    void showForceQuit(String disconnectedPlayerName, Color oldColor);
    void showPlayerDisconnected(String playerName, boolean hadColor);
    void showNewHost();

    void showStartRound(int round);
    void showChosenTotemColor(String playerName, Color totemColor);
    void showCardDrawn(String player, Card card, boolean topRow, boolean fromBuildings);
    void showEraChanged(int era);
    void showTurnPassed(String playerThatPassed, String newCurrentPlayer);
    void showInitialFood(Map<String,Integer> initialFood);
    void showTileChosen(String playerName, int index);
    void showFoodBonusTile(String playerName, int foodBonus);
    void showNewCurrentPlayer(String playerName, ClientState clientState);
    void showNewGamePhase(GamePhase newGamePhase);
    void showFoodModified(String playerName, int food);
    void showPrestigePointsModified(String playerName, int pp);
    void showEvent(EventType eventType, int foodModified, int ppModified);
    void showEndGame(Map<String, Integer> finalRanking);
    void showLeaderboardInfo(int playerPosition);
}