package it.polimi.ingsw;

import static org.junit.jupiter.api.Assertions.assertEquals;

import cards_and_deck.BuildingCard;
import org.junit.jupiter.api.*;
import cards_and_deck.CharacterCard;
import cards_and_deck.CardLoader;

import java.util.ArrayList;
import java.util.List;

public class BuildingCardTest {
    private List<BuildingCard> allBuildingCards;
    CardLoader loader = new CardLoader();

    @Test
    public void initBuildings(){
        this.allBuildingCards = loader.loadBuildings();
        if(!allBuildingCards.isEmpty()){
            System.out.println(this.allBuildingCards.get(0).getCardID());
            assertEquals("E1_B_1", this.allBuildingCards.get(0).getCardID());
        }
    }
}
