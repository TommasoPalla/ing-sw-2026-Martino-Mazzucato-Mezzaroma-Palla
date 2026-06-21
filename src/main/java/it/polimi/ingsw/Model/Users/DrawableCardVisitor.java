package it.polimi.ingsw.Model.Users;

import it.polimi.ingsw.CustomException.IllegalDrawException;
import it.polimi.ingsw.CustomException.InsufficientFoodException;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Utils.VisitorAdapter;
import it.polimi.ingsw.Controller.ClientController.ClientModel;

/**
 * Implementation of Visitor Pattern to verify whether a card can be drawn by a player,
 * an EventCard cannot be drawn,
 * a CharacterCard can always be drawn,
 * a BuildingCard can be drawn only if the player can afford it.
 */
//throws exception if card isn't drawable, otherwise does nothing
public class DrawableCardVisitor extends VisitorAdapter {
    private final boolean isLightModel;
    private final ClientModel model;
    private final Player player;

    public DrawableCardVisitor(ClientModel lightModel){
        isLightModel = true;
        model = lightModel;
        player = null;
    }
    public DrawableCardVisitor(Player player){
        isLightModel = false;
        model = null;
        this.player = player;
    }

    @Override
    public void visitCard(BuildingCard building) {
        int discountedCost = building.getCost();
        int foodReserve;
        if(isLightModel){
            String currPlayer = model.getCurrentPlayer();
            discountedCost -= model.getPlayerTribe(currPlayer).getBuildersDiscount();
            foodReserve = model.getPlayerTribe(currPlayer).getFoodReserve();
        } else {
            discountedCost -= player.getTribe().getBuildersDiscount();
            foodReserve = player.getTribe().getFoodReserve();
        }
        if (foodReserve < discountedCost) {
            throw new InsufficientFoodException();
        }
    }
    //visit Character does nothing

    @Override
    public void visitCard(EventCard event) {
        throw new IllegalDrawException("You can't draw an Event card");
    }
}