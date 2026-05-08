package it.polimi.ingsw.View;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;

import java.util.ArrayList;

public interface ClientViewUpdate {
    void updateCurrentRound(int round);

    void updateGameCreated(int gameID, int numPlayers);
    //void updateGameStarted();
    void updatePlayerConnected(String playerName);
    void updateCardDrawn(boolean isTopRow, boolean isBuilding, int index, String id);
    void updateTotemColor(String playerName, Color totemColor);
    void updateCurrentOfferTile(String playerName, Character offerTileIndex);

    void updateFoodReserve(String playerName, int food);
    void updateShamansStars(String playerName, int stars);
    void updatePrestigePoints(String playerName, int pp);
    void updateTopRow(ArrayList<Card> newTopRow);
    void updateTopBuildings(ArrayList<BuildingCard> newTopBuildings);
    void updateBottomRow(ArrayList<Card> newBottomRow);
    void updateBottomBuildings(ArrayList<BuildingCard> newBottomBuildings);
    void updateCurrentPlayer(String playerName);
    void updateGamePhase(GamePhase phase);
    void updateCurrentEra(int era);
}
