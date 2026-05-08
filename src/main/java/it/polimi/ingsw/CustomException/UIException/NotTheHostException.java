package it.polimi.ingsw.CustomException.UIException;

public class NotTheHostException extends RuntimeException {
    public NotTheHostException(String message) {
        super("ERROR: you can't start the game if you're not the host");
    }
}
