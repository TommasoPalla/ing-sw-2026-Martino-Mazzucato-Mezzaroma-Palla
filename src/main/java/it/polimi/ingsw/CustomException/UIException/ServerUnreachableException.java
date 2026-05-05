package it.polimi.ingsw.CustomException.UIException;

public class ServerUnreachableException extends RuntimeException {
    public ServerUnreachableException(String message) {
        super(message);
    }
}
