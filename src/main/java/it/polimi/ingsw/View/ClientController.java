package it.polimi.ingsw.View;

import it.polimi.ingsw.Controller.GameController;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.ClientModel;
import it.polimi.ingsw.Model.GameBoard.OfferTile;
import it.polimi.ingsw.Model.Users.Illegal_Draw_Exception;
import it.polimi.ingsw.Model.Users.Occupied_Tile_Exception;
import it.polimi.ingsw.Model.Users.Player;
import it.polimi.ingsw.Networking.Shared.ServerConnection;

import java.util.ArrayList;

public class ClientController implements ClientViewUpdate{
    GameController gameController;
    ServerConnection connection;
    ClientModel localModel;

    public ClientController() {
        this.connection = connection;
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
    public void onChooseOfferTile(int index, String id) {
        if(localModel.isOccupied(index)) throw new Occupied_Tile_Exception();
        gameController.handleChooseOfferTile(player, index, gameController.getGameModel().getOfferTrack());
    }

    public void onChoosenTotemColor(Player player, Color color){
        localModel.chosenTotemColor(player, color);
    }
    @Override
    public void updateCardDrawn(boolean isTopRow, boolean isBuilding, int index, String id){
        if(isTopRow){
            if(isBuilding){
                localModel.getTopRowBuildings().remove(index);
                ArrayList<BuildingCard> arr = localModel.getTopRowBuildings();
                localModel.updateTopRowBuildings(arr);
            }else{
                localModel.getTopRow().remove(index);
                ArrayList<Card> arr = localModel.getTopRow();
                localModel.updateTopRow(arr);
            }
        }else{
            if(isBuilding){
                localModel.getTopRowBuildings().remove(index);
                ArrayList<BuildingCard> arr=localModel.getBottomRowBuildings();
                localModel.updateBottomRowBuildings(arr);
            }else{
                localModel.getBottomRow().remove(index);
                ArrayList<Card> arr=localModel.getBottomRow();
                localModel.updateBottomRow(arr);
            }

        }
    }

    @Override
    public void updateFoodReserve(int food) {

    }

    @Override
    public void updatePrestigePoints(int pp) {

    }

    @Override
    public void updateCurrentOfferTile(OfferTile offerTile) {

    }

    @Override
    public void updateTopRow(ArrayList<Card> newTopRow) {

    }

    @Override
    public void updateBottomRow(ArrayList<Card> newBottomRow) {

    }

    @Override
    public void updateCurrentPlayer(Player nextPlayer) {

    }
    @Override
    public void updateCurrentRound() {

    }
}
