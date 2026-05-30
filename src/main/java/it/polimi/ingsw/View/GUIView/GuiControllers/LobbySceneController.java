package it.polimi.ingsw.View.GUIView.GuiControllers;

import it.polimi.ingsw.Controller.ClientController.ClientModel;
import it.polimi.ingsw.View.GUIView.Gui;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;

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
    private VBox playersContainer;

    @FXML
    private Button chooseTotemButton;

    @FXML
    private Button leaveLobby;

    @FXML
    private Button startGameButton;


    @FXML
    private Pane root;

    @FXML
    private AnchorPane gamePane;

    @FXML
    private ImageView background;

    private final Scale scaleTransform = new Scale();

    private static final double BASE_WIDTH = 1920;
    private static final double BASE_HEIGHT = 1080;
    @FXML
    public void initialize() {

        Image img = new Image(getClass().getResource("/Images/mesosBoxQuality.png").toExternalForm());

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

    @FXML
    private void handleTotem() {
        if (gui.getClientController().getLocalModel().getTotemColors().get(gui.getClientController().getPlayerName()) == null) {
            gui.chooseTotemScene();

        } else {
            updateLabel.setText("You already chose a totem");
        }
    }

    @FXML
    private void handleLeave() {
        gui.getClientController().leaveGame();
        try {
            gui.showCreationChoiceScene();
            gui.removePlayers();
            gui.handleLeave();
        } catch (IOException e) {

        }
    }

    @FXML
    private void handleStart(){

    }

    public void joined(){
        updateLabel.setText("You joined the lobby!");
    }

    public void init() {
        for (String name : gui.getPlayers()) {
            notifyPlayerJoined(name);
        }

        startGameButton.setVisible(gui.returnHost());
        startGameButton.setManaged(gui.returnHost());

    }

    public void notifyPlayerJoined(String playerName) {
        boolean alreadyIn = playersContainer.getChildren().stream()
                .anyMatch(node -> node instanceof Label && ((Label) node).getText().equals(playerName));

        // 2. Se NON è presente, crea la Label e aggiungila alla schermata
        if (!alreadyIn) {
            updateLabel.setText(playerName + " joined the lobby");
            addPlayerToContainer(playerName);
        }
    }

    public void notifyPlayerLeft(String playerName) {
        updateLabel.setText(playerName + " left the lobby");
        removePlayerFromContainer(playerName);
    }

    private void addPlayerToContainer(String playerName) {
        // Platform.runLater assicura che l'aggiunta avvenga sul thread grafico
        Platform.runLater(() -> {
            Label playerLabel = new Label(playerName);

            // Se usi fogli di stile o vuoi cambiare font/colore ai nomi, puoi farlo qui:
            // playerLabel.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");

            playersContainer.getChildren().add(playerLabel);
        });
    }

    private void removePlayerFromContainer(String playerName) {
        // Platform.runLater assicura che la rimozione avvenga sul thread grafico
        Platform.runLater(() -> {
            playersContainer.getChildren().removeIf(node ->
                    node instanceof Label && ((Label) node).getText().equals(playerName)
            );
        });
    }
    public void newHost(){
        startGameButton.setVisible(gui.returnHost());
        startGameButton.setManaged(gui.returnHost());
    }
}
