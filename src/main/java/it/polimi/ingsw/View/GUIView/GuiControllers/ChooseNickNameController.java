package it.polimi.ingsw.View.GUIView.GuiControllers;

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
import javafx.scene.transform.Scale;

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

        nicknameField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                handleConfirm();  //invia i dati premendo ENTER
            }
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
            errorLabel.setText("try another nickName please");
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
