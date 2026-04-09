package game;

import java.util.ArrayList;
import java.util.Collections;

import enums.Era;
import users.Player;
import game_board.OfferTrack;
import enums.GamePhase;


/* Game uses the Singleton design pattern: one instance of the game is created, with a specific ID to
* define different games' instances. A method "getInstance()" allows other classes to access the game instance
 */
public class Game {
  private static Game instance;
  final private ArrayList<Player> players;
  final private int numPlayers;
  private Player currentPlayer;
  private ArrayList<Player> turnOrder;
  private Era era;
  private int currentRound;
  private GamePhase currentPhase;
  private OfferTrack offertrack;

  /* Game constructor, which with the game is initialized
  *
   */
  public Game(Game gameID, ArrayList<Player> players) {
      instance = gameID;
      this.players = players;
      this.numPlayers = players.size();
  }

  public static Game getInstance() {return instance;}
  public GamePhase getGamePhase(){return currentPhase}
  public Player getCurrentPlayer(){return currentPlayer}
  public int getNumPlayer(){return numPlayers}
  private Player getNextPlayer(){
      int i=0;
      while(turnOrder.get(i)!=currentPlayer){
          i++;
      }
      return turnOrder.get(i+1);
  }
  public ArrayList<Player> getTurnOrder(){return turnOrder}



  public void startGame(){

      turnOrder = new ArrayList<>(players);
      Collections.shuffle(turnOrder);

      currentPlayer = turnOrder.get(0);
      currentRound = 1;
      era = Era.FIRST;

      offertrack = new OfferTrack(numPlayers);

      currentPhase=GamePhase.START_TURN;
  }


  public void finishGame(){
      if(currentRound == 10){

      }
  }

  public void setCurrentPlayer(){
      currentPlayer = getNextPlayer();
  }
  public void updateCurrentPhase(){


  }
  public void checkEra(){

  }

  public void changeEra(){
      era=era++;
  }

  public Era getEra(){
      return era;
  }




}
