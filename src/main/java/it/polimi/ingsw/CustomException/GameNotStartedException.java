package it.polimi.ingsw.CustomException;

public class GameNotStartedException extends RuntimeException {
    private static final String prefix = "Game not started yet";
    public GameNotStartedException() {
        super(prefix+".");
    }
    public GameNotStartedException(String message){
        super(prefix + message);
    }
}