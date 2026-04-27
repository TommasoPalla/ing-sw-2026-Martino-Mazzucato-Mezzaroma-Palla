package it.polimi.ingsw.Model;

import it.polimi.ingsw.Model.Cards.Buildings.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Cards.VisitorAdapter;
import it.polimi.ingsw.Model.Users.Player;

import java.util.ArrayList;

public class VisitorModel extends VisitorAdapter {

    @Override
    public void visitCard(BuildingCard building, ClientModel localModel) {
        localModel.updateTopRow(building);
    }

    @Override
    public void visitCard(CharacterCard character, ClientModel localModel, String id) {
        localModel.getTopRow().remove(character);
        ArrayList<Card> arr = localModel.getTopRow();
        localModel.updateTopRow(arr);
        localModel.getPlayerTribe(id).addToPopulation(character);//esempio

    }

}
