package it.polimi.ingsw.View.GUIView.Controllers;

import it.polimi.ingsw.View.GUIView.Gui;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.transform.Scale;

import java.io.IOException;
import java.util.Map;

public class RankingSceneController{
    private Gui gui;

    public void setGUI(Gui gui) {
        this.gui = gui;
    }

    @FXML
    private Pane root;

    @FXML
    private AnchorPane gamePane;
    @FXML
    private ImageView background;

    @FXML
    private Button leaveLobby;

    @FXML
    private VBox ranking;

    @FXML
    private VBox generalLeaderboard;

    private final Scale scaleTransform = new Scale();

    private static final double BASE_WIDTH = 1920;
    private static final double BASE_HEIGHT = 1080;

    @FXML
    public void initialize() {

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

        for (Map.Entry<String, Integer> entry : gui.getCurrentRanking().entrySet()) {
            Label label = new Label(entry.getKey() + ": " + entry.getValue());
            ranking.getChildren().add(label);
        }
        for (String s : gui.getClientController().getLocalModel().getDbLeaderboard()){
            generalLeaderboard.getChildren().add(new Label(s));
        }
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
    private void handleLeave() {
        gui.getClientController().leaveGame();
        try {
            gui.showCreationChoiceScene();
            gui.removePlayers();
            gui.handleLeave();
        } catch (IOException e) {

        }
    }



}