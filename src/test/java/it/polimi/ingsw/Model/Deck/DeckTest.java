package it.polimi.ingsw.Model.Deck;

import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Model.Game.Game;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeckTest {
    private Deck deck;

    @ParameterizedTest(name = "Checking deck initialization for {0} players")
    @ValueSource(ints = {2, 3, 4, 5})
    void initDeck(int numPlayers) {
        Game game = new Game(1, numPlayers);
        game.setEra(1);
        deck = new Deck(game, "json/cards.json");
    }

    @ParameterizedTest(name = "Checking Building Deck size for {0} players")
    @ValueSource(ints = {2, 3, 4, 5})
    void testBuildingDeckCorrectSize(int numPlayers) {
        initDeck(numPlayers);
        int numBuildings;
        int[] buildingsForEra = new int[]{0, 0, 0};

        if (numPlayers == 2) {
            numBuildings = 6;
            buildingsForEra[0] = 1;
            buildingsForEra[1] = 2;
            buildingsForEra[2] = 3;
        } else if (numPlayers == 3) {
            numBuildings = 8;
            buildingsForEra[0] = 2;
            buildingsForEra[1] = 2;
            buildingsForEra[2] = 4;
        } else if (numPlayers == 4) {
            numBuildings = 9;
            buildingsForEra[0] = 2;
            buildingsForEra[1] = 3;
            buildingsForEra[2] = 4;
        } else {
            numBuildings = 10;
            buildingsForEra[0] = 2;
            buildingsForEra[1] = 3;
            buildingsForEra[2] = 5;
        }
        assertEquals(numBuildings, deck.getBuildingsDeck().size());
        BuildingCard tmp = deck.getBuildingsDeck().pop();
        int totalCards = 0;
        for (int era = 1; era <= 3 && totalCards < numBuildings; era++) {
            int i = 0;
            while (tmp.getEra() == era && totalCards < numBuildings) {
                i++;
                totalCards++;
                if (totalCards != numBuildings - 1) {
                    tmp = deck.getBuildingsDeck().pop();
                }
            }
            assertEquals(buildingsForEra[era - 1], i);
        }
        /*for(int i = 0; i < numBuildings; i++){
            BuildingCard tmp = deck.getBuildingsDeck().pop();
            System.out.println(tmp.getCardID());
            System.out.println(tmp.getEffect());*/
    }

    @ParameterizedTest(name = "Checking TribeDeck (characters) initialization for {0} players")
    @ValueSource(ints = {2, 3, 4, 5})
    public void testCharactersDeck(int numPlayers) {
        initDeck(numPlayers);
        String pattern = "^E[0-9]_C.*$";
        //int i = 1;
        for (Card card : deck.getTribeDeck()) {
            if (card.getCardID().matches(pattern)) {
                /*System.out.println(i);
                i++;*/
                CharacterCard character = (CharacterCard) card;
                /*System.out.println(character.getEra());
                System.out.println(character.getRole());*/
                assert (character.getNumPlayersFlag() <= numPlayers);
            }
        }
    }

    @Test
    public void testEventCardsDeck() {
        initDeck(5);    //numPlayers is irrelevant to EventCard initialization
        //int i = 0;

        String pattern = "^E[0-9]_E.*$";
        String ritualPattern = "^E[0-9]_E_R$";
        String sustenancePattern = "^E[0-9]_E_S$";
        String paintingsPattern = "^E[0-9]_E_C$";
        String huntPattern = "^E[0-9]_E_H$";

        for (Card card : deck.getTribeDeck()) {
            if (card.getCardID().matches(pattern)) {
                EventCard event = (EventCard) card;
                /*System.out.println(event.getCardID());
                System.out.println(event.getEventType());*/
                if (event.getCardID().matches(ritualPattern)) {
                    assertEquals(EventType.SHAMANIC_RITUAL, ((EventCard) card).getEventType());
                } else if (event.getCardID().matches(sustenancePattern)) {
                    assertEquals(EventType.SUSTENANCE, ((EventCard) card).getEventType());
                } else if (event.getCardID().matches(paintingsPattern)) {
                    assertEquals(EventType.CAVE_PAINTINGS, ((EventCard) card).getEventType());
                } else if (event.getCardID().matches(huntPattern)) {
                    assertEquals(EventType.HUNT, ((EventCard) card).getEventType());
                }
            }
        }
    }

    @Test
    public void drawCardTest(){
        /*Game game = new Game(2, 3);
        game.addPlayer("mickey");
        game.addPlayer("duffy");
        game.addPlayer("goofy");
        game.startGame();
        Deck deck = game.getDeck();*/
        initDeck(4);
        /*Card card =*/ deck.drawCard();
        /*while(card.getEra() != 3){
            card = deck.drawCard();
        }*/
    }
}