package it.polimi.ingsw.Model.Users;

import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Model.Cards.VisitorAdapter;
import it.polimi.ingsw.Model.ClientModel;

/**Implementation of Visitor Pattern to verify whether a card can be drawn by a player,
 * an EventCard cannot be drawn,
 * a CharacterCard can always be drawn,
 * a BuildingCard can be drawn only if the player can afford it.
 */
public class DrawableCardVisitor extends VisitorAdapter {
    private boolean drawable;
    private final boolean isLightModel;
    private final ClientModel model;
    private final Player player;

    public DrawableCardVisitor(ClientModel lightModel){
        drawable = false;
        isLightModel = true;
        model = lightModel;
        player = null;
    }
    public DrawableCardVisitor(Player player){
        drawable = false;
        isLightModel = false;
        model = null;
        this.player = player;
    }

    public boolean isDrawable(){
        return drawable;
    }

    @Override
    public void visitCard(BuildingCard building) {
        int discountedCost = building.getCost();
        int foodReserve;
        if(isLightModel){
            //non super safe, ma questo visitor è chiamato in ClientModel solo se è verificato
            //che il giocatore a pescare è il current
            String currPlayer = model.getCurrentPlayer();
            discountedCost -= model.getPlayerTribe(currPlayer).getBuilderDiscount();
            foodReserve = model.getPlayerTribe(currPlayer).getFoodReserve();
        } else {
            discountedCost -= player.getTribe().getBuilderDiscount();
            foodReserve = player.getTribe().getFoodReserve();
        }
        if (foodReserve >= discountedCost) {
            drawable = true;
        } else {
            drawable = false;
            throw new InsufficientFoodException();
        }
    }
    @Override
    public void visitCard(CharacterCard character){
    drawable = true;
    }

    @Override
    public void visitCard(EventCard event) {
        drawable = false;
        throw new IllegalDrawException();
    }
}
