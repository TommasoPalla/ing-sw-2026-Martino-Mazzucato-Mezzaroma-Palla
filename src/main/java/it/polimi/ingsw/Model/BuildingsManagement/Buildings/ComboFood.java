package it.polimi.ingsw.Model.BuildingsManagement.Buildings;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.Effect;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Parser.BuildingCardDTO;

import java.util.HashMap;
import java.util.Map;

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

    // When the player purchases the building, initializes the variable currentSetsNumber
    // with the number of sets already completed in their tribe
    @Override
    public void effectOnPurchase() {
        Map<CharacterRole, Integer> occurrencesPerRole = new HashMap<>();
        for (CharacterRole role : this.getOwner().getTribe().getPopulation().keySet()) {
            occurrencesPerRole.put(role, this.getOwner().getTribe().getPopulation().get(role).size());
        }
        if(occurrencesPerRole.size() == 6) {
            this.currentSetsNumber = occurrencesPerRole.values()
                    .stream()
                    .min(Integer::compare)
                    .orElse(0);
        }
        else { this.currentSetsNumber = 0; }
    }

    @Override
    public void applyEffect() {
        // Maps every character's type to the number of its occurrences in the player's tribe
        Map<CharacterRole, Integer> occurrencesPerRole = new HashMap<>();
        for (CharacterRole role : this.getOwner().getTribe().getPopulation().keySet()) {
            occurrencesPerRole.put(role, this.getOwner().getTribe().getPopulation().get(role).size());
        }
        // if there's at least one occurrence of every character's role, it extracts the
        // value of the role with fewer occurrences (the number of sets),
        // and if the number of sets has increased, the player takes 5 Food tokens
        if(occurrencesPerRole.size() == 6) {
            long setsNumber = occurrencesPerRole.values()
                    .stream()
                    .min(Integer::compare)
                    .orElse(0);
            if(setsNumber > this.currentSetsNumber) {
                this.getOwner().getTribe().modifyFood(this.foodBonus);
                currentSetsNumber++;
            }
        }
    }
}

