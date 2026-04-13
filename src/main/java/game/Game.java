package game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.stream.Collectors;

import building_management.BuildingManager;
import cards_and_deck.Deck;
import users.Player;
import game_board.OfferTrack;
import enums.GamePhase;
import game_board.OfferTile;


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

      //inizializzo track
      offerTrack.initializeBottomRow();
      offerTrack.repopulateTopRow();
      offerTrack.repopulateTopBuildingCards();


      //inizializzo cibo
      turnOrder.get(0).getTribe().modifyFood(+2);
      turnOrder.get(1).getTribe().modifyFood(+3);

      switch (numPlayers){
          case 2:
              break;
          case 3:
              turnOrder.get(2).getTribe().modifyFood(+3);
              break;
          case 4:
              turnOrder.get(3).getTribe().modifyFood(+4);
              break;
          case 5:
              turnOrder.get(4).getTribe().modifyFood(+4);
              break;
      }

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

    public void changeEra(){ //da mettere un'eccezione
      era++;
      offerTrack.moveBuildings();
      offerTrack.repopulateTopBuildingCards();
    }

    public int getEra(){
      return era;
    }

    public void playGame(){
        ArrayList <OfferTile> newTurns = null;

        this.startGame();
        //ripristina game phase
        while(this.currentRound<10){
            //turno di player 1 da startGame()
            for(int i=0; i<this.numPlayers;i++){//tutti scelgono la loro tile in ordine


                //classe controller richiede l'indice input
                int k=0;


                currentPlayer.chooseOfferTile(k, offerTrack);
                currentPlayer=getNextPlayer();
            }

            //se qualcuno sceglie la tessera A dagli 3 cibo
            for(int i=0; i<this.numPlayers;i++){
                turnOrder.get(i).getTribe().modifyFood(turnOrder.get(i).getCurrentOfferTile().getFoodBonus());
            }


            this.updateCurrentPhase();//fase draw


            for(int i=0; i<this.numPlayers;i++){//tutti scelgono le loro carte in ordine


                int j=0;
                //classe controller richiede l'indice input
                currentPlayer.drawFromTopRow(j, offerTrack);
                //classe controller richiede l'indice input
                currentPlayer.drawFromBottomRow(j, offerTrack);


                currentPlayer=getNextPlayer();
            }

            this.updateCurrentPhase();//fase eventi

            //resolve

            for(int i=0; i<this.numPlayers;i++){//creazione newTurns per riordinare i turni
                newTurns.add(i, currentPlayer.getCurrentOfferTile());
                currentPlayer=getNextPlayer();
            }
            //ripristino turnOrder in ordine alfabetico
            turnOrder=newTurns.stream().sorted(Comparator.comparing(OfferTile :: getTileCode))
                    .map(OfferTile::getCurrentOccupant).collect(Collectors.toCollection(ArrayList::new));

            this.updateCurrentPhase();//fase finale

            //remove bottom row
            offerTrack.moveCardsToBottom();
            offerTrack.repopulateTopRow();



        }
    }

}

