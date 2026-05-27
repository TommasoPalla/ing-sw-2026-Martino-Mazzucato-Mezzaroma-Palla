package it.polimi.ingsw.Model.Game;

import java.util.*;
import java.util.stream.Collectors;

import it.polimi.ingsw.CustomException.*;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Deck.Deck;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Model.Users.*;
import it.polimi.ingsw.Model.GameBoard.OfferTrack;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.EventManagement.EventManager;

public class Game {
    private boolean readyToStart;   //set to true when all players joined
    private boolean isStarted;
    private final int gameID;
    private final int numPlayers;
    private final ArrayList<Player> players;
    private Player currentPlayer;
    private final Map<String, Color> totemColors;
    private int era;
    private int currentRound;
    private GamePhase currentPhase;
    private OfferTrack offerTrack;
    private BuildingManager buildingManager;
    private final EventManager eventManager;
    private Deck deck;
    private static final String jsonCardsPath = "json/cards.json";


    public Game(int gameID, int numPlayers) {
        readyToStart = false;
        isStarted = false;
        this.gameID = gameID;
        this.numPlayers = numPlayers;
        this.players = new ArrayList<>();
        eventManager = new EventManager();
        totemColors = new HashMap<>();
    }

    //getters
    public boolean isReadyToStart(){
        return readyToStart;
    }
    public boolean isStarted(){
        return isStarted;
    }
    public int getGameID(){
        return gameID;
    }
    public int getNumPlayer(){return numPlayers;}
    public Player getCurrentPlayer(){
        if(isStarted){
            return currentPlayer;
        }
        throw new GameNotStartedException();
    }
    public ArrayList<String> getPlayersNames(){
        ArrayList<String> names = new ArrayList<>();
        for (Player p: players){
            names.add(p.getName());
        }
        return names;
    }
    public ArrayList<Player> getPlayers(){
        return players;
    }
    public Player getPlayerByName(String playerName){
        return players.stream().filter(p -> p.getName().equals(playerName))
                .findFirst().orElse(null);
    }
    public Set<Color> getAvailableColors(){
        Set<Color> colors = EnumSet.allOf(Color.class);
        for(Color color: totemColors.values()){
            colors.remove(color);
        }
        return colors;
    }

    public Map<String, Color> getPlayersTotemColors(){
        return totemColors;
    }
    public int getEra(){
        if(isStarted){
            return era;
        }
        throw new GameNotStartedException();
    }
    public int getCurrentRound(){
        if(isStarted){
            return currentRound;
        }
        throw new GameNotStartedException();
    }
    public GamePhase getGamePhase(){
        if(isStarted){
            return currentPhase;
        }
        throw new GameNotStartedException();
    }
    public OfferTrack getOfferTrack(){
        if(isStarted){
            return offerTrack;
        }
        throw new GameNotStartedException();
    }
    public BuildingManager getBuildingManager(){
        if(isStarted){
            return buildingManager;
        }
        throw new GameNotStartedException();
    }
    public EventManager getEventManager(){return eventManager;}
    public Deck getDeck() {
        if(isStarted){
            return deck;
        }
        throw new GameNotStartedException();
    }

    //mainly used for tests
    public void setEra(int era){
        this.era = era;
    }

    public void setReadyToStart(boolean readyToStart){
        this.readyToStart = readyToStart;
    }

    public void setCurrentPhase(GamePhase phase){
        currentPhase = phase;
    }

    //Next methods are used for update game data, according to player's actions
    public void chooseTotemColor(String playerName, Color totemColor){
        totemColors.put(playerName, totemColor);
    }

    public void addPlayer(String playerName) {
        Player newPlayer = new Player(this, playerName, totemColors.get(playerName));
        players.add(newPlayer);
    }

    //used for testing
    public void startGameUnshuffled(){
        isStarted = true;
        this.era = 1;
        this.currentRound = 0;
        this.currentPhase = GamePhase.START_GAME;
        this.deck = new Deck(this, jsonCardsPath);
        this.buildingManager = new BuildingManager(players);
        this.offerTrack = new OfferTrack(this, numPlayers);
        offerTrack.getTurnTile().initTurnOrderUnshuffled(players);
        giveInitialFood(numPlayers);
    }

    public Map<String,Integer> startGame(){
        isStarted = true;
        this.era = 1;
        this.currentRound = 0;
        this.currentPhase = GamePhase.START_GAME;
        this.deck = new Deck(this, jsonCardsPath);
        this.buildingManager = new BuildingManager(players);
        this.offerTrack = new OfferTrack(this, numPlayers);
        offerTrack.getTurnTile().initTurnOrder(players);
        return new HashMap<>(giveInitialFood(numPlayers));
    }

