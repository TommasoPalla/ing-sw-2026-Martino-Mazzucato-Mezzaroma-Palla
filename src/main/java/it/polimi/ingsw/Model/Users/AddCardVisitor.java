package it.polimi.ingsw.Model.Users;

import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Cards.VisitorAdapter;

/**Implementation of Visitor Pattern to add a card to a player's tribe, both got as inputs.
 */
public class AddCardVisitor extends VisitorAdapter {
    @Override
    public void visitCard(BuildingCard building, Player player) {
        player.getTribe().addBuildingToTribe(building);
    }

    @Override
    public void visitCard(CharacterCard character, Player player) {
        player.getTribe().addCharacterToTribe(character);
    }
}
