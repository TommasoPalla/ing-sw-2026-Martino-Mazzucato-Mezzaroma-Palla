package cards_and_deck;

import game.Game;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private ArrayDeque<Card> tribeDeck;
    private ArrayDeque<BuildingCard> buildingsDeck;
    private int[] buildingsDeckLenght;  //Number of buildingCards for each era

    // capire se metterli qua o in Game
    private final List<CharacterCard> allCharacterCards;
    private final List<EventCard> allEventCards;
    private final List<BuildingCard> allBuildingCards;    //from JSON

    //constructor called by game.Game.startGame()
    public Deck(int numPlayers){
        switch (numPlayers){
            case 2 -> this.buildingsDeckLenght = new int[]{1, 2, 3};
            case 3 -> this.buildingsDeckLenght = new int[]{2, 2, 4};
            case 4 -> this.buildingsDeckLenght = new int[]{2, 3, 4};
            case 5 -> this.buildingsDeckLenght = new int[]{2, 3, 5};
        }
        //da revisionare, scegliere se fare 3 file diversi per ogni tipo di carta
        CardLoader loader = new CardLoader();
        this.allCharacterCards = loader.loadCharacters("cards.json");    //card Loader
        this.allEventCards = loader.loadEvents("cards.json");
        this.allBuildingCards = loader.loadBuildings("cards.json");
        initTribeDeck(1);
        initBuildingDeck(1);
    }

    //getters
    public int getCardsNumber(){return tribeDeck.size();}
    public ArrayDeque<Card> getTribeDeck() {return tribeDeck;}
    public ArrayDeque<BuildingCard> getBuildingsDeck() {return buildingsDeck;}

    //method called by game.Game.changeEra(), creates a new deck for the current era
    public void initTribeDeck(int era){
        List<Card> tempDeck = new ArrayList<>();
        for (CharacterCard charCard: allCharacterCards){
            if(charCard.getEra() == era){
                tribeDeck.add(charCard);
            }
        }
        for(EventCard evCard: allEventCards){
            if(evCard.getEra() == era){
                tribeDeck.add(evCard);
            }
        }
        Collections.shuffle(tempDeck);
        this.tribeDeck = new ArrayDeque<>(tempDeck);
    }

    public void initBuildingDeck(int era){
        List<Card> tempDeck = new ArrayList<>();
        for (BuildingCard buildingCard: allBuildingCards){
            if(buildingCard.getEra() == era){
                buildingsDeck.add(buildingCard);
                //shuffle
                //prnedo i primi buldingDeck[era-1]
                // da completare: inserire pick randomico
            }
        }
    }

    public Card drawCard(){
        if(!tribeDeck.isEmpty()) {
            return tribeDeck.pop();
        }    //pop lancia noSuchElementException, si potrebbe usare al posto dell'if
        else {
            // non dovrebbe essere chiamato changeEra()?
            Game.getInstance().changeEra(); //verificare che non vada oltre Era 3
            int era = Game.getInstance().getEra();
            initTribeDeck(era);
            return tribeDeck.pop();     //anche se cambi l'era poi comunque devi pescare la carta e metterla sul tracciato
        }
    }

    //uguale a drawCard. forse sta roba va cambiata e fatta un po' meglio idk
    public BuildingCard drawBuilding(){
        if(buildingsDeck.isEmpty()){
            int era = Game.getInstance().getEra();
            initBuildingDeck(era);
        }
        return buildingsDeck.pop();
    }
}
