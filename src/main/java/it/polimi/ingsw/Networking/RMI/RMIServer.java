package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Users.Illegal_Draw_Exception;
import it.polimi.ingsw.Model.Users.Occupied_Tile_Exception;
import it.polimi.ingsw.Networking.Shared.ServerInterface;

public class RMIServer implements ServerInterface {
    /*final MainController controller;
    final List<ClientInterface> clients = new ArrayList<>();*/

    //metodi di server interface overridati per avere logica rmi
    @Override
    public void connect(String nickName){};
    @Override

    public void disconnect(String nickName){};
    @Override

    public void chooseTotem(Color totemColor){};
    @Override

    public void chooseOfferTile(int index) throws Occupied_Tile_Exception{};
    @Override

    public void drawCardFromTop(int index) throws Illegal_Draw_Exception{};
    @Override

    public void drawCardFromBottom(int index) throws Illegal_Draw_Exception{};
}
//overrida serverInterface per implementare logica rmi, il socket farà la stessa cosa
//ma nel corpo ci sarà logica socket
