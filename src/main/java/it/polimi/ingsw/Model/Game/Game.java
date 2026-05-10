package it.polimi.ingsw.Model.Game;

import java.util.*;
import java.util.stream.Collectors;

import it.polimi.ingsw.CustomException.UIException.IllegalActionPhaseException;
import it.polimi.ingsw.CustomException.IllegalDrawException;
import it.polimi.ingsw.CustomException.LastPlayerOfTurnException;
import it.polimi.ingsw.CustomException.LastRoundException;
import it.polimi.ingsw.CustomException.UnavailableColorException;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.Deck.Deck;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Model.Users.*;
import it.polimi.ingsw.Model.GameBoard.OfferTrack;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.EventManagement.EventManager;

public class Game {
    private final int gameID;
    private final ArrayList<Player> players;
    final private int numPlayers;
    private Player currentPlayer;

    /*è ridondante rispetto allo stesso attributo di TurnTile
    ma dato che viene usato molto si potrebbe lasciare
     */
    private ArrayList<Player> turnOrder;
    private int era;
    private int currentRound;
    private GamePhase currentPhase;
    private OfferTrack offerTrack;
    private BuildingManager buildingManager;
    private EventManager eventManager = new EventManager();
    private Deck deck;
    private final Map<String, Color> totemColors = new HashMap<>();


    public Game(int gameID, int numPlayers) {
        this.gameID = gameID;
        this.numPlayers = numPlayers;
        this.players = new ArrayList<>();
        this.currentRound = 0;
    }

    //getters
    public int getGameID(){
        return gameID;
    }
    public Deck getDeck() {return deck;}
    public GamePhase getGamePhase(){return currentPhase;}
    public Player getCurrentPlayer(){return currentPlayer;}
    public ArrayList<String> getPlayersNames(){
        ArrayList<String> names = new ArrayList<>();
        for (Player p: players){
            names.add(p.getName());
        }
        return names;
    }
    public ArrayList<Player> getPlayers(){ return players; }
    public int getNumPlayer(){return numPlayers;}
    public OfferTrack getOfferTrack(){return offerTrack;}
    public BuildingManager getBuildingManager(){return buildingManager;}
    public EventManager getEventManager(){return eventManager;}
    public ArrayList<Player> getTurnOrder(){return turnOrder;}
    public int getEra(){return era;}
    public Set<Color> getAvailableColors(){
        Set<Color> colors = EnumSet.allOf(Color.class);
        for(Color color: totemColors.values()){
            colors.remove(color);
        }
        return colors;
    }

    public Player getPlayerByName(String playerName){
        return players.stream().filter(p -> p.getName().equals(playerName))
                .findFirst().orElse(null);
    }

    public Player getNextPlayer() {
        int currentPlayerIndex = turnOrder.indexOf(currentPlayer);
        if  (currentPlayerIndex < turnOrder.size()-1) return turnOrder.get(currentPlayerIndex+1);
        else throw new LastPlayerOfTurnException();
    }

    //not sure about the logic here but also can't see anything horribly wrong
    //maybe the control logic should con in GameController and here the values
    //are only set to what they should be and nothing more
    public void chooseTotemColor(String playerName, Color totemColor){
        totemColors.put(playerName, totemColor);
    }

    public void addPlayer(String playerName) {
        Player newPlayer = new Player(this, playerName);
        players.add(newPlayer);
        //if(players.size() == numPlayers) currentPhase = READY_TO_START; Se il numero di giocatori necessario
        // è stato raggiunto, cambia la fase da IN_LOBBY a READY_TO_START
    }

    public void removePlayer(String playerName) {
        players.remove(getPlayerByName(playerName));
    }

    public void startGame(){
        this.currentRound = 1;
        this.era = 1;

        this.deck = new Deck(this, "json/cards.json");

        this.offerTrack = new OfferTrack(this, numPlayers);
        this.offerTrack.initializeBottomRow();
        this.offerTrack.repopulateTopRow();
        this.offerTrack.repopulateTopBuildingCards();

        this.turnOrder = offerTrack.getTurnTile().initTurnOrder(players);
        this.currentPlayer = turnOrder.getFirst();

        this.eventManager = new EventManager();
        this.buildingManager = new BuildingManager(players);

        giveInitialFood(numPlayers);
        this.currentPhase = GamePhase.START_TURN;
    }


    /*
    public void finishGame(){
      if(currentRound == 10){

      }
    }
     */

    //actual functions
    public Player setNextPlayer(){
        return currentPlayer = getNextPlayer();
    }

    /*
    * Called in "startTurn()" in GameController, return the next round, throws Last_Round_Exception if
    * the last round has been played
     */
    public int getNextRound() {
        if(currentRound == 10) {
            throw new LastRoundException("Fine del gioco raggiunta");
        }
        return currentRound++;
    }

    private void giveInitialFood(int numPlayers){
        turnOrder.get(0).getTribe().modifyFood(2);
        turnOrder.get(1).getTribe().modifyFood(3);

        if(numPlayers >= 3) turnOrder.get(2).getTribe().modifyFood(3);
        if(numPlayers >= 4) turnOrder.get(3).getTribe().modifyFood(4);
        if(numPlayers >= 5) turnOrder.get(4).getTribe().modifyFood(5);
    }

