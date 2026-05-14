package it.polimi.ingsw.Networking.Socket;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.View.GamePlayers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface VirtualSocketClient {
    void showUpdate() throws IOException;
    void reportError(String errorMessage) throws IOException;

    void updateGameCreated(int gameID, int numPlayers);
    void updateGameStarted(List<String> firstTurnOrder) throws  IOException;
    void updatePlayerConnected(String playerName) throws IOException;
    void successfullyJoinedGame(int gameID, int playerNum, ArrayList<String> playerNames) throws IOException;
    void updatePlayerLeftGame(String playerName) throws IOException;
    void updateAvailableGames(Map<Integer, GamePlayers> availableGames) throws IOException;
    void updateChosenTotemColor(String playerName, Color totemColor) throws  IOException;
    void updateDrawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index) throws  IOException;
    void updateChosenTile(String playerName, int index) throws  IOException;

    void updateFood(String playerName, int food) throws IOException;
    void updateShamansStars(String playerName, int stars) throws IOException;
    void updatePrestigePoints(String playerName, int points) throws IOException;
    void updateTopRow(ArrayList<Card> newTopRow);
    void updateTopBuildings(ArrayList<BuildingCard> newTopBuildings);
    void updateBottomRow(ArrayList<Card> newBottomRow);
    void updateBottomBuildings(ArrayList<BuildingCard> newBottomBuildings);
    void updateNextPlayer(String playerName);
    void updateGamePhase(GamePhase phase);
    void updateEra(int era);
}
