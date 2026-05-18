package it.polimi.ingsw.View.GUIView.GuiControllers;

import it.polimi.ingsw.Controller.ClientController.ClientModel;
import it.polimi.ingsw.View.GUIView.Gui;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import java.io.IOException;


public class LobbySceneController {
    private Gui gui;
    private ClientModel model;

    public void setGUI(Gui gui) {
        this.gui = gui;
    }

    @FXML
    private Label updateLabel;

    @FXML
    private ComboBox<String> comboDropDownMenu;

    @FXML
    private Button chooseTotemButton;

    @FXML
    private Button leaveLobby;

    @FXML
    private void handleTotem(){
        if(gui.getClientController().getLocalModel().getTotemColors().get(gui.getClientController().getPlayerName())==null){
            gui.chooseTotemScene();

        }else{
            updateLabel.setText("You already chose a totem");
        }
    }

    @FXML
    private void handleLeave(){
        gui.getClientController().leaveGame();
        try{
            gui.showCreationChoiceScene();
            gui.removeMe();
        }catch(IOException e){

        }
    }
    public void init(){
        for (String name : gui.getPlayers()){
            notifyPlayerJoined(name);
        }
    }

    public void notifyPlayerJoined(String playerName){
        updateLabel.setText(playerName + " joined the lobby");
        addComboBox(playerName);
    }
    public void notifyPlayerLeft(String playerName){
        updateLabel.setText(playerName + " left the lobby");
        removeComboBox(playerName);
    }
    private void addComboBox(String playerName){
        comboDropDownMenu.getItems().add(playerName);
    }
    private void removeComboBox(String playerName){
        comboDropDownMenu.getItems().remove(playerName);
    }
}
