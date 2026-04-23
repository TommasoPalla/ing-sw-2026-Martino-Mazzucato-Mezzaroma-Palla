package it.polimi.ingsw.View;

import it.polimi.ingsw.Controller.GameController;
import it.polimi.ingsw.Model.ClientModel;
import it.polimi.ingsw.Networking.Shared.ServerInterface;

public class ClientController {
    private String playerName;
    GameController gameController;
    ServerInterface server;
    ClientModel localModel;

    public ClientController(ServerInterface server, GameController gameController) {
        this.server = server;
        this.gameController = gameController;
    }

}
