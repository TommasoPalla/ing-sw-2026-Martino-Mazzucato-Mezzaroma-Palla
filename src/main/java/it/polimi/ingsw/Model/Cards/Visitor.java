package it.polimi.ingsw.Model.Cards;

import it.polimi.ingsw.Model.Cards.Buildings.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.ClientModel;
import it.polimi.ingsw.Model.GameBoard.OfferTrack;
import it.polimi.ingsw.Model.Users.Player;


/**
 * Visitor interface uses visitor Pattern in order to let
 * room for future additions of new methods, used by the three types
 * of cards but in different ways.
 */
public interface Visitor {
    void visitCard(BuildingCard building, Player player);
    void visitCard(CharacterCard character, Player player);
    void visitCard(EventCard event, Player player);

    void visitCard(BuildingCard building, OfferTrack offerTrack);
    void visitCard(CharacterCard character, OfferTrack offerTrack);
    void visitCard(EventCard event, OfferTrack offerTrack);

    void visitCard(BuildingCard building, ClientModel localModel);
    void visitCard(CharacterCard character, ClientModel localModel);
    void visitCard(EventCard event, ClientModel localModel);
}
