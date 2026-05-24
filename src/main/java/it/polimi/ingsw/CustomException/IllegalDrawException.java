package it.polimi.ingsw.CustomException;

//goes under InvalidSelection
//TODO: cambiare, ci sono mille motivi per cui uno cerca di fare una pescata illegale, come ad esempio cercare di pescare da sotto se
//puo' solo pescare da sopra, oppure con un indice sbagliaoo. you cant draw an event card non basta, bisogna aggiungere un messaggio modificabile
public class IllegalDrawException extends RuntimeException {
    public IllegalDrawException() {
        super("You can't draw an Event card");
    }
    public IllegalDrawException(String message) {
        super(message);
    }
}
