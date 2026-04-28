package it.polimi.ingsw.View;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.GameBoard.OfferTile;
import it.polimi.ingsw.Model.Users.Player;

import java.util.ArrayList;

public interface ClientViewUpdate {
    public void updateFoodReserve(int food);
    public void updatePrestigePoints(int pp);
    public void updateCurrentOfferTile(OfferTile offerTile);
    public void updateCardDrawn(boolean isTopRow, boolean isBuilding, int index, String id);
    public void updateTopRow(ArrayList<Card> newTopRow);
    public void updateBottomRow(ArrayList<Card> newBottomRow);
    public void updateCurrentPlayer(Player nextPlayer);
    public void updateCurrentRound();
    public void updateChosenOfferTile(String playerName, Color color);
}
