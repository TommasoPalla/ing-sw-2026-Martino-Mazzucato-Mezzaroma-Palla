package it.polimi.ingsw.View;

import it.polimi.ingsw.Enums.ClientState;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.Card;

import java.util.ArrayList;
import java.util.Map;

/**
 * These methods are common to both views types (GUI and TUI). It contains methods used to notify the view about changes
 * to the status of the game.
 */
public interface ViewInterface {
    void runView();
    void showNameSet(String newName);
    void showNewAvailableGames();
    void showGameCreated(int gameID);
    void showPlayerJoinedLobby(String playerName);
    void showSuccessfullyJoinedGame(int gameID, ArrayList<String> playerNames, Map<String, Color> totemColors);
    void showPlayerLeftLobby(String playerName, Color oldColor);
    void showChosenTotemColor(String playerName, Color totemColor);
    void showForceQuit(String disconnectedPlayerName, Color oldColor);
    void showNewHost();
    void showGameStarted();

    void showStartRound(int round);
    void showCardDrawn(String player, Card card, boolean topRow, boolean fromBuildings);
    void showEraChanged(int era);

    //todo in gui
    void showTurnPassed(String playerThatPassed, String newCurrentPlayer);
    void showInitialFood(Map<String,Integer> initialFood);
    void showTileChosen(String playerName, int index);
    void showFoodBonusTile(String playerName, int foodBonus);
    void showNewCurrentPlayer(String playerName, ClientState clientState);
    void showNewGamePhase(GamePhase newGamePhase);

    void showEvent(String playerName, EventType eventType, int foodModified, int ppModified);
    void showFoodModified(String playerName, int deltaFood, int finalFood);
    void showPrestigePointsModified(String playerName, int deltaPP, int finalPP);

    //int parameter represents new absolute value of this attribute, not delta
    void showShamanStarsModified(String playerName, int stars);
    void showBuildersDiscountModified(String playerName, int discount);
    void showGatherersDiscountModified(String playerName, int discount);

    void showTotemToTurnTile(String playerName, int index);
    //todo in gui
    void showEndGame(Map<String, Integer> finalRanking);
    void showLeaderboardInfo(int playerPosition);
    void showEndGameLeft();
}