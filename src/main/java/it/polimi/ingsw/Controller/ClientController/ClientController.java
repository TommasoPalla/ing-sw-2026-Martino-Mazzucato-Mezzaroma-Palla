package it.polimi.ingsw.Controller.ClientController;

import it.polimi.ingsw.CustomException.IllegalDrawException;
import it.polimi.ingsw.CustomException.InsufficientFoodException;
import it.polimi.ingsw.CustomException.UIException.InvalidSelectionException;
import it.polimi.ingsw.CustomException.UIException.IllegalActionPhaseException;
import it.polimi.ingsw.CustomException.OccupiedTileException;
import it.polimi.ingsw.CustomException.UnavailableColorException;
import it.polimi.ingsw.Enums.ClientState;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Networking.Shared.ServerConnection;
import it.polimi.ingsw.View.ClientViewUpdate;
import it.polimi.ingsw.View.ViewInterface;
import it.polimi.ingsw.View.GamePlayers;

import java.util.ArrayList;
import java.util.Map;


public class ClientController implements ClientViewUpdate {
    private String playerName;
    private ServerConnection connection;
    private ClientModel localModel;
    private ViewInterface view;
    /**
     * The attribute representing the state of the client, deciding which actions
     * they can perform in that state
     */
    private ClientState clientState;

    public ClientController() {
        clientState = ClientState.SETUP;
    }
    public String getPlayerName(){  //servirà da qualche parte
        return playerName;
    }
    public ServerConnection getConnection(){    //servirà da qualche parte?
        return connection;
    }
    public ClientModel getLocalModel() {
        return localModel;
    }
    public ViewInterface getView(){
        return view;
    }

    /*no constructor defined, default constructor is used,
    then setPlayerName, onGameStarted, bindConnection, bindView
     methods are invoked to initialize private fields
    * */
    public void bindConnection(ServerConnection connection){
        this.connection = connection;
    }
    public void bindView(ViewInterface view){
        this.view = view;
    }

    public void setPlayerName(String playerName) {
        if(localModel.checkNameAvailable(playerName)){
            //da gestire bene con TUI e GUI
            System.out.println("Name not valid");
        } else {
            connection.setPlayerName(playerName);
            this.playerName = playerName;
            localModel.addPlayer(playerName);
        }
    }

    /*called when server responds with a successful 'startGame' request by first player
    or next players join the game via 'joinGame' method
    actually called by onGameStarted()*/
    public void createLocalModel(int gameId, int num){
        this.localModel = new ClientModel(gameId, num);
    }

    /**
     * This method forwards the request through the network to the Server Controller, which will add the
     * game to the list of active games.
     * @param numPlayers the number of players chosen by the client who creates the game.
     */
    public void createGame(int numPlayers){
        //con partite multiple non serve verificare che ci sia una partita già inizializzata
        if (clientState != ClientState.SETUP) {
            throw new IllegalActionPhaseException();
        }
        if(numPlayers < 2 || numPlayers > 5){
            throw new IllegalArgumentException();
        }
        connection.createGame(playerName, numPlayers);
        clientState = ClientState.IN_LOBBY;
        //da notificare il player della creazione del game in modo che stampi le possibili azioni da fare mentre in lobby
    }

    public Map<Integer, GamePlayers> getActiveGames(){
        if(clientState != ClientState.SETUP){
            throw new IllegalActionPhaseException();
        }
        return connection.getActiveGames();
    }

    public void joinGame(String playerName, int gameID){
        if(clientState != ClientState.SETUP) {
            throw new IllegalActionPhaseException();
        }
        connection.joinGame(playerName, gameID);
    }
    /*decidere se cancellare il model dopo l'effettivo abbandono,
     per la resilienza potrebbe servire conservarlo per tot tempo dopo la disconnessione
     */
    public void leaveGame(){
        connection.leaveGame(playerName, localModel.getGameId());
    }

    /*methods used for client's requests,
    ClientController checks if localModel allows them and then send to server,
    identified by connection field (RMI/socket)*/
    public void chooseTotem(Color color){
        if (clientState != ClientState.IN_LOBBY) {
            throw new IllegalActionPhaseException();
        }
        try {
            if (localModel.isColorAvailable(color)) throw new IllegalArgumentException();
            connection.chooseTotem(color);
        }catch (UnavailableColorException e){
            throw new UnavailableColorException(color);
        }
    }

    public void chooseOfferTile(int index) {
        try {
            if (localModel.isOccupied(index)) throw new OccupiedTileException();
            connection.chooseOfferTile(index);
        }catch(OccupiedTileException e){
            throw new OccupiedTileException();
        }
    }

