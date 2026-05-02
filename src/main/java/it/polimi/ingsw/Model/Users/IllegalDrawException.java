package it.polimi.ingsw.Model.Users;

public class IllegalDrawException extends RuntimeException {
    public IllegalDrawException() {
        super("carta evento non pescabile");
    }
}
