package it.polimi.ingsw.View.GUIView.GuiControllers;

import it.polimi.ingsw.View.GUIView.Gui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ChooseGameIdController {
    private Gui gui;

    @FXML
    private Button ConfirmButton;

    @FXML
    private TextField nicknameField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleConfirm() {
        try {
            String nickname = nicknameField.getText();
            int number = Integer.parseInt(nickname);
            gui.handleGameID(number);
        }catch (NumberFormatException e){
            errorLabel.setText("Insert a valid number");
        }
    }
}
