package it.polimi.ingsw.Networking.Socket;

import java.io.IOException;

public interface VirtualSocketClient {
    void showUpdate() throws IOException;
    void reportError(String errorMessage) throws IOException;
}
