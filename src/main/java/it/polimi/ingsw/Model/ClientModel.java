package it.polimi.ingsw.Model;

import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.Buildings.BuildingCard;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.Model.GameBoard.OfferTrack;
import it.polimi.ingsw.Model.Users.Player;
import it.polimi.ingsw.Networking.Client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
/*
* Light model for the client to be accessed. Updated when the model state is modified
 */
public class ClientModel {
    public GamePhase currentPhase;
    public Player currentPlayerTurn;
    public OfferTrack offerTrack;
    public int foodReserve;
    public int prestigePoints;
    public Map<CharacterRole, ArrayList<CharacterCard>> population;
    public ArrayList<BuildingCard> buildings;

    public ClientModel(Game realModel) {
        this.currentPhase = realModel.getGamePhase();
        this.currentPlayerTurn = realModel.getCurrentPlayer();
        this.offerTrack = realModel.getOfferTrack();
        this.foodReserve = 0;
        this.prestigePoints = 0;
        this.population = new HashMap<>();
        this.buildings = new ArrayList<>();
    }

    public boolean drawable(int index, int row) {}

    public boolean isOccupied(int index) { return offerTrack.getOfferTiles().get(index).isOccupied(); }
}