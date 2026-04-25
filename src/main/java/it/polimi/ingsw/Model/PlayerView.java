package it.polimi.ingsw.Model;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Model.Cards.Buildings.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;

import java.util.ArrayList;
import java.util.Map;

public class PlayerView {
    public String id; // o name se è unico
    public int prestigePoints;
    public int foodReserve;
    public int populationSize;
    public Map<CharacterRole, ArrayList<CharacterCard>> population;
    public ArrayList<BuildingCard> buildings;

    public PlayerView(String id) {
        this.id = id;
    }
}
