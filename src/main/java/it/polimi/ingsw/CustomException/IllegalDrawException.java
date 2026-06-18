package it.polimi.ingsw.CustomException;

//goes under InvalidSelection
public class IllegalDrawException extends RuntimeException {
    public IllegalDrawException(String message) {
        super(message);
    }
}