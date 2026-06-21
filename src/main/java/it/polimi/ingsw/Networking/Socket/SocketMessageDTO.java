package it.polimi.ingsw.Networking.Socket;

import it.polimi.ingsw.Enums.SocketHeaderNames;

public class SocketMessageDTO {
    private SocketHeaderNames commandName;
    private Object[] parameters;

    public SocketMessageDTO(SocketHeaderNames commandName, Object... parameters){
        this.commandName = commandName;
        this.parameters = parameters;
    }

    public SocketHeaderNames getCommandName(){return commandName;}
    public Object[] getParameters(){return parameters;}
}