    /*
    public void finishGame(){
      if(currentRound == 10){
      }
    }
     */

    //actual functions
    public String setFirstPlayer(){
        currentPlayer = offerTrack.getTurnTile().getTurnOrder().getFirst();
        return currentPlayer.getName();
    }

    public Player setNextPlayer(){
        if(!isStarted){
            throw new GameNotStartedException();
        }
        ArrayList<Player> turnOrder = offerTrack.getTurnTile().getTurnOrder();

        if (currentPlayer == null) {
            this.currentPlayer = turnOrder.getFirst();
            return this.currentPlayer;
        }

        int currentPlayerIndex = turnOrder.indexOf(currentPlayer);
        if  (currentPlayerIndex < turnOrder.size()-1){
            this.currentPlayer =  turnOrder.get(currentPlayerIndex+1);
            return this.currentPlayer;
        }
        // se è finita la fase di piazzamento dei totem, si passa a pescare le carte
        else {
            if (currentPhase == GamePhase.START_TURN) {
                offerTrack.getTurnTile().updateTurnOrder();
            }
            this.currentPlayer = null;
            throw new LastPlayerOfTurnException();
        }
    }

    public void initOfferTrack() {
        if(currentRound == 1){
            offerTrack.initializeBottomRow();
            offerTrack.repopulateTopRow();
            offerTrack.repopulateTopBuildingCards();
        } else if(currentRound <= 10){
            offerTrack.moveCardsToBottom();
            offerTrack.repopulateTopRow();
        }
        for(Card card: offerTrack.getTopRow()){
            if(card.getEra() != era){
                throw new ChangeEraException();
            }
        }
    }

    /*
    * Called in "startRound()" in GameController, return the next round, throws Last_Round_Exception if
    * the last round has been played
     */
    public int setNextRound() {
        if(currentRound == 10) {
            throw new LastRoundException("Fine del gioco raggiunta");
        }
        return currentRound++;
    }

    private Map<String,Integer> giveInitialFood(int numPlayers){
        ArrayList<Player> turnOrder = offerTrack.getTurnTile().getTurnOrder();
        Map<String,Integer> initialFood = new HashMap<>();
        turnOrder.get(0).getTribe().modifyFood(2);
        initialFood.put(turnOrder.get(0).getName(), 2);
        turnOrder.get(1).getTribe().modifyFood(3);
        initialFood.put(turnOrder.get(1).getName(), 3);

        if(numPlayers >= 3) {
            turnOrder.get(2).getTribe().modifyFood(3);
            initialFood.put(turnOrder.get(2).getName(), 3);
        }
        if(numPlayers >= 4) {
            turnOrder.get(3).getTribe().modifyFood(4);
            initialFood.put(turnOrder.get(3).getName(), 4);
        }
        if(numPlayers >= 5) {
            turnOrder.get(4).getTribe().modifyFood(4);
            initialFood.put(turnOrder.get(4).getName(), 4);
        }
        return initialFood;
    }

    public void changeEra() { //da mettere un'eccezione (inutile)
        era++;
        offerTrack.moveBuildings();
        offerTrack.repopulateTopBuildingCards();
    }

