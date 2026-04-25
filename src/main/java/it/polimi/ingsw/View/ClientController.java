package it.polimi.ingsw.View;

import it.polimi.ingsw.Controller.GameController;
import it.polimi.ingsw.Model.ClientModel;
import it.polimi.ingsw.Model.GameBoard.OfferTile;
import it.polimi.ingsw.Model.Users.Illegal_Draw_Exception;
import it.polimi.ingsw.Model.Users.Occupied_Tile_Exception;
import it.polimi.ingsw.Model.Users.Player;
import it.polimi.ingsw.Networking.Shared.ServerConnection;

public class ClientController implements ClientViewUpdate {
    private Player player;
    GameController gameController;
    ServerConnection connection;
    ClientModel localModel;

    public ClientController(GameController gameController, Player player) {
        //this.connection = connection;
        this.player = player;
        this.gameController = gameController;

    }

    /*
    * Before making a call to the game controller methods, the client controller checks
    * if the player's draw is legal by checking the client light model
     */
    public void onDrawFromTopRow(int index) {
        if(!localModel.drawable(index, 0)) throw new Illegal_Draw_Exception();
        gameController.handleDrawFromTopRow(player, index, gameController.getGameModel().getOfferTrack());
    }

    public void onDrawFromBottomRow(int index) {
        if(!localModel.drawable(index, 1)) throw new Illegal_Draw_Exception();
        gameController.handleDrawFromBottomRow(player, index, gameController.getGameModel().getOfferTrack());
    }

    public void onChooseOfferTile(int index) {
        if(localModel.isOccupied(index)) throw new Occupied_Tile_Exception();
        gameController.handleChooseOfferTile(player, index, gameController.getGameModel().getOfferTrack());
    }

    @Override
    public void updateFoodReserve(Player updatedPlayer, int food) {
        if(updatedPlayer == player) localModel.updateFoodReserve(food);
        else localModel.updateOtherFoodReserves(updatedPlayer, food);
    }

    @Override
    public void updatePrestigePoints(Player updatedPlayer, int pp) {
        if(updatedPlayer == player) localModel.updatePrestigePoints(pp);
        else localModel.updateOtherPrestigePoints(updatedPlayer, pp);
    }

    @Override
    public void updateCurrentOfferTile(Player updatedPlayer, OfferTile offerTile) {
        if(updatedPlayer == player) localModel.updateCurrentOfferTile(offerTile);
        else localModel.updateOtherOfferTile(player, offerTile);
    }
}
