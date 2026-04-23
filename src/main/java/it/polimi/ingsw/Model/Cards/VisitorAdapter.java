package it.polimi.ingsw.Model.Cards;

import it.polimi.ingsw.Model.Cards.Buildings.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Users.Player;

/**
 * class VisitorAdapter overrides visit methods with blank bodies
 * to avoid further useless ones:
 * for each new Visitor class only the visit methods that are actually needed
 * will be re-defined
 */
public abstract class VisitorAdapter implements Visitor{
    @Override
    public void visitCard(BuildingCard building, Player player){}
    @Override
    public void visitCard(CharacterCard character, Player player){}
    @Override
    public void visitCard(EventCard event, Player player){}
}