    public void updateCurrentPhase(){
        switch(currentPhase){
            case GamePhase.START_TURN:
                currentPhase=GamePhase.ON_DRAW;
                break;
            case GamePhase.ON_DRAW:
                currentPhase=GamePhase.ON_EVENT;
                break;
            case GamePhase.ON_EVENT:
                currentPhase=GamePhase.END_TURN;
                break;
            case GamePhase.END_TURN:
                currentPhase=GamePhase.START_TURN;
                break;
        }


    }

    public void changeEra() { //da mettere un'eccezione
        era++;
        offerTrack.moveBuildings();
        offerTrack.repopulateTopBuildingCards();
    }


    //direi che potrebbe essere il caso di fare una classe turnManager: ci sono un sacco di cose di cui tener conto
    public void playGame() throws IllegalDrawException {

        this.startGame();
        while(this.currentRound <= 10){
            //turno di player 1 da createGame()
            for(int i=0; i<this.numPlayers;i++){//tutti scelgono la loro tile in ordine

                //classe controller richiede l'indice input
                int k=0;

                currentPlayer.chooseOfferTile(k, offerTrack);
                buildingManager.useBuilding(currentPhase, currentPlayer);
                currentPlayer=getNextPlayer();
            }

            //se qualcuno sceglie la tessera A dagli 3 cibo
            for(int i=0; i<this.numPlayers;i++){
                turnOrder.get(i).getTribe().modifyFood(turnOrder.get(i).getCurrentOfferTile().getFoodBonus());
            }

            this.updateCurrentPhase();//fase draw
            //aggiornare turnorder qui
            //return turnTile
            turnOrder = offerTrack.getTurnTile().updateTurnOrder();

            //forse dentro questo for il discorso currentPlayer e n-esima iterazione del ciclo si può gestire meglio
            for(Player player : turnOrder){//tutti scelgono le loro carte in ordine

                //classe controller richiede gli indici input
                boolean fromTopRow = false;
                int index = 0;
                boolean isBuilding = true;

                int topDrawable = currentPlayer.getCurrentOfferTile().getCardsFromAbove();
                int bottomDrawable = currentPlayer.getCurrentOfferTile().getCardsFromBelow();
                for(int j = 0; j < topDrawable + bottomDrawable; j++) {
                    //getRow && index da controller

                    //da verificare che cardsLeft funzioni bene
                    int cardsLeft = (fromTopRow) ? topDrawable : bottomDrawable;
                    if(cardsLeft > 0){
                        while(!currentPlayer.drawable(fromTopRow, isBuilding, index, offerTrack)){
                            //chiede nuovi input
                        }
                        currentPlayer.drawCard(fromTopRow, isBuilding, index, offerTrack);
                        if (fromTopRow) topDrawable--;
                        else bottomDrawable--;
                    }
                    /* versione non ottimizzata
                    if (fromTopRow == 0 && topDrawable > 0) {
                        while(currentPlayer.drawable(index, fromTopRow, isBuilding, offerTrack) == false){
                            //chiede nuovi input
                        }
                        currentPlayer.drawCard(index, fromTopRow, isBuilding, offerTrack);
                        topDrawable--;
                    }
                    else if(fromTopRow == 1 && bottomDrawable > 0){
                        while(currentPlayer.drawable(index, fromTopRow, isBuilding, offerTrack) == false){
                            //chiede nuovi input
                        }
                        currentPlayer.drawCard(index, fromTopRow, isBuilding, offerTrack);
                        bottomDrawable--;
                    }*/
                    else{
                        throw new IllegalDrawException();
                    }
                    buildingManager.useBuilding(currentPhase, currentPlayer);
                }
                //fase intermittente tra return to tile on draw
                currentPhase = GamePhase.RETURN_TO_TILE;
                offerTrack.getTurnTile().returnToStartingTile(currentPlayer, buildingManager);
                currentPhase = GamePhase.ON_DRAW;

                currentPlayer = getNextPlayer();
            }

            this.updateCurrentPhase();//fase eventi
            // If round is 10 then resolves both top and bottom rows' events.
            if(currentRound<10){
                eventManager.resolve(offerTrack.getBottomEvents(), players, buildingManager);
            }else{
                ArrayList<EventCard> allEvents = new ArrayList<>();
                allEvents.addAll(offerTrack.getBottomEvents());
                allEvents.addAll(offerTrack.getTopEvents());
                eventManager.resolve(allEvents, players, buildingManager);
            }


            this.updateCurrentPhase();//fase finale

            for(int i=0; i<this.numPlayers;i++){//building attivati alla fine del round
                buildingManager.useBuilding(currentPhase, currentPlayer);
                currentPlayer=getNextPlayer();
            }


            //fase inizializzata
            this.updateCurrentPhase();

            //track inizializzata
            offerTrack.moveCardsToBottom();
            offerTrack.repopulateTopRow();
            currentRound++;
        }
        currentPhase = GamePhase.END_GAME;

        for(int i=0; i<this.numPlayers;i++){//building attivati alla fine del gioco
            buildingManager.useBuilding(currentPhase, currentPlayer);
            currentPlayer.getTribe().modifyPrestigePoints(currentPlayer.getTribe().calculateFinalPoints());
            currentPlayer=getNextPlayer();
        }
        ArrayList<Player> ranking = new ArrayList<>(players);
        ranking=players.stream().sorted(Comparator.comparingInt(
                (Player p) -> p.getTribe().getPrestigePoints()).reversed())
                        .collect(Collectors.toCollection(ArrayList::new));
        //showRanking nella view o qualcosa del genere
    }

}