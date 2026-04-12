package cards_and_deck;

import enums.EventType;
import game.Game;

import java.util.*;

public class Deck {
    private ArrayDeque<Card> tribeDeck;
    private ArrayDeque<BuildingCard> buildingsDeck;
    private int[] buildingsDeckLength;  //Number of buildingCards for each era

    // capire se metterli qua o in Game
    private final List<CharacterCard> allCharacterCards;
    private final List<EventCard> allEventCards;
    private final List<BuildingCard> allBuildingCards;    //from JSON

    //constructor called by game.Game.startGame()
    public Deck(int numPlayers){
        switch (numPlayers){
            case 2 -> this.buildingsDeckLength = new int[]{1, 2, 3};
            case 3 -> this.buildingsDeckLength = new int[]{2, 2, 4};
            case 4 -> this.buildingsDeckLength = new int[]{2, 3, 4};
            case 5 -> this.buildingsDeckLength = new int[]{2, 3, 5};
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

    /** method intTribeDeck creates a new deck with current era's characters and events
     * first picks all cards suitable for the number of players, then it shuffles them and returns a deque
     */
    //method called by Game.getInstance().changeEra()
    //cambiare fare un deck unico
    public void initTribeDeck(int era){
        List<Card> tempDeck = new ArrayList<>();
        for (CharacterCard charCard: allCharacterCards){
            if(charCard.getEra() == era){
                tempDeck.add(charCard);
            }
        }
        // forse si può migliorare la gestione delle carte Evento Finale
        for(EventCard evCard: allEventCards){
            if(evCard.getEra() == era && (era != 3 ||
                    (evCard.getEventType() != EventType.SUSTENANCE && evCard.getEventType() != EventType.SHAMANIC_RITUAL))){
                tempDeck.add(evCard);
            }
        }
        Collections.shuffle(tempDeck);
        this.tribeDeck = new ArrayDeque<>(tempDeck);
        //aggiunta carte evento finale
        for(EventCard evCard: allEventCards){
            if(evCard.getEra() == 3 &&
                    (evCard.getEventType() == EventType.SUSTENANCE || evCard.getEventType() == EventType.SHAMANIC_RITUAL)){
                this.tribeDeck.push(evCard);
            }
        }
    }

    /** method initBuildingDeck creates a deck with current era's buildings
     * method shuffles all buildings of the current era and then picks the correct number of cards for the number
     * of players
    * */
    public void initBuildingDeck(int era){
        List<BuildingCard> tempDeck = new ArrayList<>();
        this.buildingsDeck = new ArrayDeque<>();
        for (BuildingCard buildingCard: allBuildingCards){
            if(buildingCard.getEra() == era){
                tempDeck.add(buildingCard);
            }
        }
        Collections.shuffle((tempDeck));
        for(int i = 0; i < buildingsDeckLength[era] && i < tempDeck.size(); i++){
            BuildingCard tempCard = tempDeck.get(i);
            this.buildingsDeck.push(tempCard);
        }
    }

    public Card drawCard() {
        int era = Game.getInstance().getEra();
        if(era != 3 && tribeDeck.peek().getEra() != era){
            Game.getInstance().changeEra();
            //chiama repopulate buildings
        }
        return tribeDeck.pop();
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
