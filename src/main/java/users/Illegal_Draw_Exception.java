package users;

public class Illegal_Draw_Exception extends RuntimeException {
    public Illegal_Draw_Exception() {
        super("carta evento non pescabile");
    }
}
