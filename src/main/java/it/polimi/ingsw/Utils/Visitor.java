package it.polimi.ingsw.Utils;

import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Cards.EventCard;

/**
 * Visitor interface uses visitor Pattern in order to let
 * room for future additions of new methods, used by the three types
 * of cards but in different ways.
 */
public interface Visitor {
    void visitCard(BuildingCard building);
    void visitCard(CharacterCard character);
    void visitCard(EventCard event);
}
