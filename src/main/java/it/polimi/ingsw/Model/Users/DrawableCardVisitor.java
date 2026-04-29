package it.polimi.ingsw.Model.Users;

import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Cards.EventCard;
import it.polimi.ingsw.Model.Cards.VisitorAdapter;

/**Implementation of Visitor Pattern to verify whether a card can be drawn by a player,
 * an EventCard cannot be drawn,
 * a CharacterCard can always be drawn,
 * a BuildingCard can be drawn only if the player can afford it.
 */
public class DrawableCardVisitor extends VisitorAdapter {
    private boolean drawable = false;

    public boolean isDrawable(){
        return drawable;
    }

    @Override
    public void visitCard(BuildingCard building, Player player) {
        int discountedCost = building.getCost() - player.getTribe().getGatherersDiscount();
        int foodReserve = player.getTribe().getFoodReserve();
        if (foodReserve >= discountedCost) {
            drawable = true;
        } else {
            drawable = false;
            throw new Insufficient_Food_Exception();
        }
    }
    @Override
    public void visitCard(CharacterCard character, Player player){
    drawable = true;
    }

    @Override
    public void visitCard(EventCard event, Player player) {
        drawable = false;
        throw new Illegal_Draw_Exception();
    }
}