    public void chooseOfferTile(Player player, int index){
        offerTrack.getOfferTiles().get(index).occupy(player.getName());
        player.setCurrentOfferTile(offerTrack.getOfferTiles().get(index));
    }


//    //direi che potrebbe essere il caso di fare una classe turnManager: ci sono un sacco di cose di cui tener conto
//    /**
//     * @deprecated
//     */
//    public void playGame() throws IllegalDrawException {
//
//        this.startGame();
//        while(this.currentRound <= 10){
//            //turno di player 1 da createGame()
//            for(int i=0; i<this.numPlayers;i++){//tutti scelgono la loro tile in ordine
//
//                //classe controller richiede l'indice input
//                int k=0;
//
//                //currentPlayer.chooseOfferTile(k, offerTrack);
//                buildingManager.useBuilding(currentPhase, currentPlayer);
//                setNextPlayer();
//            }
//
//            //se qualcuno sceglie la tessera A dagli 3 cibo
//            for(int i=0; i<this.numPlayers;i++){
//                Player player = offerTrack.getTurnTile().getTurnOrder().get(i);
//                player.getTribe().modifyFood(player.getCurrentOfferTile().getFoodBonus());
//            }
//
//            this.setCurrentPhase(GamePhase.END_GAME);//fase draw
//            //aggiornare turnorder qui
//            //return turnTile
//            offerTrack.getTurnTile().updateTurnOrder();
//
//            //forse dentro questo for il discorso currentPlayer e n-esima iterazione del ciclo si può gestire meglio
//            for(Player player : offerTrack.getTurnTile().getTurnOrder()){//tutti scelgono le loro carte in ordine
//
//                //classe controller richiede gli indici input
//                boolean fromTopRow = false;
//                int index = 0;
//                boolean isBuilding = true;
//
//                int topDrawable = currentPlayer.getCurrentOfferTile().getCardsFromAbove();
//                int bottomDrawable = currentPlayer.getCurrentOfferTile().getCardsFromBelow();
//                for(int j = 0; j < topDrawable + bottomDrawable; j++) {
//                    //getRow && index da controller
//
//                    //da verificare che cardsLeft funzioni bene
//                    int cardsLeft = (fromTopRow) ? topDrawable : bottomDrawable;
//                    if(cardsLeft > 0){
//                        while(!currentPlayer.drawable(fromTopRow, isBuilding, index, offerTrack)){
//                            //chiede nuovi input
//                        }
//                        currentPlayer.drawCard(fromTopRow, isBuilding, index, offerTrack);
//                        if (fromTopRow) topDrawable--;
//                        else bottomDrawable--;
//                    }
//                    /* versione non ottimizzata
//                    if (fromTopRow == 0 && topDrawable > 0) {
//                        while(currentPlayer.drawable(index, fromTopRow, isBuilding, offerTrack) == false){
//                            //chiede nuovi input
//                        }
//                        currentPlayer.drawCard(index, fromTopRow, isBuilding, offerTrack);
//                        topDrawable--;
//                    }
//                    else if(fromTopRow == 1 && bottomDrawable > 0){
//                        while(currentPlayer.drawable(index, fromTopRow, isBuilding, offerTrack) == false){
//                            //chiede nuovi input
//                        }
//                        currentPlayer.drawCard(index, fromTopRow, isBuilding, offerTrack);
//                        bottomDrawable--;
//                    }*/
//                    else{
//                        throw new IllegalDrawException();
//                    }
//                    buildingManager.useBuilding(currentPhase, currentPlayer);
//                }
//                //fase intermittente tra return to tile on draw
//                currentPhase = GamePhase.RETURN_TO_TILE;
//                offerTrack.getTurnTile().returnToStartingTile(currentPlayer, buildingManager);
//                currentPhase = GamePhase.ON_DRAW;
//
//                setNextPlayer();
//            }
//
//            this.setCurrentPhase(GamePhase.END_GAME);//fase eventi
//            // If round is 10 then resolves both top and bottom rows' events.
//            if(currentRound<10){
//                eventManager.resolve(offerTrack.getBottomEvents(), players, buildingManager);
//            }else{
//                ArrayList<EventCard> allEvents = new ArrayList<>();
//                allEvents.addAll(offerTrack.getBottomEvents());
//                allEvents.addAll(offerTrack.getTopEvents());
//                eventManager.resolve(allEvents, players, buildingManager);
//            }
//
//
//            this.setCurrentPhase(GamePhase.END_GAME);//fase finale
//
//            for(int i=0; i<this.numPlayers;i++){//building attivati alla fine del round
//                buildingManager.useBuilding(currentPhase, currentPlayer);
//                setNextPlayer();
//            }
//
//
//            //fase inizializzata
//            this.setCurrentPhase(GamePhase.END_GAME);
//
//            //track inizializzata
//            offerTrack.moveCardsToBottom();
//            offerTrack.repopulateTopRow();
//            currentRound++;
//        }
//        currentPhase = GamePhase.END_GAME;
//
//        for(int i=0; i<this.numPlayers;i++){//building attivati alla fine del gioco
//            buildingManager.useBuilding(currentPhase, currentPlayer);
//            currentPlayer.getTribe().modifyPrestigePoints(currentPlayer.getTribe().calculateFinalPoints());
//            setNextPlayer();
//        }
//        ArrayList<Player> ranking = new ArrayList<>(players);
//        ranking=players.stream().sorted(Comparator.comparingInt(
//                (Player p) -> p.getTribe().getPrestigePoints()).reversed())
//                        .collect(Collectors.toCollection(ArrayList::new));
//        //showRanking nella view o qualcosa del genere
//    }

}