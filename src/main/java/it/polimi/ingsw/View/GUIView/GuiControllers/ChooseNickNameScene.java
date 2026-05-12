package it.polimi.ingsw.View.GUIView.GuiControllers;

import it.polimi.ingsw.View.GUIView.Gui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class ChooseNickNameScene {
    private Gui gui;

    @FXML
    private Button ConfirmButton;

    @FXML
    private TextField nicknameField;

    @FXML
    private void handleConfirm() {

        String nickname = nicknameField.getText();
        gui.handleNickname(nickname);

    }
}
