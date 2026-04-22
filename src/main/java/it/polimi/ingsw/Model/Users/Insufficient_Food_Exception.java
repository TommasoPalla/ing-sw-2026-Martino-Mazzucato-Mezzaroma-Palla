package it.polimi.ingsw.Model.Users;

public class Insufficient_Food_Exception extends RuntimeException {
    public Insufficient_Food_Exception() {
        super("Not enough food to purchase");
    }
}
