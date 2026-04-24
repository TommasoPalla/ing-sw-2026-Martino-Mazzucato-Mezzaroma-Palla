package it.polimi.ingsw.View;

import it.polimi.ingsw.Controller.GameController;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.ClientModel;
import it.polimi.ingsw.Model.Users.Occupied_Tile_Exception;
import it.polimi.ingsw.Networking.Shared.ServerInterface;

public class ClientController {
    private String playerName;
    GameController gameController;
    ServerInterface server;
    ClientModel localModel;

    public ClientController(ServerInterface server, GameController gameController) {
        this.server = server;//rmi o socket
        this.gameController = gameController;
    }
//chiama metodi di server interface
    public void draw(boolean isTopRow, int index) {
        if(isTopRow) {
            server.drawCardFromTop(index); // 🔵 ServerInterface
        }
        else{
            server.drawCardFromBottom(index);
        }
    }
    public void chooseOfferTile(int index) throws Occupied_Tile_Exception{
        server.chooseOfferTile(index);
    }
    public void connect(String nickName){
        server.connect(nickName);
    }
    public void disconnect(String nickName){
        server.disconnect(nickName);
    }
    public void chooseTotem(Color totemColor){
        server.chooseTotem(totemColor);
    }


}
