package it.polimi.ingsw.View;

import it.polimi.ingsw.Enums.Color;

/**
 * @deprecated è inutile se usata solo dal ClientController, da cancellare
 */
public interface ClientViewCommands {
    public void chooseOfferTile(int index);
    public void chooseTotem(Color color);
    public void pauseGame();
}
