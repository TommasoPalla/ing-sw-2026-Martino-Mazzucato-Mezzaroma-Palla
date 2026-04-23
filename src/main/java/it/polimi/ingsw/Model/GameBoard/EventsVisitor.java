package it.polimi.ingsw.Model.GameBoard;

import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Model.Cards.VisitorAdapter;
import java.util.ArrayList;

public class EventsVisitor extends VisitorAdapter {
    @Override
    public void visitCard(EventCard event, ArrayList<Card> array){
        array.add(event);
    }
}
