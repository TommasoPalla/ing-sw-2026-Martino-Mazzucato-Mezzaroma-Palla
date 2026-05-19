package it.polimi.ingsw.View.Listeners;

import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.GameBoard.OfferTile;

public interface Listener {
    void notifyNameModified(String newName);
    void notifyTurnChange(String player);
    void notifyGameEvent();
    void notifyCardDrawn(String player, Card card, boolean topRow);
}
