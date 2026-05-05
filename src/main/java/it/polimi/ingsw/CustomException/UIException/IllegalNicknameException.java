package it.polimi.ingsw.CustomException.UIException;

//main UI-side exception
public class IllegalNicknameException extends RuntimeException {
    public IllegalNicknameException() {
        super("Nickname not valid");
    }
}
