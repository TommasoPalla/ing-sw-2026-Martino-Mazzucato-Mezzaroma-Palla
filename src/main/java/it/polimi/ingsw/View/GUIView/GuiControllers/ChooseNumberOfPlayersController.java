package it.polimi.ingsw.View.GUIView.GuiControllers;

import it.polimi.ingsw.View.GUIView.Gui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

import java.io.IOException;
import java.lang.NumberFormatException;

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
    public void initialize() {
        numberField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleConfirm();  //invia i dati premendo ENTER
            }
        });
    }

    @FXML
    private void handleConfirm() {
        int number;
        String nickname = numberField.getText();

        try {
            number = Integer.parseInt(nickname);
        } catch (NumberFormatException e) {
            showError("You did not enter a number.");
            return;
        }

        clearError();
        if(number >= 2 && number <= 5) {
            try {
                gui.handleNumber(number);
            }catch (IOException e){
                showError("Insert a valid number please!");
            }
        }else{
            showError("Insert a number between 2 and 5 please!");
        }
    }

    private void showError(String message) {
        if (errorLabel != null) {
            errorLabel.setText(message);
            errorLabel.setVisible(true);
        }
    }
    private void clearError() {
        if (errorLabel != null) {
            errorLabel.setText("");
            errorLabel.setVisible(false);
        }
    }
}
