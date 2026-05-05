package it.polimi.ingsw.CustomException.UIException;

public class ConnectionLostException extends RuntimeException {
    public ConnectionLostException(String message) {
        super(message);
    }
}
