package it.polimi.ingsw.CustomException;

//goes under InvalidSelection
public class IllegalDrawException extends RuntimeException {
    public IllegalDrawException() {
        super("You can't draw an Event card");
    }
}
