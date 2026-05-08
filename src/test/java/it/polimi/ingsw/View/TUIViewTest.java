package it.polimi.ingsw.View;

import it.polimi.ingsw.Controller.ClientController.ClientController;
import org.junit.jupiter.api.Test;

class TUIViewTest {
    @Test
    public void testParse(){
        ClientController controller = new ClientController();
        TUIView view = new TUIView(controller);
        controller.createLocalModel(1, 4);
        controller.setPlayerName("ciao");
        controller.updatePlayerConnected("ciao");
        controller.updatePlayerConnected("pippo");
        controller.updateCurrentPlayer("ciao");
        //parseCOmmand is private view.parseCommand("draw_card(top, building, 1)");
    }

}