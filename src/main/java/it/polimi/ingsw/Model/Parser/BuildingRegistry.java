package it.polimi.ingsw.Model.Parser;

import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Model.BuildingsManagement.Buildings.*;
import it.polimi.ingsw.Model.Cards.BuildingCard;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.EnumMap;

public class BuildingRegistry {
    private static final EnumMap<Effect, Class<? extends BuildingCard>> registry = new EnumMap<>(Effect.class);

    public BuildingRegistry(){
        registry.put(Effect.ARTISTS_FOOD, ArtistsFood.class);
        registry.put(Effect.BONUS_POINTS, BonusPoints.class);
        registry.put(Effect.BONUS_STARS, BonusStars.class);
        registry.put(Effect.COMBO_FOOD, ComboFood.class);
        registry.put(Effect.COMBO_HUNTERS, ComboHunters.class);
        registry.put(Effect.COMBO_POINTS, ComboPoints.class);
        registry.put(Effect.DRAW_CARD, DrawAdditionalCard.class);
        registry.put(Effect.INVENTORS_FOOD, InventorsFood.class);
        registry.put(Effect.MULTIPLIER_RITUAL, MultiBonusRitual.class);
        registry.put(Effect.MULTIPLIER_BUILDER, MultiPointsBuilder.class);
        registry.put(Effect.NO_MALUS_RITUAL, NoMalusRitual.class);
        registry.put(Effect.POINTS_PER_ROLE, PointsPerRole.class);
        registry.put(Effect.SUSTENANCE_DISCOUNT, SustenanceDiscount.class);
        registry.put(Effect.TOTEM_FOOD, TotemFood.class);
    }
    //mainly used for testing and future implementation
    public void addBuildingType(Effect effect, Class<? extends BuildingCard> buildingType) {
        registry.put(effect, buildingType);
    }

    public BuildingCard createBuilding(BuildingCardDTO buildingData) throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<? extends BuildingCard> buildingType = registry.get(buildingData.effect);
        if(buildingType == null){
            throw new IllegalArgumentException("Building not identified");
        }
        Constructor<? extends BuildingCard> constructor = buildingType.getConstructor(BuildingCardDTO.class);
        return constructor.newInstance(buildingData);
    }
}