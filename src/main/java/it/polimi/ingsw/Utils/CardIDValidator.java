package it.polimi.ingsw.Utils;

import it.polimi.ingsw.Controller.ClientController.ClientController;
import it.polimi.ingsw.Model.Cards.BuildingCard;

import java.util.ArrayList;

/**
 * This class is used to ensure that the CardID
 * entered by the user is valid. If it is, the
 * corresponding card info will be shown
 */
public class CardIDValidator {
    private final ClientController clientController;

    public CardIDValidator(ClientController clientController){
        this.clientController = clientController;
    }

    public boolean checkCardID(String cardID){
        if(cardID == null || cardID.isEmpty())
            return false;

        ArrayList<BuildingCard> topBuildings = clientController.getLocalModel().getTopBuildings();
        ArrayList<BuildingCard> bottomBuildings = clientController.getLocalModel().getBottomBuildings();

        //if at least one matches (in either bottom row or top row) return true immediately
        if(topBuildings != null){
            for(BuildingCard building : topBuildings){
                if(building != null && cardID.equalsIgnoreCase(building.getCardID()))
                    return true;
            }
        }

        if(bottomBuildings != null){
            for(BuildingCard building : bottomBuildings){
                if(building != null && cardID.equalsIgnoreCase(building.getCardID()))
                    return true;
            }
        }

        return false;
    }
}