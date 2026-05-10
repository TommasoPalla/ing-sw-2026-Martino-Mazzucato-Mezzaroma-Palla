package it.polimi.ingsw.View.GUIView.GuiControllers;

import it.polimi.ingsw.View.GUIView.Gui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class connectionTypeSceneController {

    private Gui gui;

    @FXML
    private Button RMIButton;

    @FXML
    private Button SocketButton;

    public void setGUI(Gui gui) {
        this.gui = gui;
    }

    @FXML
    private void handleSocket() {
        gui.handleSocket();
        gui.showGameIdScene();
    }
    @FXML
    private void handleRMI() {
        gui.handleRMI();
        gui.showGameIdScene();
    }
}
