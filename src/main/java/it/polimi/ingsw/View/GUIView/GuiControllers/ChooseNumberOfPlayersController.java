package it.polimi.ingsw.View.GUIView.GuiControllers;

import it.polimi.ingsw.View.GUIView.Gui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;

public class ChooseNumberOfPlayersController {
    private Gui gui;

    public void setGUI(Gui gui) {
        this.gui = gui;
    }
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
            if(number <=5) {
                gui.handleNumber(number);
            }else{
                errorLabel.setText("Insert a number <= 5 please!");
            }
        }catch (IOException e){
            errorLabel.setText("Insert a valid number please!");
        }
    }
}
