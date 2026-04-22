package it.polimi.ingsw.Model.Cards;

import it.polimi.ingsw.Model.Cards.Buildings.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;

/**
 * class VisitorAdapter overrides visit methods with blank bodies
 * to avoid further useless ones:
 * for each new Visitor object only the visit methods that are actually needed
 * will be re-defined
 */
public abstract class CardVisitor implements Visitor{
    @Override
    public void visitCard(BuildingCard building){}
    @Override
    public void visitCard(CharacterCard character){}
    @Override
    public void visitCard(EventCard event){}
}