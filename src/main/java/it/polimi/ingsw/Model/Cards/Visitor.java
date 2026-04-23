package it.polimi.ingsw.Model.Cards;

import it.polimi.ingsw.Model.Cards.Buildings.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Users.Player;

import java.util.ArrayList;

/**
 * Visitor interface uses visitor Pattern in order to let
 * room for future additions of new methods, used by the three types
 * of cards but in different ways.
 */
public interface Visitor {
    void visitCard(BuildingCard building, Player player);
    void visitCard(CharacterCard character, Player player);
    void visitCard(EventCard event, Player player);

    void visitCard(BuildingCard building, ArrayList<Card> array);
    void visitCard(CharacterCard character, ArrayList<Card> array);
    void visitCard(EventCard event, ArrayList<Card> array);
}
