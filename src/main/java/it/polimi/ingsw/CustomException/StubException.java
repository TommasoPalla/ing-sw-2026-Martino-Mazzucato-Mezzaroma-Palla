package it.polimi.ingsw.CustomException;

//when a RemoteException is caught this class of Excpetion is thrown
public class StubException extends RuntimeException {
    private static final String defaultPrefix = "RMI Stub exception: ";
    public StubException(String message) {
        super(defaultPrefix + message);
    }
}