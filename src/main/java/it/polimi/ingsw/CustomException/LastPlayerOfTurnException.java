package it.polimi.ingsw.CustomException;

public class LastPlayerOfTurnException extends RuntimeException {
    public LastPlayerOfTurnException() {
        super("Last player of turn has played");
    }
}
