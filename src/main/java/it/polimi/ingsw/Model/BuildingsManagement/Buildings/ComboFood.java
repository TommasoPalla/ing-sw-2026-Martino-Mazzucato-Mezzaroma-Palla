package it.polimi.ingsw.Model.BuildingsManagement.Buildings;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Parser.BuildingCardDTO;
import it.polimi.ingsw.Model.Users.TribeInterface;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ComboFood extends BuildingCard {
    private final int foodBonus;
    long currentSetsNumber = 0;

    //constructor mainly used for testing
    public ComboFood(int era, String cardID, int cost, GamePhase activatedAt, Effect effect,
                     String effectDescription, int prestige, int foodBonus) {
        super(era, cardID, cost, activatedAt, effect, effectDescription, prestige);
        this.foodBonus = foodBonus;
    }
    //constructor used to parse JSON data
    public ComboFood(BuildingCardDTO buildingData){
        super(buildingData.era, buildingData.cardID, buildingData.cost,
                buildingData.activatedAt, buildingData.effect, buildingData.effectDescription,
                buildingData.prestige);
        this.foodBonus = buildingData.foodBonus;
    }

    @Override
    public int getFoodBonus(){
        return this.foodBonus;
    }

    /**When the player purchases the building, initializes the variable currentSetsNumber
     * with the number of sets already completed in their tribe
     */
    @Override
    public void effectOnPurchase(TribeInterface tribe) {
        this.currentSetsNumber = tribe.getPopulation().entrySet().stream()
                .filter(entry -> entry.getKey() != CharacterRole.NONE)
                .mapToInt(entry -> entry.getValue().size())
                .min()
                .orElse(0);
    }

    @Override
    public void applyEffect() {
        int setsNumber = this.getOwner().getTribe().getPopulation().entrySet().stream()
                .filter(entry -> entry.getKey() != CharacterRole.NONE)
                .mapToInt(entry -> entry.getValue().size())
                .min()
                .orElse(0);
            if(setsNumber > this.currentSetsNumber) {
                this.getOwner().getTribe().modifyFood(this.foodBonus);
                currentSetsNumber = setsNumber;
            }
    }
}

