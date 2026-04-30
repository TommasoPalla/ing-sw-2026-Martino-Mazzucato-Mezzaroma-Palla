package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.ClientModel;
import it.polimi.ingsw.Model.GameBoard.OfferTile;
import it.polimi.ingsw.Model.Users.Occupied_Tile_Exception;
import it.polimi.ingsw.Model.Users.Player;
import it.polimi.ingsw.Networking.Shared.ServerConnection;
import it.polimi.ingsw.View.ClientViewUpdate;

import java.util.ArrayList;

public class ClientController implements ClientViewUpdate {
    String name;
    ServerConnection connection;
    ClientModel localModel;

    public ClientController() {
    }

    public void setPlayerName(String playerName) {
        this.name = playerName;
    }
    public ClientModel getLocalModel() {
        return localModel;
    }

    public void onGameStarted(String gameId, int num){
        this.localModel=new ClientModel(gameId, num);
    }
    /* Before making a call to the game controller methods, the client controller checks
    if the player's draw is legal by checking the client light model
     */
    public void bindConnection(ServerConnection connection){
        this.connection = connection;
    }
    public void onChooseOfferTile(int index, String playerName) {
        if(localModel.isOccupied(index)) throw new Occupied_Tile_Exception();
        connection.chooseOfferTile(index);
    }

    public void onChoosenTotemColor(String playerName, Color color){

    }

    @Override
    public void updateCardDrawn(boolean fromTopRow, boolean fromBuilding, int index, String id){
        if(fromTopRow){
            if(fromBuilding){
                localModel.getTopRowBuildings().remove(index);
                ArrayList<BuildingCard> arr = localModel.getTopRowBuildings();
                localModel.updateTopRowBuildings(arr);
            }else{
                localModel.getTopRow().remove(index);
                ArrayList<Card> arr = localModel.getTopRow();
                localModel.updateTopRow(arr);
            }
        }else{
            if(fromBuilding){
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
    public void addPlayer(String id) {
        localModel.addPlayer(id);
    }

    @Override
    public void updateChosenOfferTile(String playerName, Color color) {
        localModel.chosenTotemColor(playerName, color);
    }

    @Override
    public void updateFoodReserve(String playerName, int food) {
        localModel.updateFoodReserve(playerName, food);
    }

    @Override
    public void updatePrestigePoints(String playerName, int pp) {
        localModel.updatePrestigePoints(playerName, pp);
    }

    @Override
    public void updateCurrentOfferTile(String playerName, int index) {
        localModel.updateOfferTile(playerName, index);
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
    public void updateCurrentPlayer(String playerName) {
        localModel.setNextPlayer(playerName);

    }

    @Override
    public void updateCurrentRound(int round) {
        localModel.updateCurrentRound(round);
    }

    @Override
    public void updateCurrentEra(int era) {
        localModel.updateEra(era);
    }

    @Override
    public void updateShamansStars(String playerName, int stars) {
        localModel.updateShamansStars(playerName, stars);
    }

    @Override
    public void updateGamePhase(GamePhase phase) {
        localModel.updateGamePhase(phase);
    }
}
