package it.polimi.ingsw.Model.Deck;

import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Model.Cards.*;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.Parser.CardLoader;

import java.util.*;

/**The Deck class represents the actual game's deck,
 * with a tribe deck that consists of characters and events
 * and a building deck. Deck also has lists of all cards,
 * divided by type, instantiated by CardLoader methods.
 */
public class Deck {
    private final Game game;
    private ArrayDeque<Card> tribeDeck;
    private ArrayDeque<BuildingCard> buildingsDeck;
    private int[] buildingsDeckLength;  //Number of buildingCards for each era

    private final List<CharacterCard> allCharacterCards;
    private final List<EventCard> allEventCards;
    private final List<BuildingCard> allBuildingCards;    //from JSON

    //constructor called by game.startGame()
    public Deck(Game gameInstance, int numPlayers, String jsonPath){
        this.game = gameInstance;
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
    public ArrayDeque<Card> getTribeDeck() {return tribeDeck;}
    public ArrayDeque<BuildingCard> getBuildingsDeck() {return buildingsDeck;}

    /** Method intTribeDeck creates a new deck with characters and events:
     * first picks all cards suitable for the number of players,
     * then it shuffles them and moves the cards to the private tribeDeck field.
     * The method is defined as 'private' because it can only be invoked
     * by the class constructor.
     */
    private void initTribeDeck(int numPlayers){
        this.tribeDeck = new ArrayDeque<>();
        for(int era = 1; era <= 3; era++){
            List<Card> tempDeck = new ArrayList<>();
            for (CharacterCard charCard: allCharacterCards){
                if(charCard.getEra() == era && charCard.getNumPlayersFlag() <= numPlayers){
                    tempDeck.add(charCard);
                }
            }
            //It adds all eventCards except for the final events
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

    /** The initBuildingDeck method creates a deck with buildings:
     * it shuffles all buildings for each era and then picks
     * the correct number of cards for the number of players.
     * The method is defined as 'private' because it can only be invoked
     * by the class constructor.
    */
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

    /** The drawCard method is used to repopulate topRow and bottomRow,
     *  drawing cards from Deck of character and events.
     * @return instance of Card
     */
    public Card drawCard() {
        int era = game.getEra();
        if(era != 3 && tribeDeck.peek().getEra() != era){
            game.changeEra();
        }
        return tribeDeck.pop();
    }
}