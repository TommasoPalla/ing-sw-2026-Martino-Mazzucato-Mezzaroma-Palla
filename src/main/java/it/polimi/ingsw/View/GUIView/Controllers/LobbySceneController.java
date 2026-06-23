package it.polimi.ingsw.View.GUIView.Controllers;

import it.polimi.ingsw.Controller.ClientController.ClientModel;
import it.polimi.ingsw.CustomException.IllegalClientStateActionException;
import it.polimi.ingsw.CustomException.UIException.ConnectionLostException;
import it.polimi.ingsw.CustomException.UIException.NotEnoughPlayersException;
import it.polimi.ingsw.CustomException.UIException.NotTheHostException;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.View.GUIView.Gui;
import it.polimi.ingsw.View.GUIView.Components.GameSceneBanner;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;

import java.io.IOException;

/**
 * this class controls the "LobbyScene" scene, allowing the user to choose between the loading of the ChooseTotemScene
 * via the "ChooseTotemButton" button or to leave the lobby and the host to start the game once the number of players
 * in the lobby is the one associated with the lobby and once all the players chose their totem.
 * All users sharing a lobby are also notified via "updateLabel" label of other players joining or leaving the lobby or
 * choosing a totem.
 */
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

    private GameSceneBanner banner;

    private static final double BASE_WIDTH = 1920;
    private static final double BASE_HEIGHT = 1080;

    /**
     * standard method to set the background and initialize local fields
     */
    @FXML
    public void initialize() {

        Image img = new Image(getClass().getResource("/Images/Background/LobbyBackground.png").toExternalForm());

        background.setImage(img);

        background.setPreserveRatio(true);

        this.banner = new GameSceneBanner(gamePane);

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

    /**
     * calls the scene where user is required to choose a totem
     */
    @FXML
    private void handleTotem() {
        if (gui.getClientController().getLocalModel().getTotemColors().get(gui.getClientController().getPlayerName()) == null) {
            gui.chooseTotemScene();

        } else {
            updateLabel.setText("You already chose a totem");
        }
    }

    /**
     * calls the creation scene and propagates the choice to leave the game to gui, where local fields are updated
     */
    @FXML
    private void handleLeave() {
        gui.getClientController().leaveGame();
        try {
            gui.showCreationChoiceScene();
            gui.removePlayers();
            gui.handleLeave();
        } catch (IOException e) {
            updateLabel.setText(e.getMessage());
        }
    }

    /**
     * calls the play game scene and propagates the choice to gui where local fields are updated
     */
    @FXML
    private void handleStart(){
        try {
            gui.getClientController().startGame();
            gui.removePlayers();
        } catch (IllegalClientStateActionException | NotTheHostException | IllegalArgumentException |
                 NotEnoughPlayersException | ConnectionLostException e) {
            updateLabel.setText(e.getMessage());
        }
    }

    /**
     * updated label
     */
    public void joined(){
        Platform.runLater(() -> {
            updateLabel.setText("You joined the lobby!");
        });
    }

    /**
     * initializes the lobbyscene where only the host can start the game
     */
    public void init() {
        for (String name : gui.getPlayers()) {
            notifyPlayerJoined(name);
        }

        startGameButton.setVisible(gui.returnHost());
        startGameButton.setManaged(gui.returnHost());

    }

    /**
     * updated the label with player of player who joined
     * @param playerName name of player who joined
     */
    public void notifyPlayerJoined(String playerName) {
        Platform.runLater(() -> {
            boolean alreadyIn = playersContainer.getChildren().stream()
                    .anyMatch(node -> node instanceof Label && ((Label) node).getText().equals(playerName));


            if (!alreadyIn) {
                updateLabel.setText(playerName + " joined the lobby");
                //addPlayerToContainer(playerName);

                Label playerLabel = new Label(playerName);
                playerLabel.setStyle("-fx-text-fill: #E5CEBE; -fx-font-size: 28px; -fx-font-weight: bold;");
                playersContainer.getChildren().add(playerLabel);
            }
        });
    }

    /**
     * updated the label with player of player who joined
     * @param playerName name of player who left
     */
    public void notifyPlayerLeft(String playerName) {
        updateLabel.setText(playerName + " left the lobby");
        removePlayerFromContainer(playerName);
    }

//    private void addPlayerToContainer(String playerName) {
//        // Platform.runLater assicura che l'aggiunta avvenga sul thread grafico
//        Platform.runLater(() -> {
//            Label playerLabel = new Label(playerName);
//
//            //playersContainer.getChildren().add(playerLabel);
//        });
//    }

    /**
     * removes a String from the VBox of connected player
     * @param playerName player whose left
     */
    private void removePlayerFromContainer(String playerName) {
        Platform.runLater(() -> {
            playersContainer.getChildren().removeIf(node ->
                    node instanceof Label && ((Label) node).getText().equals(playerName)
            );
        });
    }

    /**
     * notifies the new host they are the new host
     */
    public void newHost(){
        startGameButton.setVisible(gui.returnHost());
        startGameButton.setManaged(gui.returnHost());
    }

    /**
     * returns the update banner
     * @return banner
     */
    public GameSceneBanner getBanner(){
        return this.banner;
    }

    /**
     * notifies a player chose a totem
     * @param playerName name of the player
     * @param totemColor chosen totem
     */
    public void notifyTotemChosen(String playerName, Color totemColor){
        Platform.runLater(() -> {
            updateLabel.setText(playerName + " chose " + totemColor);
        });
    }

    /**
     * notifies an error occurred
     * @param errorMessage error
     */
    public void showError(String errorMessage){
        Platform.runLater(() -> {
            updateLabel.setText(errorMessage);
        });
    }
}
