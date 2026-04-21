package it.polimi.ingsw;

import enums.Color;
import game.Game;
import game_board.OfferTile;
import game_board.OfferTrack;
import game_board.TurnTile;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

import cards_and_deck.*;
import org.junit.jupiter.api.*;
import users.Player;

import java.util.ArrayList;

public class TurnTileTest {

    @Test
    public void initialize(){
        int numPlayers;
        int[] solution;
        for(int k = 2; k <= 5; k++) {
            numPlayers = k;
            TurnTile turnTile = new TurnTile(numPlayers);
            switch (numPlayers) {
                case 2:
                    solution = new int[]{1, -1};
                    for (int i = 0; i < solution.length; i++) {
                        assertEquals(solution[i], turnTile.getTileModifier()[i]);
                    }
                    System.out.println("Passed initialization test with " + k + " players");
                    break;
                case 3:
                    solution = new int[]{2, 0, -1};
                    for (int i = 0; i < solution.length; i++) {
                        assertEquals(solution[i], turnTile.getTileModifier()[i]);
                    }
                    System.out.println("Passed initialization test with " + k + " players");
                    break;
                case 4:
                    solution = new int[]{2, 1, 0, -1};
                    for (int i = 0; i < solution.length; i++) {
                        assertEquals(solution[i], turnTile.getTileModifier()[i]);
                    }
                    System.out.println("Passed initialization test with " + k + " players");
                    break;
                case 5:
                    solution = new int[]{3, 1, 0, 0, -1};
                    for (int i = 0; i < solution.length; i++) {
                        assertEquals(solution[i], turnTile.getTileModifier()[i]);
                    }
                    System.out.println("Passed initialization test with " + k + " players");
                    break;
            }
        }
    }

    @Nested
    class testFunctions{
        Player player1;
        Player player2;
        Player player3;
        ArrayList<Player> turnOrder;
        OfferTrack offerTrack;
        TurnTile turnTile;
        Game game;
        @BeforeEach
        public void setup(){
            player1 = new Player("1", Color.RED);
            player2 = new Player("2", Color.BLUE);
            player3 = new Player("3", Color.WHITE);
            turnOrder = new ArrayList<>();
            turnOrder.add(player3);
            turnOrder.add(player1);
            turnOrder.add(player2);
            offerTrack = new OfferTrack(turnOrder.size());
            player1.chooseOfferTile(2, offerTrack);
            player2.chooseOfferTile(1, offerTrack);
            player3.chooseOfferTile(3, offerTrack);
            turnTile = new TurnTile(turnOrder.size());
            game = new Game(turnOrder);

            //in this scenario player1 chose to go on tile 2 and then player2 chose to go on tile 1
            //meaning that the next turn player 2 will play before player 1
            //player 3 was last and will still be last

            // chooseOfferTile functionally is useless in the returnToStart testcase
            // it has to be here just because currentOfferTile().free() would return null otherwise
        }


        @Test
        public void turnOrderTest(){
            ArrayList<Player> newTurnOrder = turnTile.getTurnOrder(turnOrder);
            ArrayList<Player> correctTurnOrder = new ArrayList<>();
            correctTurnOrder.add(player2);
            correctTurnOrder.add(player1);
            correctTurnOrder.add(player3);
            for(int i = 0; i < turnOrder.size(); i++){
                assertEquals(correctTurnOrder.get(i), newTurnOrder.get(i));
            }
        }

        @Test
        public void returnToStartTest(){
            for(Player player : turnOrder){
                turnTile.returnToStartingTile(turnOrder, player, game.getBuildingManager());
            }
            assertEquals(2, player3.getTribe().getFoodReserve());       //player3 was the first => he got 2 food
            assertEquals(0, player1.getTribe().getFoodReserve());       //player2 the second => he got 0 food
            assertEquals(0, player2.getTribe().getFoodReserve());       //player1 the last => he got 0 food
            assertEquals(-1, player2.getTribe().getPrestigePoints());   //and since he had no PP, he now has -1PP
        }

    }

}
