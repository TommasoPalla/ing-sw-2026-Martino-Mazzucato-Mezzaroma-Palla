package it.polimi.ingsw.View;

import it.polimi.ingsw.Model.GameBoard.OfferTile;
import it.polimi.ingsw.Model.Users.Player;

public interface ClientViewUpdate {
    public void updateFoodReserve(Player player, int food);
    public void updatePrestigePoints(Player player, int pp);
    public void updateCurrentOfferTile(Player player, OfferTile offerTile);
}
