package it.polimi.ingsw.Networking.Shared;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;

import java.util.ArrayList;

public interface ClientNotifier {
    //void showUpdate() throws IOException;
    //void reportError(String errorMessage) throws IOException;

    void notifyGameCreated(int gamID, int playerNum);
    //void notifyGameStarted();
    void notifyNewPlayerConnected(String playerName);
    void notifySuccessfullyJoinedGame(int gameID, int playerNum, ArrayList<String> players);
    void notifyPlayerLeftGame(String playerName);
    void notifyTotemColor(String playerName, Color totemColor);
    void notifyDrawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index);
    void notifyChosenTile(String playerName, int index);

    void notifyFoodToAdd(String playerName, int food);
    void notifyShamansStarsToAdd(String playerName, int food);
    void notifyPrestigePointsToAdd(String playerName, int food);
    void notifyTopRow(ArrayList<Card> newTopRow);
    void notifyTopBuildings(ArrayList<BuildingCard> newTopBuildings);
    void notifyBottomRow(ArrayList<Card> newBottomRow);
    void notifyBottomBuildings(ArrayList<BuildingCard> newBottomBuildings);
    void notifyNextPlayer(String playerName);
    void notifyGamePhase(GamePhase newPhase);
    void notifyEra(int era);
}