package it.polimi.ingsw.CustomException;

//goes under InvalidSelectionException
public class IllegalClientStateActionException extends RuntimeException {
    public IllegalClientStateActionException(String message) {
        super(message);
    }
}
