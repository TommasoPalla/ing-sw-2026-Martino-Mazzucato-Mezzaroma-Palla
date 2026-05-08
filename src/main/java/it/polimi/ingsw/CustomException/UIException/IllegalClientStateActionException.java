package it.polimi.ingsw.CustomException.UIException;

public class IllegalClientStateActionException extends RuntimeException {
    public IllegalClientStateActionException(String message) {
        super(message);
    }
}
