package it.polimi.ingsw.Model.BuildingsManagement.Buildings;

import it.polimi.ingsw.Enums.Parameters;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Model.BuildingsManagement.EffectContext;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.EventManagement.CavePaintingsEvent;
import it.polimi.ingsw.Model.EventManagement.EventStrategy;
import it.polimi.ingsw.Model.Parser.BuildingCardDTO;

// During the Cave Paintings event, the owner takes 1 food token for every
// artist in his tribe
public class ArtistsFood extends BuildingCard {
    private final int foodBonus;

    public ArtistsFood(int era, String cardID, int cost, GamePhase activatedAt, Effect effect,
                       String effectDescription, int prestige, int foodBonus) {
        super(era, cardID, cost, activatedAt, effect, effectDescription, prestige);
        this.foodBonus = foodBonus;
    }
    public ArtistsFood(BuildingCardDTO buildingData){
        super(buildingData.era, buildingData.cardID, buildingData.cost,
                buildingData.activatedAt, buildingData.effect, buildingData.effectDescription,
                buildingData.prestige);
        this.foodBonus = buildingData.foodBonus;
    }
    @Override
    public int getFoodBonus(){
        return this.foodBonus;
    }

    @Override
    public void applyEffect(EffectContext context){
        int artistsNumber = context.getPlayer().getTribe().getArtistsNumber();
        int currentFoodBonus = context.getParam(Parameters.FOOD_BONUS);
        context.putParam(Parameters.FOOD_BONUS, artistsNumber*this.foodBonus + currentFoodBonus);
    }

    @Override
    public boolean isUsedIn(Class<? extends EventStrategy> eventType) {
        return eventType.equals(CavePaintingsEvent.class);
    }
}
