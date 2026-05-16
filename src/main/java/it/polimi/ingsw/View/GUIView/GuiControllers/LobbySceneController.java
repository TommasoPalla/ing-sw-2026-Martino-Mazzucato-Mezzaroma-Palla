package it.polimi.ingsw.View.GUIView.GuiControllers;

import it.polimi.ingsw.Controller.ClientController.ClientModel;
import it.polimi.ingsw.View.GUIView.Gui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;


public class LobbySceneController {
    private Gui gui;
    private ClientModel model;

    public void setGUI(Gui gui) {
        this.gui = gui;
    }

    @FXML
    private Label updateLabel;

    @FXML
    private ComboBox<String> comboTendina;

    public void notifyPlayerJoined(String playerName){
        updateLabel.setText(playerName + "joined the lobby");
        addComboBox(playerName);
    }
    public void notifyPlayerLeft(String playerName){
        updateLabel.setText(playerName + "left the lobby");
        removeComboBox(playerName);
    }
    private void addComboBox(String playerName){
        comboTendina.getItems().add(playerName);

    }
    private void removeComboBox(String playerName){
        comboTendina.getItems().remove(playerName);
    }
}
