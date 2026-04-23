package it.polimi.ingsw.Model.Users;

public class Illegal_Action_Phase_Exception extends RuntimeException {
    public Illegal_Action_Phase_Exception(String message) {
        super("You can't do this during this game phase.");
    }
}
