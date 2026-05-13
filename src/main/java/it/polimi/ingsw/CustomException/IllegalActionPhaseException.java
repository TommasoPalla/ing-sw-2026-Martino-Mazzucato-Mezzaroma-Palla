package it.polimi.ingsw.CustomException;

//goes under InvalidSelection
public class IllegalActionPhaseException extends RuntimeException {
    public IllegalActionPhaseException() {
        super("You can't do this action during this game phase.");
    }
}
