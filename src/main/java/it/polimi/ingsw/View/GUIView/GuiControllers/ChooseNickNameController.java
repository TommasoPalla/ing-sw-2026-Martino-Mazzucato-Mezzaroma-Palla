package it.polimi.ingsw.View.GUIView.GuiControllers;

import it.polimi.ingsw.View.GUIView.Gui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;

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
    private Label errorLabel;

    @FXML
    public void initialize() {
        nicknameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleConfirm();  //invia i dati premendo ENTER
            }
        });
    }

    @FXML
    private void handleConfirm() {

        String nickname = nicknameField.getText().trim();
        if (nickname.isEmpty()) {
            showError("Please enter a nickname.");
            return;
        }

        clearError();
        try{
            gui.handleNickname(nickname);
        }catch(IOException e){
            showError("An IO error occurred");
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