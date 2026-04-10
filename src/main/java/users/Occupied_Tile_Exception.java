package users;

public class Occupied_Tile_Exception extends RuntimeException {
    public Occupied_Tile_Exception() {
        super("Tile occupata");
    }
}
