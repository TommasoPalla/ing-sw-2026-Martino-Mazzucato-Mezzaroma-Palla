package it.polimi.ingsw.Controller;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.ClientModel;
import it.polimi.ingsw.Model.Users.Occupied_Tile_Exception;
import it.polimi.ingsw.Networking.Shared.ServerConnection;
import it.polimi.ingsw.View.ClientViewUpdate;
import it.polimi.ingsw.View.ClientViewCommands;

import java.util.ArrayList;

public class ClientController implements ClientViewUpdate, ClientViewCommands {
    private String playerName;
    private ServerConnection connection;
    private ClientModel localModel;

    /*no constructor defined, default constructor is used,
    then setPlayerName, onGameStarted , bindConnection are used to initialize private fields
    * */
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }
    public ClientModel getLocalModel() {
        return localModel;
    }

    public void createLocalModel(String gameId, int num){
        this.localModel = new ClientModel(gameId, num);
    }
    /* Before making a call to the game controller methods, the client controller checks
    if the player's draw is legal by checking the client light model
     */
    public void bindConnection(ServerConnection connection){
        this.connection = connection;
    }

    /*ClientViewCommands interface override:methods used for client's requests,
     ClientController checks if localModel allows them and then send to server,
     identified by connection field (RMI/socket)*/
    @Override
    public void chooseTotem(Color color){
        if(localModel.isColorAvailable(color)) throw new IllegalArgumentException();
        connection.chooseTotem(color);
    }

    @Override
    public void chooseOfferTile(int index) {
        if(localModel.isOccupied(index)) throw new Occupied_Tile_Exception();
        connection.chooseOfferTile(index);
    }


    /*da definire: pensavo che il giocatore può mettere in pausa con un timer
    che scade in automatico. (volevo fare che se tutti sono d'accordo il game
    viene sospeso a tempo indefinito ma lasciamo stare)*/
    @Override
    public void pauseGame(){
        //mostra schermata di pausa (dentro la view)
        //fa partire il timer
        //manda l'info al server per notificare gli altri player
    }

    /*ClientViewUpdate interface override: update methods called by RMI/socket Client
    when an update is sent by the server.
    Valutare se aggiungere per ogni metodo lo show() di TUI o GUI (secondo me si),
    eventualmente aggiungere un attributo alla classe che dice quale
    interfaccia è stata scelta.
    */
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