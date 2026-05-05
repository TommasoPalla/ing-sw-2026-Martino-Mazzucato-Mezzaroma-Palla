package it.polimi.ingsw.View;

import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.Card;

import java.util.ArrayList;

public interface ClientViewUpdate {
    public void updateFoodReserve(String playerName, int food);
    public void updatePrestigePoints(String playerName, int pp);
    public void updateCurrentOfferTile(String playerName, int offerTileIndex);
    public void updateCardDrawn(boolean isTopRow, boolean isBuilding, int index, String id);
    public void updateTopRow(ArrayList<Card> newTopRow);
    public void updateBottomRow(ArrayList<Card> newBottomRow);
    public void updateCurrentPlayer(String playerName);
    public void updateCurrentRound(int round);
    public void updateChosenOfferTile(String playerName, int index);
    public void addPlayer(String playerName);
    public void updateCurrentEra(int era);
    public void updateShamansStars(String playerName, int stars);
    public void updateGamePhase(GamePhase phase);
}
