package it.polimi.ingsw.View.GUIView.GuiControllers;

import it.polimi.ingsw.View.GUIView.Gui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ChooseToCreateController {

    private Gui gui;

    @FXML
    private Button Join;

    @FXML
    private Button Create;

    public void setGUI(Gui gui) {
        this.gui = gui;
    }

    @FXML
    private void handleJoin() {
        gui.handleJoin();
    }
    @FXML
    private void handleCreate() {
        gui.handleCreate();
    }
}
