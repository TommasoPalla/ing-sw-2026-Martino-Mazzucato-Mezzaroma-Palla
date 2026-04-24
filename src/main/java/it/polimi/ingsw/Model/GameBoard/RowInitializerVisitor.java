package it.polimi.ingsw.Model.GameBoard;

import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Model.Cards.VisitorAdapter;

import java.util.ArrayList;

public class RowInitializerVisitor extends VisitorAdapter {
    @Override
    public void visitCard(CharacterCard character, OfferTrack offerTrack) {
        offerTrack.getBottomRow().add(character);
    }
    @Override
    public void visitCard(EventCard event, OfferTrack offerTrack){
        offerTrack.getBottomRow().add(event);
    }
}
