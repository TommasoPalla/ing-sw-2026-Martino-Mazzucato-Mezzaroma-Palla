package users;

import enums.Color;
import game_board.OfferTile;

public class Player {
    private String name;
    private Color totemColor;
    private Tribe tribe;
    private OfferTile currentOfferTile;

    //getters
    public String getName() {return name;}
    public Color getTotemColor() {return totemColor;}
    public Tribe getTribe() {return tribe;}
    public OfferTile getCurrentOfferTile() {return currentOfferTile;}
}
