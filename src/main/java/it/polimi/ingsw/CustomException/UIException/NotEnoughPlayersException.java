package it.polimi.ingsw.CustomException.UIException;

public class NotEnoughPlayersException extends RuntimeException {
    public NotEnoughPlayersException() {
        super("ERROR: Not enough players to start the game.");
    }
}
