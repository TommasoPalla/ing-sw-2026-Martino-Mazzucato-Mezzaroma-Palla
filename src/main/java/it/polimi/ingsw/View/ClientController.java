package it.polimi.ingsw.View;

import it.polimi.ingsw.Controller.GameController;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Cards.Visitor;
import it.polimi.ingsw.Model.ClientModel;
import it.polimi.ingsw.Model.Users.Illegal_Draw_Exception;
import it.polimi.ingsw.Model.Users.Occupied_Tile_Exception;
import it.polimi.ingsw.Model.Users.Player;
import it.polimi.ingsw.Model.VisitorModel;
import it.polimi.ingsw.Networking.Shared.ServerConnection;

public class ClientController implements ClientViewUpdate{
    GameController gameController;
    ServerConnection connection;
    ClientModel localModel;

    public ClientController() {
        //this.gameController = gameController;

    }

    public void onGameStarted(String gameId, int num){
        this.localModel=new ClientModel(gameId, num);
    }
    /*
    * Before making a call to the game controller methods, the client controller checks
    * if the player's draw is legal by checking the client light model
     */

    public void bindConnection(ServerConnection connection){
        this.connection = connection;
    }

    public void onDrawFromTopRow(int index, String id) {
        if(!localModel.drawable(index, 0)) throw new Illegal_Draw_Exception();
        gameController.handleDrawFromTopRow(player, index, gameController.getGameModel().getOfferTrack());
    }

    public void onDrawFromBottomRowBuilding(int index, String id) {
        Visitor visitor = new VisitorModel(id);
        localModel.getBottomRowBuildings().get(index).accept(visitor, localModel);
        if(!localModel.drawable(index, 1)) throw new Illegal_Draw_Exception();
        gameController.handleDrawFromBottomRow(player, index, gameController.getGameModel().getOfferTrack());
    }

    public void onChooseOfferTile(int index, String id) {
        if(localModel.isOccupied(index)) throw new Occupied_Tile_Exception();
        gameController.handleChooseOfferTile(player, index, gameController.getGameModel().getOfferTrack());
    }

    public void onChoosenTotemColor(Player player, Color color){
        localModel.chosenTotemColor(player, color);
    }

    @Override
    public void updateCurrentRound() {

    }
}
