package game;

import java.util.ArrayList;
import java.util.Collections;

import building_management.BuildingManager;
import cards_and_deck.Deck;
import users.Player;
import game_board.OfferTrack;
import enums.GamePhase;


/* Game uses the Singleton design pattern: one instance of the game is created, with a specific ID to
* define different games' instances. A method "getInstance()" allows other classes to access the game instance
* */

/*
* esempio Era e = Game.getInstance().getEra();
 */
public class Game {
    private static Game instance;
    final private ArrayList<Player> players;
    final private int numPlayers;
    private Player currentPlayer;
    private ArrayList<Player> turnOrder;
    private int era;
    private int currentRound;
    private GamePhase currentPhase;
    private OfferTrack offerTrack;
    private BuildingManager buildingManager;
    private static Deck deck;       //forse static non è la soluzione ma ad ora non so che altro fare

  /* Game constructor, which with the game is initialized
  *
   */
    public Game(Game gameID, ArrayList<Player> players) {
      instance = gameID;
      this.players = players;
      this.numPlayers = players.size();
    }

    public static Game getInstance() {return instance;}
    // non ho assolutamente idea se sia il modo migliore per fare questa cosa
    public static Deck getDeck() {return deck;}

    public GamePhase getGamePhase(){return currentPhase;}
    public Player getCurrentPlayer(){return currentPlayer;}
    public int getNumPlayer(){return numPlayers;}
    public OfferTrack getOfferTrack(){return offerTrack;}
    public BuildingManager getBuildingManager(){return buildingManager;}
    private Player getNextPlayer() /*throws No_More_Players_Exception*/{
      int i=0;
      while(turnOrder.get(i)!=currentPlayer && i<this.numPlayers){
          i++;
      }
      if(i==this.numPlayers-1){
          i=0;
      }
      return turnOrder.get(i);
    }
    public ArrayList<Player> getTurnOrder(){return turnOrder;}



    public void startGame(){

      turnOrder = new ArrayList<>(players);
      Collections.shuffle(turnOrder);

      currentPlayer = turnOrder.get(0);
      currentRound = 1;
      era = 1;

      offerTrack = new OfferTrack(numPlayers);
      buildingManager = new BuildingManager();

      currentPhase = GamePhase.START_TURN;
    }


    public void finishGame(){
      if(currentRound == 10){

      }
    }

    public void setCurrentPlayer(){
      currentPlayer = getNextPlayer();
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
    /*public void checkEra(){

    }*/

    public void changeEra(){
      era++;
    }

    public int getEra(){
      return era;
    }

    public void playGame(){
        this.startGame();
        while(deck.getCardsNumber()>0){
            //turno di player 1 da startGame()
            for(int i=0; i<this.numPlayers;i++){//tutti scelgono la loro tile in ordine
                currentPlayer.chooseOfferTile();
                currentPlayer=getNextPlayer();
            }
            this.updateCurrentPhase();
            for(int i=0; i<this.numPlayers;i++){//tutti scelgono le loro carte in ordine
                currentPlayer.drawFromTopRow();
                currentPlayer.drawFromBottomRow();
                currentPlayer=getNextPlayer();
            }
            this.updateCurrentPhase();//scartare bottom, fase eventi
            //manca metodo per scartare la fila sotto attivando effetti
            offerTrack.moveCardsToBottom();
            offerTrack.repopulateTopRow();



        }
    }

}

