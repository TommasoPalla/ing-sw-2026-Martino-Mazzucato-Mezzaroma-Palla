package it.polimi.ingsw.Utils;

import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Cards.EventCard;


/**
 * class VisitorAdapter overrides visit methods with blank bodies
 * to avoid further useless ones:
 * for each new Visitor class only the visit methods that are actually needed
 * will be re-defined
 */
public abstract class VisitorAdapter implements Visitor{
    @Override
    public void visitCard(BuildingCard building){}
    @Override
    public void visitCard(CharacterCard character){}
    @Override
    public void visitCard(EventCard event){}
}