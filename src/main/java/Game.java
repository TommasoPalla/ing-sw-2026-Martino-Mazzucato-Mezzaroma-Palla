import java.util.ArrayList;
import java.util.Collections;
import users.Player;
import game_board.OfferTrack;
import enums.GamePhase;



public class Game {
  
  final private ArrayList<Player> players;
  final private int numPlayers;
  private Player currentPlayer;
  private ArrayList<Player> turnOrder;
  private int era;
  private int currentRound;
  private GamePhase currentPhase;
  private OfferTrack offertrack;
  
  /*private string game_id;
  public Game(string game_id){
    this.game_id=game_id;
  }*/

  public Game(ArrayList<Player> players) {
      this.players = players;
      this.numPlayers = players.size();
  }

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

      currentPlayer=turnOrder.get(0);
      currentRound=1;
      era=1;

      offertrack=new OfferTrack(numPlayers);

      currentPhase=GamePhase.START_TURN;
  }


  public void finishGame(){
      if(currentRound == 10){

      }
  }

  public void setCurrentPlayer(){
      currentPlayer=getNextPlayer();
  }
  public void updateCurrentPhase(){


  }
  public void checkEra(){

  }

  public void changeEra(){
      era=era++;
  }

  public int getEra(){
      return era;
  }




}