    /*GESTIONE EXCEPTION ESEMPIO!
    il drawable può lanciare InsufficientFood o IllegalDraw
    che viene catchato qui perché lasciarlo arrivare alla view (che è un altro thread)
    causerebbe la morte del thread che si occupa di questa classe.
    Lancia una nuova eccezione già formattata in un formato user-friendly per la view
    con messaggio personalizzato e 'cause', ovvero l'eccezione originale.
    Questa informazione non viene persa e può essere usata per stampare informazioni aggiuntive
    o per mantenere informazioni di log.
    * */
    public void drawCard(boolean fromTopRow, boolean fromBuildings, int index){
        if(clientState != ClientState.DRAW_CARD) {
            //throw new IllegalActionPhaseException();
            //TODO:forse temrina il thread, controllo dopo

        }
        if(localModel.getCurrentPlayer().equals(playerName)){
            try {
                localModel.drawable(fromTopRow, fromBuildings, index);
                connection.drawCard(fromTopRow, fromBuildings, index);
            } catch (InsufficientFoodException | IllegalDrawException e){
                throw new InvalidSelectionException("Can't draw this card: ", e);
            }
        } else {
            throw new IllegalActionPhaseException();
        }
    }

    /*da definire: pensavo che il giocatore può mettere in pausa con un timer
    che scade in automatico. (volevo fare che se tutti sono d'accordo il game
    viene sospeso a tempo indefinito ma lasciamo stare)*/
    public void pauseGame(){
        //mostra schermata di pausa (dentro la view)
        //fa partire il timer
        //manda l'info al server per notificare gli altri player
    }

    public void endTurn(){
        if(localModel.getCurrentPlayer().equals(playerName)) {
            connection.endTurn(playerName);
        } else {
            throw new IllegalActionPhaseException();
        }
    }

    //TODO: siamo sicuri che vada fatto cosi?
    @Override
    public void addPlayer(String id) {
        if(!localModel.checkNameAvailable(id)){
            localModel.addPlayer(id);
        }
        else {
            //da gestire TUI o GUI
            System.out.println("Name already taken");
        }
    }

    /*ClientViewUpdate interface override: update methods called by RMI/socket Client
    when an update is sent by the server.
    Valutare se aggiungere per ogni metodo lo show() di TUI o GUI (secondo me si),
    eventualmente aggiungere un attributo alla classe che dice quale
    interfaccia è stata scelta.
     */

    //-----------------CALLBACKS----------------------

    /** Before making a call to the game controller methods, the client controller checks
     * if the player's draw is legal by checking the client light model
     */

    @Override
    public void updateCurrentRound(int round) {
        localModel.updateCurrentRound(round);
    }

    @Override
    public void updateCardDrawn(boolean fromTopRow, boolean fromBuilding, int index, String playerName){
        if(fromTopRow){
            if(fromBuilding){
                BuildingCard drawn = localModel.getTopBuildings().remove(index);
                localModel.getPlayerTribe(playerName).addToBuildings(drawn);
            }else{
                CharacterCard drawn = (CharacterCard) localModel.getTopRow().remove(index);
                localModel.getPlayerTribe(playerName).addToPopulation(drawn);
            }
        }else{
            if(fromBuilding){
                BuildingCard drawn = localModel.getBottomBuildings().remove(index);
                localModel.getPlayerTribe(playerName).addToBuildings(drawn);
            }else{
                CharacterCard drawn = (CharacterCard) localModel.getBottomRow().remove(index);
                localModel.getPlayerTribe(playerName).addToPopulation(drawn);
            }
        }
    }

    @Override
    public void updateCurrentOfferTile(String playerName, Character index) {
        localModel.updateOfferTile(playerName, index);
    }


    public void removePlayer(String name){
        localModel.removePlayer(name);
    }

    @Override
    public void updateFoodReserve(String playerName, int food) {
        localModel.updateFoodReserve(playerName, food);
    }

    @Override
    public void updateShamansStars(String playerName, int stars) {
        localModel.updateShamansStars(playerName, stars);
    }

    @Override
    public void updatePrestigePoints(String playerName, int pp) {
        localModel.updatePrestigePoints(playerName, pp);
    }

    @Override
    public void updateTopRow(ArrayList<Card> newTopRow) {
        localModel.updateTopRow(newTopRow);
    }

    @Override
    public void updateTopBuildings(ArrayList<BuildingCard> newTopBuildings) {
        localModel.updateTopRowBuildings(newTopBuildings);
    }

    @Override
    public void updateBottomRow(ArrayList<Card> newBottomRow) {
        localModel.updateBottomRow(newBottomRow);
    }

    @Override
    public void updateBottomBuildings(ArrayList<BuildingCard> newBottomBuildings) {
        localModel.updateBottomRowBuildings(newBottomBuildings);
    }

    @Override
    public void updateCurrentPlayer(String playerName) {
        localModel.setNextPlayer(playerName);
    }

    @Override
    public void updateGamePhase(GamePhase phase) {
        localModel.updateGamePhase(phase);
    }

    @Override
    public void updateCurrentEra(int era) {
        localModel.updateEra(era);
    }

}