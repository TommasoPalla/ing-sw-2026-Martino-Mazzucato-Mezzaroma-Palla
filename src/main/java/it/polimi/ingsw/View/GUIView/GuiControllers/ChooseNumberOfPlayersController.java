package it.polimi.ingsw.View.GUIView.GuiControllers;

import it.polimi.ingsw.View.GUIView.Gui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ChooseNumberOfPlayersController {
    private Gui gui;

    @FXML
    private Button ConfirmButton;

    @FXML
    private TextField numberField;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleConfirm() {
        try {
            String nickname = numberField.getText();
            int number = Integer.parseInt(nickname);
            gui.handleNumber(number);
        }catch (NumberFormatException e){
            errorLabel.setText("Insert a valid number");
        }
    }
}
