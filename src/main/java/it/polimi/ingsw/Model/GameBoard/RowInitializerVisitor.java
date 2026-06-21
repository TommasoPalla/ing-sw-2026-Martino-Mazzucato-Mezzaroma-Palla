package it.polimi.ingsw.Model.GameBoard;

import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Utils.VisitorAdapter;


/**
 * Implementation of Visitor Pattern that is used by OfferTrack.initializeBottomRow,
 * to organize new drawn cards: characters to the bottomRow and Events to the topRow.
 */
public class RowInitializerVisitor extends VisitorAdapter {
    private final OfferTrack offerTrack;

    public RowInitializerVisitor(OfferTrack offerTrack){
        this.offerTrack = offerTrack;
    }

    @Override
    public void visitCard(CharacterCard character) {
        offerTrack.getBottomRow().add(character);
    }
    @Override
    public void visitCard(EventCard event){
        offerTrack.getTopRow().add(event);
    }
}