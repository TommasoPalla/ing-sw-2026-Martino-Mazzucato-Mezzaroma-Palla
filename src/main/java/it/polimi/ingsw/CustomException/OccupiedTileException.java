package it.polimi.ingsw.CustomException;

//goes under InvalidSelection
public class OccupiedTileException extends RuntimeException {
    public OccupiedTileException() {
        super("offer tile already occupied");
    }
}
