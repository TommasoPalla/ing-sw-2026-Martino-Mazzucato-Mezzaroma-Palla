package it.polimi.ingsw.Model.Users;

public class OccupiedTileException extends RuntimeException {
    public OccupiedTileException() {
        super("Tile occupata");
    }
}
