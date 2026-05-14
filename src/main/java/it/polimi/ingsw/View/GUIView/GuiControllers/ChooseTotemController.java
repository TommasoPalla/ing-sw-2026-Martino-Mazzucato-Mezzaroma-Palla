package it.polimi.ingsw.View.GUIView.GuiControllers;

import it.polimi.ingsw.CustomException.UnavailableColorException;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.View.GUIView.Gui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class ChooseTotemController {
    private Gui gui;

    public void setGUI(Gui gui) {
        this.gui = gui;
    }
    @FXML
    private Button whiteButton;
    @FXML
    private Button redButton;
    @FXML
    private Button purpleButton;
    @FXML
    private Button yellowButton;
    @FXML
    private Button blueButton;

    @FXML
    private Label errorLabel;

    @FXML
    private void handleWhite() {
        if(gui.getClientController().getLocalModel().isColorAvailable(Color.WHITE)){

        }else{
            errorLabel.setText("Unavailable color, please choose another one");
        }
    }
    @FXML
    private void handleRed() {
        if(gui.getClientController().getLocalModel().isColorAvailable(Color.RED)){

        }else{
            errorLabel.setText("Unavailable color, please choose another one");
        }
    }
    @FXML
    private void handlePurple() {
        if(gui.getClientController().getLocalModel().isColorAvailable(Color.BLACK)){

        }else{
            errorLabel.setText("Unavailable color, please choose another one");
        }
    }
    @FXML
    private void handleYellow() {
        if(gui.getClientController().getLocalModel().isColorAvailable(Color.YELLOW)){

        }else{
            errorLabel.setText("Unavailable color, please choose another one");
        }
    }
    @FXML
    private void handleBlue() {
        if(gui.getClientController().getLocalModel().isColorAvailable(Color.BLUE)){

        }else{
            errorLabel.setText("Unavailable color, please choose another one");
        }
    }
}
