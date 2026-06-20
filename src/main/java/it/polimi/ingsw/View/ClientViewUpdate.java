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

/**
 * Interface defining the callbacks used to update the client's View.
 * It contains methods invoked by the controller to push updates,
 * resource modifications, and lobby events to the player's user interface,
 * modifying the local model and the view state.
 */
public interface ClientViewUpdate {
    void updateGameCreated(int gameID, int numPlayers);
    void updateGameStarted(List<String> firstTurnOrder, Map<String,Integer> initialFood, ArrayList<Card> firstTopRow,
                           ArrayList<Card> firstBottomRow, ArrayList<BuildingCard> buildingsTopRow,
                           ArrayList<BuildingCard> buildingsBottomRow);
    void updatePlayerConnected(String playerName);
    void updateSuccessfullyJoinedGame(int gameID, int numPlayers, ArrayList<String> players, Map<String,Color> totemColors) throws RemoteException;
    void updatePlayerLeftGame(String playerName);
    void updateNewHost();
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
