package it.polimi.ingsw.Networking.Socket;

public class SocketMessageDTO {
    private String commandName;
    private Object[] parameters;

    public SocketMessageDTO(){}
    public SocketMessageDTO(String commandName, Object... parameters){
        this.commandName = commandName;
        this.parameters = parameters;
    }

    public String getCommandName(){return commandName;}
    public Object[] getParameters(){return parameters;}
}
