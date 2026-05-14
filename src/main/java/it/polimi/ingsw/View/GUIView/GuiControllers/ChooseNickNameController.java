package it.polimi.ingsw.View.GUIView.GuiControllers;

import it.polimi.ingsw.View.GUIView.Gui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ChooseNickNameController {
    private Gui gui;

    public void setGUI(Gui gui) {
        this.gui = gui;
    }
    @FXML
    private Button ConfirmButton;

    @FXML
    private TextField nicknameField;

    @FXML
    private void handleConfirm() {

        String nickname = nicknameField.getText();
        try{
            gui.handleNickname(nickname);
        }catch(IOException e){

        }
    }
}
