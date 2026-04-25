package it.polimi.ingsw.Model.Cards.Buildings;

import it.polimi.ingsw.Enums.Effect;

import java.util.EnumMap;
import java.util.function.Supplier;

public class BuildingRegistry {
    private static final EnumMap<Effect, Supplier<BuildingCard>> registry = new EnumMap<>(Effect.class);

    public static void addBuilding(Effect effect, Supplier<BuildingCard> supplier) {
        registry.put(effect, supplier);
    }
    public static BuildingCard create(Effect effect){
        Supplier<BuildingCard> supplier = registry.get(effect);
        if(supplier == null) {
            throw new IllegalArgumentException("JSON problem: building effect not supported"+ effect);
        }
        return supplier.get();
    }
}
