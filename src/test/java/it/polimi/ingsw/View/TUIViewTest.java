package it.polimi.ingsw.View;

import it.polimi.ingsw.Controller.ClientController.ClientController;
import it.polimi.ingsw.Networking.RMI.RMIClient;
import it.polimi.ingsw.Networking.RMI.RMIServerAdapter;
import it.polimi.ingsw.Networking.RMI.VirtualRMIClient;
import it.polimi.ingsw.Networking.Shared.ServerConnection;
import org.junit.jupiter.api.Test;

class TUIViewTest {
    @Test
    public void testParse(){
        ClientController controller = new ClientController();
        ServerConnection connection = new RMIServerAdapter("127.0.0.1", 8080, controller);
        controller.bindConnection(connection);
        TUIView view = new TUIView(controller);
        controller.createLocalModel(1, 4);
        controller.setPlayerName("ciao");
        controller.updatePlayerConnected("ciao");
        controller.updatePlayerConnected("pippo");
//        controller.updateCurrentPlayer("ciao");
        //parseCOmmand is private view.parseCommand("draw_card(top, building, 1)");
    }

}