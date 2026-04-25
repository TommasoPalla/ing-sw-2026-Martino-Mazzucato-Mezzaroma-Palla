package it.polimi.ingsw.View;

import it.polimi.ingsw.Controller.GameController;
import it.polimi.ingsw.Model.ClientModel;
import it.polimi.ingsw.Networking.Shared.ServerInterface;

public class ClientController {
    private Player player;
    GameController gameController;
    ServerConnection connection;
    ClientModel localModel;

    public ClientController(ServerInterface server, GameController gameController, Player player) {
        this.server = server;
        this.gameController = gameController;
    }

}
