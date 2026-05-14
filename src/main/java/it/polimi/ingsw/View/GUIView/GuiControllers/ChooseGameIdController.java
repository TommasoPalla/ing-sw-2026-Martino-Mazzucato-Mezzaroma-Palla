package it.polimi.ingsw.View.GUIView.GuiControllers;

import it.polimi.ingsw.Controller.ClientController.ClientController;
import it.polimi.ingsw.View.GUIView.Gui;
import it.polimi.ingsw.View.GamePlayers;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.Map;

public class ChooseGameIdController {
    private Gui gui;

    public void setGUI(Gui gui) {
        this.gui = gui;
    }

    @FXML
    private Label errorLabel;

    @FXML
    private Button confirmButton;

    @FXML
    private TableView<Map.Entry<Integer, GamePlayers>> gamesTable;

    //each table column has a specific field
    @FXML
    private TableColumn<Map.Entry<Integer, GamePlayers>, String> connectedColumn;

    @FXML
    private TableColumn<Map.Entry<Integer, GamePlayers>, String> playersColumn;

    //shows id from map
    @FXML
    private TableColumn<Map.Entry<Integer, GamePlayers>, Integer> idColumn;

    //cellData is given every time a cell is created, 3 return because we have 3 columns
    //initialize automatically called by javaFX
    @FXML
    public void initialize() {

        //cellData is -> text, which is connected / max
        connectedColumn.setCellValueFactory(cellData -> {

            GamePlayers gamePlayers = cellData.getValue().getValue();

            int connected = gamePlayers.playerNames().size();

            int max = gamePlayers.playersNum();

            String text = connected + "/" + max;

            return new SimpleStringProperty(text);//sets the value to actually show
        });

        playersColumn.setCellValueFactory(cellData -> {

            GamePlayers gamePlayers = cellData.getValue().getValue();

            String players = String.join(", ", gamePlayers.playerNames());

            return new SimpleStringProperty(players);
        });

        idColumn.setCellValueFactory(cellData -> {

            Integer id =
                    cellData.getValue().getKey();

            return new SimpleObjectProperty<>(id);
        });
    }
    public void loadGames(Map<Integer, GamePlayers> availableGames) {

        gamesTable.getItems().clear();

        gamesTable.getItems().addAll(availableGames.entrySet());
    }
    @FXML
    private void handleConfirm() {
        //selected row is selected
        Map.Entry<Integer, GamePlayers> selected = gamesTable.getSelectionModel().getSelectedItem();

        if(selected == null) {
            errorLabel.setText("Select a game");
            return;
        }

        int gameId = selected.getKey();//selected.getKey() is ID

        gui.handleGameID(gameId);
    }

}
