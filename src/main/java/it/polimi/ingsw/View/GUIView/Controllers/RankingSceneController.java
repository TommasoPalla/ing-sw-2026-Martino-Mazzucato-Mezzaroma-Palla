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
import java.util.List;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RankingSceneController{
    private Gui gui;

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

//    @FXML
//    private MediaView background;

    private final Scale scaleTransform = new Scale();

    private static final double BASE_WIDTH = 1920;
    private static final double BASE_HEIGHT = 1080;


    public void setGUI(Gui gui) {
        this.gui = gui;

    }

    @FXML
    public void initialize() {

        Image img = new Image(Objects.requireNonNull(getClass().getResource("/Images/Background/Background.png")).toExternalForm());
        //logic for video ending
//        String videoPath = getClass().getResource("/Images/Background/mesosvideo.mp4").toExternalForm();
//        Media media = new Media(videoPath);
//        MediaPlayer player = new MediaPlayer(media);
//        player.setCycleCount(MediaPlayer.INDEFINITE);
//        player.setMute(true);
//        player.play();
//        background.setMediaPlayer(player);
//        player.setOnPlaying(() -> {
//            System.out.println("MediaView size: " + background.getFitWidth() + "x" + background.getFitHeight());
//            System.out.println("Media size: " + media.getWidth() + "x" + media.getHeight());
//        });


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

//    private void setup(){
//        for (Map.Entry<String, Integer> entry : gui.getCurrentRanking().entrySet()) {
//            Label label = new Label(entry.getKey() + ": " + entry.getValue());
//            ranking.getChildren().add(label);
//        }
//        for (String s : gui.getClientController().getLocalModel().getDbLeaderboard()){
//            generalLeaderboard.getChildren().add(new Label(s));
//        }
//    }


    public void populateRanking(Map<String, Integer> data) {
        ranking.getChildren().clear();
        data.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(e -> {
                    Label label = new Label(e.getKey() + ": " + e.getValue());
                    label.setStyle("-fx-text-fill: #E5CEBE; -fx-font-size: 24px;");
                    label.setPrefWidth(480);
                    ranking.getChildren().add(label);
                });
    }

    public void populateLeaderboard(List<String> data) {
        generalLeaderboard.getChildren().clear();
        data.forEach(s -> {
            Label label = new Label(s);
            label.setStyle("-fx-text-fill: #E5CEBE; -fx-font-size: 24px;");
            label.setPrefWidth(480);
            generalLeaderboard.getChildren().add(label);
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
    private void handleLeave() {
        //gui.getClientController().leaveGame();
        try {
            gui.showCreationChoiceScene();
            gui.removePlayers();
            gui.handleLeave();
        } catch (IOException e) {

        }
    }

}