package it.polimi.ingsw.Controller.ClientController;

import it.polimi.ingsw.CustomException.*;
import it.polimi.ingsw.CustomException.UIException.*;
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
import java.util.HashMap;
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
    private Map<Integer, GamePlayers> availableGames;

    public ClientController() {
        clientState = ClientState.CONNECTING;
        availableGames = new HashMap<>();
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
    public ClientState getClientState(){
        return clientState;
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
    /*called when server responds with a successful 'startGame' request by first player
    or next players join the game via 'joinGame' method
    actually called by onGameStarted()*/
    public void createLocalModel(int gameId, int numPlayers){
        this.localModel = new ClientModel(gameId, numPlayers);
    }
    public void setClientState(ClientState clientState){
        this.clientState = clientState;
    }

    public void setPlayerName(String playerName) {
        if (clientState != ClientState.SETUP && clientState != ClientState.CONNECTING) {
            throw new IllegalClientStateActionException("ERROR: You can no longer change your name!");
        }
        this.playerName = playerName;
        connection.setPlayerName(playerName);
    }

    @Override
    public void updateCurrentRound(int round) {
        localModel.updateCurrentRound(round);
    }

    //TODO: siamo sicuri che vada fatto cosi?

    //-----------------METHODS CALLED FROM PLAYERS' ACTIONS-----------------------------
    /**
     * This method forwards the request through the network to the Server Controller, which will add the
     * game to the list of active games.
     * @param numPlayers the number of players chosen by the client who creates the game.
     */
    public void createGame(int numPlayers){
        //con partite multiple non serve verificare che ci sia una partita già inizializzata
        if (clientState != ClientState.SETUP) {
            throw new IllegalClientStateActionException("ERROR: You cannot do that right now!");
        }
        if(numPlayers < 2 || numPlayers > 5){
            throw new IllegalArgumentException();
        }
        connection.createGame(playerName, numPlayers);
        clientState = ClientState.IN_LOBBY;
        //da notificare il player della creazione del game in modo che stampi le possibili azioni da fare mentre in lobby
    }

    public Map<Integer, GamePlayers> getAvailableGames(){
        if(clientState != ClientState.SETUP){
            throw new IllegalActionPhaseException();
        }
        return availableGames;
    }

    public void joinGame(String playerName, int gameID){
        try {
            connection.joinGame(playerName, gameID);
        } catch (NotJoinableGameException e) {
            throw new NotJoinableGameException(e.getMessage());
        }
    }
    /*decidere se cancellare il model dopo l'effettivo abbandono,
     per la resilienza potrebbe servire conservarlo per tot tempo dopo la disconnessione
     */
    public void leaveGame(){
        if(clientState == ClientState.SETUP){
            throw new IllegalClientStateActionException("ERROR: You can't leave a game if you're not in one!");
        }
        if(clientState != ClientState.IN_LOBBY){
            throw new IllegalClientStateActionException("ERROR: You can't leave the game now!");
        }
        connection.leaveGame(playerName, localModel.getGameId());
    }

    /**
     * This method asks the server to start an existing game. It must be run by the host player.
     * @param player the player host.
     * @throws NotTheHostException Throw an exception if the player is not the host.
     * @throws NotEnoughPlayersException Throws an exception if tried to start the game with
     * an insufficient number of players.
     */
    public void startGame(String player) throws NotTheHostException, NotEnoughPlayersException{
        if(clientState != ClientState.IN_LOBBY){
            throw new IllegalClientStateActionException("ERROR: You cannot start a game if you're not in one!");
        }
        try {
            connection.startGame(player, localModel.getGameId());
        } catch (NotTheHostException e) {
            throw new NotTheHostException(e.getMessage());
        } catch (NotEnoughPlayersException e) {
            throw new NotEnoughPlayersException();
        }
    }

    /*methods used for client's requests,
    ClientController checks if localModel allows them and then send to server,
    identified by connection field (RMI/socket)*/
    public void chooseTotemColor(Color color){
        if (clientState != ClientState.IN_LOBBY) {
            throw new IllegalClientStateActionException("ERROR: You cannot choose a totem right now.");
        }
        if (localModel.getTotemColors().containsKey(playerName)) {
            throw new AlreadyChosenTotemException();
        }
        try {
            //if (!localModel.isColorAvailable(color)) throw new IllegalArgumentException(); CONTROLLO VA FATTO DAL SERVER
            connection.chooseTotem(color);
        }catch (UnavailableColorException e){
            throw new UnavailableColorException(color);
        }
    }

    public void chooseOfferTile(int index) {
        if (clientState == ClientState.DRAW_CARD) {
            throw new IllegalActionPhaseException();
        }
        if (clientState == ClientState.NOT_IN_TURN) {
            throw new IllegalClientStateActionException("ERROR: Wait for your turn!");
        }
        if (clientState != ClientState.PLACE_TOTEM) {
            throw new IllegalClientStateActionException("Invalid command, please try again...");
        }
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

    /*ClientViewUpdate interface override: update methods called by RMI/socket Client
    when an update is sent by the server.
    Valutare se aggiungere per ogni metodo lo show() di TUI o GUI (secondo me si),
    eventualmente aggiungere un attributo alla classe che dice quale
    interfaccia è stata scelta.
     */

    //-----------------CALLBACKS FROM SERVER UPDATES----------------------

    /** Before making a call to the game controller methods, the client controller checks
     * if the player's draw is legal by checking the client light model
     */
    public void updateAvailableGames(Map<Integer, GamePlayers> availableGames){
      this.availableGames = availableGames;
      if(clientState == ClientState.SETUP){
            view.notifyNewAvailableGames();
        }
    }

    @Override
    public void updateNameModified(String newName) {
        view.notifyNameModified(newName);
    }

    @Override
    public void updateGameCreated(int gameID, int numPlayers){
        createLocalModel(gameID, numPlayers);
        this.clientState = ClientState.IN_LOBBY;
        view.notifyGameCreated(gameID);
        //TODO: !!!! capire cosa ci va qui, questo e' il metodo che viene chiamato dal server per dire
        // "oh fra guarda che ho creato il game che mi hai chiesto di creare" !!!!
    }

    /**
     * When a new player enters the lobby, the client controller updates his local model's list of players.
     * @param player the player who entered the lobby.
     */
    @Override
    public void updatePlayerConnected(String player) {
//        localModel.addPlayer(player);
        view.notifyPlayerJoinedLobby(player); // sbagliato, serve mandargli in ingresso il game modificato
    }

    @Override
    public void updateSuccessfullyJoinedGame(int gameID, int numPlayers, ArrayList<String> players) {
        createLocalModel(gameID, numPlayers);
//        for (String player : players) {
//            localModel.addPlayer(player);
//        }
        clientState = ClientState.IN_LOBBY;
        view.notifySuccessfullyJoinedGame(gameID);
    }

    @Override
    public void updatePlayerLeftGame(String player) {
        //TODO da aggiungere controllo del gameID
        if(this.playerName.equals(player)){
            clientState = ClientState.SETUP;
            view.notifyPlayerLeftLobby(player);
            localModel = null;
        }
        else {
            localModel.removePlayer(player);
            view.notifyPlayerLeftLobby(player);
        }
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

    /**
     * The client controller updates the local model adding the choise of the totem color by the player.
     * @param playerName the player who chose the totem color
     * @param totemColor the totem color chosen.
     */
    @Override
    public void updateTotemColor(String playerName, Color totemColor) {
        localModel.chosenTotemColor(playerName, totemColor);
        view.notifyChosenTotemColor(playerName, totemColor);
    }

    @Override
    public void updateCurrentOfferTile(String playerName, int index) {
        localModel.chosenOfferTile(playerName, index);
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