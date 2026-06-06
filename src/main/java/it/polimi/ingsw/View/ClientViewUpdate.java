package it.polimi.ingsw.View;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.EventManagement.PlayerEventResults;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface ClientViewUpdate {
    void updateGameCreated(int gameID, int numPlayers);
    void updateGameStarted(List<String> firstTurnOrder, Map<String,Integer> initialFood, ArrayList<Card> firstTopRow,
                           ArrayList<Card> firstBottomRow, ArrayList<BuildingCard> buildingsTopRow,
                           ArrayList<BuildingCard> buildingsBottomRow);
    void updatePlayerConnected(String playerName);
    void updateSuccessfullyJoinedGame(int gameID, int numPlayers, ArrayList<String> players, Map<String,Color> totemColors) throws RemoteException;
    void updatePlayerLeftGame(String playerName);
    void updateNewHost();
    //void updatePlayerDisconnected(String playerName);
    void updateCardDrawn(boolean isTopRow, boolean isBuilding, int index, String id);
    void updateTurnPassed(String playerName);
    void updateTotemColor(String playerName, Color totemColor);

    void updateStartRound(Map<EventType, ArrayList<PlayerEventResults>> lastEventsResults, ArrayList<Card> newTopRow,
                          ArrayList<Card> newBottomRow, ArrayList<BuildingCard> newTopBuildings,
                          ArrayList<BuildingCard> newBottomBuildings);
    void updateCurrentOfferTile(String playerName, int index);
    void updateFoodReserve(String playerName, int food);
    void updateShamansStars(String playerName, int stars);
    void updatePrestigePoints(String playerName, int pp);
    void updateBuildersDiscount(String playerName, int discount);
    void updateGatherersDiscount(String playerName, int discount);
    void updateGamePhase(GamePhase phase);
    void updateCurrentEra(int era);
    void updateEndGame(Map<String, Integer> finalRanking);
    void updateLeaderboardInfo(List<String> leaderboard, int playerPosition);
}
