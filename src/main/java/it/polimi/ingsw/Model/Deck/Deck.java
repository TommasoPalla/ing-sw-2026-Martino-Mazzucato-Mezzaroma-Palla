package it.polimi.ingsw.Model.Deck;

import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Model.Cards.*;
import it.polimi.ingsw.Model.Cards.Buildings.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Game.Game;

import java.util.*;

public class Deck {
    private ArrayDeque<Card> tribeDeck;
    private ArrayDeque<BuildingCard> buildingsDeck;
    private int[] buildingsDeckLength;  //Number of buildingCards for each era

    private final List<CharacterCard> allCharacterCards;
    private final List<EventCard> allEventCards;
    private final List<BuildingCard> allBuildingCards;    //from JSON

    //constructor called by game.Game.startGame()
    public Deck(int numPlayers, String jsonPath){
        switch (numPlayers){
            case 2 -> this.buildingsDeckLength = new int[]{1, 2, 3};
            case 3 -> this.buildingsDeckLength = new int[]{2, 2, 4};
            case 4 -> this.buildingsDeckLength = new int[]{2, 3, 4};
            case 5 -> this.buildingsDeckLength = new int[]{2, 3, 5};
        }

        CardLoader loader = new CardLoader();
        this.allCharacterCards = loader.loadCharacters(jsonPath);   //card Loader
        this.allEventCards = loader.loadEvents(jsonPath);
        this.allBuildingCards = loader.loadBuildings(jsonPath);     //cambiare path
        initTribeDeck(numPlayers);
        initBuildingDeck();
    }

    //getters
    public int getCardsNumber(){return tribeDeck.size();}   //serve?
    public ArrayDeque<Card> getTribeDeck() {return tribeDeck;}
    public ArrayDeque<BuildingCard> getBuildingsDeck() {return buildingsDeck;}

    /** method intTribeDeck creates a new deck with characters and events
     * first picks all cards suitable for the number of players, then it shuffles them and returns a deque
     */
    //method called by Game.getInstance().changeEra()
    private void initTribeDeck(int numPlayers){
        this.tribeDeck = new ArrayDeque<>();
        for(int era = 1; era <= 3; era++){
            List<Card> tempDeck = new ArrayList<>();
            for (CharacterCard charCard: allCharacterCards){
                if(charCard.getEra() == era && charCard.getNumPlayersFlag() <= numPlayers){
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
            this.tribeDeck.addAll(tempDeck);
        }
        //final events are added to the deck
        for(EventCard evCard: allEventCards){
            if(evCard.getEra() == 3 &&
                    (evCard.getEventType() == EventType.SUSTENANCE || evCard.getEventType() == EventType.SHAMANIC_RITUAL)){
                this.tribeDeck.add(evCard);
            }
        }
    }

    /** method initBuildingDeck creates a deck with buildings
     * method shuffles all buildings for each era and then picks the correct number of cards for the number
     * of players
    * */
    private void initBuildingDeck(){
        this.buildingsDeck = new ArrayDeque<>();
        for(int era = 1; era <= 3; era++){
            List<BuildingCard> tempDeck = new ArrayList<>();
            for (BuildingCard buildingCard: allBuildingCards){
                if(buildingCard.getEra() == era){
                    tempDeck.add(buildingCard);
                }
            }
            Collections.shuffle((tempDeck));
            for(int i = 0; i < buildingsDeckLength[era] && i < tempDeck.size(); i++){
                BuildingCard tempCard = tempDeck.get(i);
                this.buildingsDeck.add(tempCard);
            }
        }
    }

    /** drawCard method is used to repopulate topRow and bottomRow, drawing cards from Deck
     * @return instance of Card
     */
    public Card drawCard() {
        int era = Game.getInstance().getEra();
        if(era != 3 && tribeDeck.peek().getEra() != era){
            Game.getInstance().changeEra();
        }
        return tribeDeck.pop();
    }
}
