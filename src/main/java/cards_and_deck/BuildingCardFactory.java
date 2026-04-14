package cards_and_deck;
import building_management.buildings.*;
import enums.Effect;

public class BuildingCardFactory {
    public static BuildingCard createBuilding(BuildingCardDTO dto) throws IllegalArgumentException{
        switch(dto.effect){
            case Effect.ARTISTS_FOOD:
                return new ArtistsFood(dto.era, dto.cardID, dto.cost, dto.activatedAt,
                        dto.effect, dto.effectDescription, dto.prestige, dto.foodBonus);
            case Effect.BONUS_POINTS:
                return new BonusPoints(dto.era, dto.cardID, dto.cost, dto.activatedAt,
                        dto.effect, dto.effectDescription, dto.prestigeBonus);
            case Effect.BONUS_STARS:
                return new BonusStars(dto.era, dto.cardID, dto.cost, dto.activatedAt,
                        dto.effect, dto.effectDescription, dto.prestige, dto.starBonus);
            case Effect.COMBO_FOOD:
                return new ComboFood(dto.era, dto.cardID, dto.cost, dto.activatedAt,
                        dto.effect, dto.effectDescription, dto.prestige, dto.foodBonus);
            case Effect.COMBO_HUNTERS:
                return new ComboHunters(dto.era, dto.cardID, dto.cost, dto.activatedAt,
                        dto.effect, dto.effectDescription, dto.prestige, dto.foodBonus, dto.prestigeBonus);
            case Effect.COMBO_POINTS:
                return new ComboPoints(dto.era, dto.cardID, dto.cost, dto.activatedAt,
                        dto.effect, dto.effectDescription, dto.prestige, dto.prestigeBonus);
            case Effect.MULTIPLIER_RITUAL:
                return new MultiBonusRitual(dto.era, dto.cardID, dto.cost, dto.activatedAt,
                        dto.effect, dto.effectDescription, dto.multiplier);
            case Effect.MULTIPLIER_BUILDER:
                return new MultiPointsBuilder(dto.era, dto.cardID, dto.cost, dto.activatedAt,
                        dto.effect, dto.effectDescription, dto.prestige, dto.multiplier);
            case Effect.DRAW_CARD:
                return new DrawAdditionalCard(dto.era, dto.cardID, dto.cost, dto.activatedAt,
                        dto.effect, dto.effectDescription, dto.prestige);
            case Effect.INVENTORS_FOOD:
                return new InventorsFood(dto.era, dto.cardID, dto.cost, dto.activatedAt,
                        dto.effect, dto.effectDescription, dto.prestige, dto.foodBonus);
            case Effect.NO_MALUS_RITUAL:
                return new NoMalusRitual(dto.era, dto.cardID, dto.cost, dto.activatedAt,
                        dto.effect, dto.effectDescription, dto.prestige);
            case Effect.POINTS_PER_ROLE:
                return new PointsPerRole(dto.era, dto.cardID, dto.cost, dto.activatedAt,
                        dto.effect, dto.effectDescription, dto.prestige, dto.roleEffect, dto.prestigeBonus);
            case Effect.SUSTENANCE_DISCOUNT:
                return new SustenanceDiscount(dto.era, dto.cardID, dto.cost, dto.activatedAt,
                        dto.effect, dto.effectDescription, dto.prestige, dto.roleEffect, dto.foodDiscount);
            case Effect.TOTEM_FOOD:
                return new TotemFood(dto.era, dto.cardID, dto.cost, dto.activatedAt,
                        dto.effect, dto.effectDescription, dto.prestige, dto.foodBonus);
            default:
                throw new IllegalArgumentException("BuildingCard type does not exist\n");
        }

    }
}
