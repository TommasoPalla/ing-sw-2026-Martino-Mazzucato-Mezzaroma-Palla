package it.polimi.ingsw.CustomException;

public class InvalidActionException extends RuntimeException {
    public InvalidActionException(String message, Throwable cause) {
        super(message, cause);
    }
}
