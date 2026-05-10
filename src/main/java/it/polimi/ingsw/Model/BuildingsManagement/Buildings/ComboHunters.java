package it.polimi.ingsw.Model.BuildingsManagement.Buildings;

import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.Parameters;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.BuildingsManagement.EffectContext;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.EventManagement.EventStrategy;
import it.polimi.ingsw.Model.EventManagement.HuntEvent;
import it.polimi.ingsw.Model.Parser.BuildingCardDTO;

/**
 * During the Hunt event, the owner of the building takes 'foodBonus' Food token
 * and gains 'prestigeBonus' additional Prestige Point for each Hunter in his tribe
 */
public class ComboHunters extends BuildingCard {

    private final int foodBonus;
    private final int prestigeBonus;

    public ComboHunters(int era, String cardID, int cost, GamePhase activatedAt, Effect effect,
                        String effectDescription, int prestige, int foodBonus, int prestigeBonus) {
        super(era, cardID, cost, activatedAt, effect, effectDescription, prestige);
        this.foodBonus = foodBonus;
        this.prestigeBonus = prestigeBonus;
    }
    public ComboHunters(BuildingCardDTO buildingData){
        super(buildingData.era, buildingData.cardID, buildingData.cost,
                buildingData.activatedAt, buildingData.effect, buildingData.effectDescription,
                buildingData.prestige);
        this.foodBonus = buildingData.foodBonus;
        this.prestigeBonus = buildingData.prestigeBonus;
    }
    @Override
    public int getFoodBonus(){
        return this.foodBonus;
    }
    @Override
    public int getPrestigeBonus(){
        return this.prestigeBonus;
    }

    @Override
    public void applyEffect(EffectContext context) {
        int initialFoodBonus = context.getParam(Parameters.FOOD_BONUS);
        int initialPrestigeBonus = context.getParam(Parameters.PRESTIGE_BONUS);

        int huntersNumber = context.getPlayer().getTribe().getHuntersNumber();
        context.putParam(Parameters.FOOD_BONUS, huntersNumber*this.foodBonus + initialFoodBonus);
        context.putParam(Parameters.PRESTIGE_BONUS, huntersNumber*this.prestigeBonus + initialPrestigeBonus);
    }

    @Override
    public boolean isUsedIn(Class<? extends EventStrategy> eventType) {
        return eventType.equals(HuntEvent.class);
    }
}
