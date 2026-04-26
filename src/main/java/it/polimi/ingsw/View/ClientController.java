package it.polimi.ingsw.View;

import it.polimi.ingsw.Controller.GameController;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.ClientModel;
import it.polimi.ingsw.Model.GameBoard.OfferTile;
import it.polimi.ingsw.Model.Users.Illegal_Draw_Exception;
import it.polimi.ingsw.Model.Users.Occupied_Tile_Exception;
import it.polimi.ingsw.Model.Users.Player;
import it.polimi.ingsw.Networking.Shared.ServerConnection;

import java.util.ArrayList;

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

    /**
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
    public void updateFoodReserve(int food) {
        localModel.updateFoodReserve(food);
    }

    @Override
    public void updatePrestigePoints(int pp) {
        localModel.updatePrestigePoints(pp);
    }

    @Override
    public void updateCurrentOfferTile(OfferTile offerTile) {
        localModel.updateCurrentOfferTile(offerTile);
    }

    @Override
    public void updateTopRow(ArrayList<Card> newTopRow) {
        localModel.updateTopRow(newTopRow);
    }

    @Override
    public void updateBottomRow(ArrayList<Card> newBottomRow) {
        localModel.updateBottomRow(newBottomRow);
    }

    @Override
    public void updateCardDrawn(CharacterCard card) {
        localModel.updateCardDrawn(card);
    }
}
