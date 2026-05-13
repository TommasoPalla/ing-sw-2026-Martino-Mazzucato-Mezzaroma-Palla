package it.polimi.ingsw.CustomException;

//goes under InvalidSelection
public class IllegalActionTurnException extends RuntimeException {
    private final static String message = "Not your turn";
    public IllegalActionTurnException() {
        super(message);
    }
}
