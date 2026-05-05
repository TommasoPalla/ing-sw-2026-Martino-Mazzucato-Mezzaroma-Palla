package it.polimi.ingsw.CustomException.UIException;

//UI side Exception: "contains" InsufficientFood, IllegalDraw
public class InvalidSelectionException extends RuntimeException {
    public InvalidSelectionException(String message, Throwable cause) {
        super(message, cause);
    }
    public InvalidSelectionException(Throwable cause){
        super("Invalid selection ", cause);
    }
}