package it.polimi.ingsw.CustomException.UIException;

//UI-side Exception
public class IllegalActionPhaseException extends RuntimeException {
    public IllegalActionPhaseException() {
        super("You can't do this action during this game phase.");
    }
}
