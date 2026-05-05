package it.polimi.ingsw.CustomException;

//non sicurissimo che serva, la gestirei come logica interna del gioco senza Exception
public class LastPlayerOfTurnException extends RuntimeException {
    public LastPlayerOfTurnException() {
        super("Last player of turn has played");
    }
}
