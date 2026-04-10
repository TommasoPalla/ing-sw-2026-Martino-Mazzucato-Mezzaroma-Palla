package users;

import enums.Color;
import game_board.OfferTile;
import cards_and_deck.CharacterCard;
import cards_and_deck.BuildingCard;
import enums.GamePhase;

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
    public int getFinalPoints(){return this.calculateFinalPoints();}


    //functions
    public int calculateFinalPoints(){

        int tribePoints=tribe.getPrestigePoints();
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
        int endGamePoints=0;
        for(BuildingCard building : tribe.getBuildings()){
            if(building.getActivatedAt()==GamePhase.END_GAME){
                //if raddoppia punti builder populationPoints*2
                //if da punti per ogni character di un ruolo
                    /*for(CharacterCard character: tribe.getPopulation()){
                        if(character.getRole()=='quello del building'{
                            tribePoints+=#character così*numero
                        }

                      }
                     */

            }
        }
        return (tribePoints+artistsPoints+populationPoints+buildingPoints+inventorsPoints+endGamePoints);

    }
    /*funzione che dipende da controller anche che è ancora da implementare, qui bozza sbagliata ma circa completa*/
    public void chooseOfferTile(/*OfferTile chosen*/) throws Occupied_Tile_Exception{
        /*logica di input*/
        //OfferTile chosen = getChosenTile();
        OfferTile chosen = null;
        if(chosen.isOccupied()){
            throw new Occupied_Tile_Exception();
        }
        else{
            chosen.occupy(this);
        }
    }

    public void drawFromTopRow(){

    }
    public void drawFromBottomRow(){

    }
}
