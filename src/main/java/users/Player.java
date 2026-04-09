package users;

import enums.Color;
import game_board.OfferTile;
import cards_and_deck.CharacterCard;
import cards_and_deck.BuildingCard;

public class Player {
    private String name;
    private Color totemColor;
    private Tribe tribe;
    private OfferTile currentOfferTile;

    public Player(String name, Tribe tribe, Color totemColor){
        this.name=name;
        this.tribe=tribe;
        this.totemColor=totemColor;
        this.currentOfferTile=null;
    }

    //getters
    public String getName() {return name;}
    public Color getTotemColor() {return totemColor;}
    public Tribe getTribe() {return tribe;}
    public OfferTile getCurrentOfferTile() {return currentOfferTile;}

    //functions
    public int calculateFinalPoints(){

        int populationPoints = 0;
        for(CharacterCard character: tribe.getPopulation()){
            populationPoints=populationPoints+character.getPrestigePoints().orElse(0);
        }
        int buildingPoints=0;
        for(BuildingCard building : tribe.getBuildings()){
            buildingPoints+=building.getPrestige();
        }
        int artistsPoints = (tribe.getArtistsNumber()/2)*10;
        int inventorsPoints=5;
        return (tribe.getPrestigePoints()+artistsPoints+populationPoints+buildingPoints);

    }
}
