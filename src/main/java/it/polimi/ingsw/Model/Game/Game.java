package it.polimi.ingsw.Model.Game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.BuildingsManagement.BuildingManager;
import it.polimi.ingsw.Model.Deck.Deck;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Model.GameBoard.TurnTile;
import it.polimi.ingsw.Model.Users.Illegal_Draw_Exception;
import it.polimi.ingsw.Model.Users.Player;
import it.polimi.ingsw.Model.GameBoard.OfferTrack;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.EventManagement.EventManager;

/*
* esempio Era e = Game.getInstance().getEra();
 */
public class Game {
    private final String gameID;
    final private ArrayList<Player> players;
    final private int numPlayers;
    private Player currentPlayer;
    private ArrayList<Player> turnOrder;
    private int era;
    private int currentRound;
    private GamePhase currentPhase;
    private OfferTrack offerTrack;
    private BuildingManager buildingManager;
    private EventManager eventManager = new EventManager();
    private static Deck deck;      //forse static non è la soluzione ma ad ora non so che altro fare
    private TurnTile turnTile;

  /*
  * Game constructor, which with the game is initialized. It initializes the players from their name and
  * the totem color they chose.
   */
    // Ciò dovrebbe rendere più pulita l'inizializzazione, facendo seguire necessariamente l'inizializzazione
    // dei Player a quella del game e non viceversa
    public Game(String gameID, Map<String, Color> newPlayers) {
        this.gameID = gameID;
        this.numPlayers = newPlayers.size();
        this.turnTile = new TurnTile(numPlayers);
        this.players = new ArrayList<>();
        for (String name : newPlayers.keySet()) {
            Player newPlayer = new Player(this, name, newPlayers.get(name));
            this.players.add(newPlayer);
          }
        this.buildingManager = new BuildingManager(players);
    }

    //getters
    // non ho assolutamente idea se sia il modo migliore per fare questa cosa
    public static Deck getDeck() {return deck;}
    public GamePhase getGamePhase(){return currentPhase;}
    public Player getCurrentPlayer(){return currentPlayer;}
    public int getNumPlayer(){return numPlayers;}
    public OfferTrack getOfferTrack(){return offerTrack;}
    public BuildingManager getBuildingManager(){return buildingManager;}
    public EventManager getEventManager(){return eventManager;}
    public ArrayList<Player> getTurnOrder(){return turnOrder;}
    public int getEra(){return era;}

    private Player getNextPlayer() /*throws No_More_Players_Exception*/{
        int i=0;
        while(turnOrder.get(i)!=currentPlayer && i<this.numPlayers){
            i++;
        }
        if(i==this.numPlayers-1){
            i=-1;
        }
        return turnOrder.get(i+1);
    }


    public void startGame(){

      turnOrder = new ArrayList<>(players);
      Collections.shuffle(turnOrder);

      currentPlayer = turnOrder.getFirst();
      currentRound = 1;
      era = 1;

      offerTrack = new OfferTrack(this, numPlayers);
      //buildingManager = new BuildingManager();
      eventManager = new EventManager();
      turnTile = new TurnTile(numPlayers);
      deck = new Deck(this, numPlayers, "json/cards.json");

      //inizializzo track
      offerTrack.initializeBottomRow();
      offerTrack.repopulateTopRow();
      offerTrack.repopulateTopBuildingCards();

      giveInitialFood(numPlayers);

      currentPhase = GamePhase.START_TURN;
    }


    /*
    public void finishGame(){
      if(currentRound == 10){

      }
    }
     */

    //actual functions
    private void setCurrentPlayer(){currentPlayer = getNextPlayer();}

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
    public void playGame() throws Illegal_Draw_Exception {

        this.startGame();
        while(this.currentRound <= 10){
            //turno di player 1 da startGame()
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
            turnOrder = turnTile.getTurnOrder(turnOrder);

            for(Player player : turnOrder){//tutti scelgono le loro carte in ordine

                //classe controller richiede gli indici input
                int whichRow = 0;
                int index = 0;

                int topDrawable = currentPlayer.getCurrentOfferTile().getCardsFromAbove();
                int bottomDrawable = currentPlayer.getCurrentOfferTile().getCardsFromBelow();
                for(int j=0; j<topDrawable+bottomDrawable;j++) {
                    //getRow && index da controller
                    if (whichRow == 0 && topDrawable > 0) {
                        while(currentPlayer.drawable(index,offerTrack, whichRow)!=true){
                            //chiede nuovi input
                        }
                        currentPlayer.drawFromTopRow(index, offerTrack);
                        topDrawable--;
                    }
                    else if(whichRow == 1 && bottomDrawable > 0){
                        while(currentPlayer.drawable(index,offerTrack, whichRow)!=true){
                            //chiede nuovi input
                        }
                        currentPlayer.drawFromBottomRow(index, offerTrack);
                        bottomDrawable--;
                    }
                    else{
                        throw new Illegal_Draw_Exception();
                    }
                    buildingManager.useBuilding(currentPhase, currentPlayer);
                }
                //fase intermittente tra return to tile on draw
                currentPhase = GamePhase.RETURN_TO_TILE;
                turnTile.returnToStartingTile(turnOrder, currentPlayer, buildingManager);
                currentPhase = GamePhase.ON_DRAW;

                currentPlayer=getNextPlayer();
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
    }

}

