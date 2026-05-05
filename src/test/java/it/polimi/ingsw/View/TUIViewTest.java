package it.polimi.ingsw.View;

import it.polimi.ingsw.Controller.ClientController;
import it.polimi.ingsw.Model.ClientModel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TUIViewTest {
    @Test
    public void testParse(){
        ClientController controller = new ClientController();
        TUIView view = new TUIView(controller);
        controller.createLocalModel(1, 4);
        controller.setPlayerName("ciao");
        controller.addPlayer("ciao");
        controller.addPlayer("pippo");
        controller.updateCurrentPlayer("ciao");
        //parseCOmmand is private view.parseCommand("draw_card(top, building, 1)");
    }

}