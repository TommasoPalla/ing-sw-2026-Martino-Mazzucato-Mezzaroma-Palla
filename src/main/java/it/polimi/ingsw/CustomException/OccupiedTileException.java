package it.polimi.ingsw.CustomException;

//goes under InvalidSelection
public class OccupiedTileException extends RuntimeException {
    public OccupiedTileException() {
        super("ERROR: this offer tile is already occupied. Choose another one!");
    }
}
