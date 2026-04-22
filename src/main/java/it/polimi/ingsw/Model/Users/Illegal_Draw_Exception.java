package it.polimi.ingsw.Model.Users;

public class Illegal_Draw_Exception extends RuntimeException {
    public Illegal_Draw_Exception() {
        super("carta evento non pescabile");
    }
}
