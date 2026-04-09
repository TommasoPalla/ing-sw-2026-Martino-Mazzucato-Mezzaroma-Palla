package cards_and_deck;

import game.Game;

import java.util.ArrayList;
import java.util.ArrayDeque;

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
        initTribeDeck(1);
    }
    //method called by game.Game.changeEra(), creates a new deck for the current era
    public void initTribeDeck(int era){

    }
    public Card drawCard(){
        if(!tribeDeck.isEmpty()) {
            return tribeDeck.pop();
        }    //pop lancia noSuchElementException, si potrebbe usare al posto dell'if
        else {
            int era = Game.getInstance().getEra();
            initTribeDeck(era);
            return null;
        }
    }
    public int getCardsNumber(){
        return tribeDeck.size();
    }
}
