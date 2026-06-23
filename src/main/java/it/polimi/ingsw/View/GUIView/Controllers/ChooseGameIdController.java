package it.polimi.ingsw.View.GUIView.Controllers;

import it.polimi.ingsw.View.GUIView.Gui;
import it.polimi.ingsw.View.GamePlayers;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;

import java.io.IOException;
import java.util.Map;

/**
 * this class is the controller for the "ChooseGameID" scene, it allows the user to choose an element from a gamesTable
 * where available games to join are display, after a selection is possible to confirm the choice via the "confirm"
 * button while it's possible to go back to the creationChoiceScene at any moment via the "back" button
 */
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
    private Button goBack;

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
    @FXML
    private Pane root;

    @FXML
    private AnchorPane gamePane;

    @FXML
    private ImageView background;

    private final Scale scaleTransform = new Scale();

    private static final double BASE_WIDTH = 1920;
    private static final double BASE_HEIGHT = 1080;
    /**
     * standard method to set the background and initialize local fields
     */
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

            Integer id = cellData.getValue().getKey();

            return new SimpleObjectProperty<>(id);
        });

        Image img = new Image(getClass().getResource("/Images/Background/Background.png").toExternalForm());

        background.setImage(img);

        background.setPreserveRatio(true);


        background.setFitWidth(BASE_WIDTH);
        background.setFitHeight(BASE_HEIGHT);
        double imgWidth = img.getWidth();
        double imgHeight = img.getHeight();

        double scaleFactor = Math.min(BASE_WIDTH / imgWidth, BASE_HEIGHT / imgHeight);
        double scaledWidth = imgWidth * scaleFactor;
        double scaledHeight = imgHeight * scaleFactor;
        background.setLayoutX((BASE_WIDTH - scaledWidth) / 2);
        background.setLayoutY((BASE_HEIGHT - scaledHeight) / 2);

        gamePane.getTransforms().add(scaleTransform);

        root.sceneProperty().addListener((obs, oldScene, scene) -> {

            if (scene != null) {

                scene.widthProperty().addListener((o, ov, nv) -> updateScale());
                scene.heightProperty().addListener((o, ov, nv) -> updateScale());

                Platform.runLater(this::updateScale);
            }
        });
    }
    /**
     * standard method to scale the background
     */
    private void updateScale() {

        double sceneWidth = root.getScene().getWindow().getWidth();
        double sceneHeight = root.getScene().getWindow().getHeight();

        double scale = Math.min(
                sceneWidth / BASE_WIDTH,
                sceneHeight / BASE_HEIGHT
        );

        scaleTransform.setX(scale);
        scaleTransform.setY(scale);

        double scaledWidth = BASE_WIDTH * scale;
        double scaledHeight = BASE_HEIGHT * scale;

        gamePane.relocate(
                (sceneWidth - scaledWidth) / 2,
                (sceneHeight - scaledHeight) / 2
        );
    }

    //cellData is given every time a cell is created, 3 return because we have 3 columns
    //initialize automatically called by javaFX

    /**
     * initializes available games table
     * @param availableGames available games data and information
     */
    public void loadGames(Map<Integer, GamePlayers> availableGames) {

        gamesTable.getItems().clear();

        gamesTable.getItems().addAll(availableGames.entrySet());
    }

    /**
     * returns the user to the creation scene
     */
    @FXML
    private void handleLeave(){
        try{
            gui.showCreationChoiceScene();
        }catch(IOException e){
            Platform.runLater(() -> {
                errorLabel.setText(e.getMessage());
            });
        }
    }

    /**
     * calls the loader of the lobby scene
     */
    @FXML
    private void handleConfirm() {
        //selected row is selected
        Map.Entry<Integer, GamePlayers> selected = gamesTable.getSelectionModel().getSelectedItem();

        if(selected == null) {
            errorLabel.setText("Select a game");
            return;
        }
        if (selected.getValue().playerNames().contains(gui.getName())) {
            errorLabel.setText("Name \"" + gui.getName() + "\" is already taken in this game");
            return;
        }

        int gameId = selected.getKey();//selected.getKey() is ID

        try{
            gui.handleGameID(gameId);
        }catch(IOException e){
            System.out.println("Error: " + e);
        }
    }

}
