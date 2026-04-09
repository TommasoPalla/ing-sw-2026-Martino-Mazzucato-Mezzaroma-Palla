package cards_and_deck;

import enums.Era;
import users.Player;
import game.Game;

import java.util.ArrayList;
import java.util.ArrayDeque;
import game.Game;

public class Deck {
    private ArrayDeque<Card> tribeDeck;
    private ArrayList<BuildingCard> buildingsDeck;
    private int deckInitLength;

    //constructor called by game.Game.startGame()
    public Deck(int numPlayers){
        switch (numPlayers){
            case 2 -> this.deckInitLength = 70;//numero da definire
            case 3 -> this.deckInitLength = 75;//num
            case 4 -> this.deckInitLength = 80;//num
            case 5 -> this.deckInitLength = 85;//num
        }
        initTribeDeck(Era.FIRST);
    }
    //method called by game.Game.changeEra(), creates a new deck for the current era
    public void initTribeDeck(Era era){

    }
    public Card drawCard(){
        if(!tribeDeck.isEmpty()) {
            return tribeDeck.pop();
        }    //pop lancia noSuchElementException, si potrebbe usare al posto dell'if
        else {
            Era e = Game.getInstance().getEra();
            initTribeDeck(e);
        }
    }
    public int getCardsNumber(){

    }
}
