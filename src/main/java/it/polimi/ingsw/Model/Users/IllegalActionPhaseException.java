package it.polimi.ingsw.Model.Users;

public class IllegalActionPhaseException extends RuntimeException {
    public IllegalActionPhaseException() {
        super("You can't do this during this game phase.");
    }
}
