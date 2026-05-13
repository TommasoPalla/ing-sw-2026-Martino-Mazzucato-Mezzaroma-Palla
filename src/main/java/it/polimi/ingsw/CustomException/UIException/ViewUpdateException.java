package it.polimi.ingsw.CustomException.UIException;

public class ViewUpdateException extends RuntimeException {
    public ViewUpdateException(String message) {
        super(message);
    }
    public ViewUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
