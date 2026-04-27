package it.polimi.ingsw.Model;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.InventorType;
import it.polimi.ingsw.Model.Cards.Buildings.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

public class LightTribe {
    public String idPlayer; // o name se è unico
    public int prestigePoints;
    public int foodReserve;
    public int populationSize;
    public Map<CharacterRole, ArrayList<CharacterCard>> population;
    public ArrayList<BuildingCard> buildings;
    private EnumMap<InventorType, Integer> inventorsPerType;
    private int builderDiscount;
    private int gatherersDiscount;
    private int shamansStars;
    private int turnTilePosition;


    public LightTribe(String id) {
        this.idPlayer = id;
        this.prestigePoints = 0;
        this.foodReserve = 0;
        this.population = new EnumMap<>(CharacterRole.class);
        for(CharacterRole role : CharacterRole.values()){
            population.put(role, new ArrayList<>());
        }
        this.buildings = new ArrayList<>();
        this.inventorsPerType = new EnumMap<>(InventorType.class);
        this.builderDiscount=0;
        this.gatherersDiscount=0;
        this.shamansStars=0;
    }

    public void addShamansStars(int stars) {shamansStars += stars;}
    public void addFood(int food) {foodReserve += food;}
    public void addPrestigePoints(int prestigePoints) {prestigePoints += prestigePoints;}
    public void addToPopulation(Card card){
        population.put(card);//visitor sul type
    }




}

