package it.polimi.ingsw.Model.Users;

public class Occupied_Tile_Exception extends RuntimeException {
    public Occupied_Tile_Exception() {
        super("Tile occupata");
    }
}
