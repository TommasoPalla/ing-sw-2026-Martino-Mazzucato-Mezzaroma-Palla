package it.polimi.ingsw.View.GUIView.GuiControllers;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.View.GUIView.Gui;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.ScaleTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Scale;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Random;

import static java.lang.Double.max;
import static java.lang.Double.min;

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

        Image img = new Image(getClass().getResource("/Images/Background/LobbyBackground.png").toExternalForm());

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
        addGlitchOutline(whiteButton, redButton, purpleButton, yellowButton, blueButton);

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
    private final Random glitchRandom = new Random();

    private void addGlitchOutline(Button... buttons) {
        for (Button b : buttons) {
            b.setStyle("-fx-background-color: transparent; -fx-border-color: transparent; " +
                    "-fx-border-width: 0; -fx-padding: 0; -fx-cursor: hand;");

            ImageView totem = (ImageView) b.getGraphic();

            // Esterno sul Button
            DropShadow outer = new DropShadow();
            outer.setBlurType(javafx.scene.effect.BlurType.GAUSSIAN);
            outer.setColor(javafx.scene.paint.Color.BLACK);
            outer.setRadius(30);
            outer.setSpread(0.0);
            b.setEffect(outer);

            // Interno sull'ImageView
            javafx.scene.effect.InnerShadow inner = new javafx.scene.effect.InnerShadow();
            inner.setBlurType(javafx.scene.effect.BlurType.GAUSSIAN);
            inner.setColor(javafx.scene.paint.Color.BLACK);
            inner.setRadius(28);
            inner.setChoke(0.25);
            totem.setEffect(inner);

            double phaseOffset = glitchRandom.nextDouble() * Math.PI * 2;
            final long startTime = System.currentTimeMillis();

            Timeline glitch = new Timeline(new KeyFrame(Duration.millis(50), e -> {
                double t = (System.currentTimeMillis() - startTime) / 1000.0;

                double slowWave   = Math.sin(t * Math.PI + phaseOffset);
                double fastGlitch = glitchRandom.nextDouble() * 0.15;

                outer.setRadius(25 + slowWave * 12 + fastGlitch * 8);
                inner.setRadius(24 + slowWave * 10 + fastGlitch * 6);
                inner.setChoke(0.20 + slowWave * 0.08 + fastGlitch * 0.05);
            }));
            glitch.setCycleCount(Animation.INDEFINITE);
            glitch.play();
        }
    }


    @FXML
    private void handleHoverIn(MouseEvent event) {
        // IL TRUCCO È QUI: Java ci dice chi è stato toccato
        Node bottoneToccato = (Node) event.getSource();

        ScaleTransition st = new ScaleTransition(Duration.millis(150), bottoneToccato);
        st.setToX(1.6);
        st.setToY(1.6);
        st.play();
    }

    @FXML
    private void handleHoverOut(MouseEvent event) {
        Node bottoneToccato = (Node) event.getSource();

        ScaleTransition st = new ScaleTransition(Duration.millis(150), bottoneToccato);
        st.setToX(1.0);
        st.setToY(1.0);
        st.play();
    }

    @FXML
    private void handleWhite() {
        if(gui.getClientController().getLocalModel().isColorAvailable(Color.WHITE)){
            gui.getClientController().chooseTotemColor(Color.WHITE);
            try{
                gui.lobbyScene();
            }catch(IOException e){

            }
        }else{
            errorLabel.setText("Unavailable color, please choose another one");
        }
    }
    @FXML
    private void handleRed() {
        if(gui.getClientController().getLocalModel().isColorAvailable(Color.RED)){
            gui.getClientController().chooseTotemColor(Color.RED);
            try{
                gui.lobbyScene();
            }catch(IOException e){

            }
        }else{
            errorLabel.setText("Unavailable color, please choose another one");
        }
    }
    @FXML
    private void handlePurple() {
        if(gui.getClientController().getLocalModel().isColorAvailable(Color.PURPLE)){
            gui.getClientController().chooseTotemColor(Color.PURPLE);
            try{
                gui.lobbyScene();
            }catch(IOException e){

            }
        }else{
            errorLabel.setText("Unavailable color, please choose another one");
        }
    }
    @FXML
    private void handleYellow() {
        if(gui.getClientController().getLocalModel().isColorAvailable(Color.YELLOW)){
            gui.getClientController().chooseTotemColor(Color.YELLOW);
            try{
                gui.lobbyScene();
            }catch(IOException e){

            }
        }else{
            errorLabel.setText("Unavailable color, please choose another one");
        }
    }
    @FXML
    private void handleBlue() {
        if(gui.getClientController().getLocalModel().isColorAvailable(Color.BLUE)){
            gui.getClientController().chooseTotemColor(Color.BLUE);
            try{
                gui.lobbyScene();
            }catch(IOException e){

            }
        }else{
            errorLabel.setText("Unavailable color, please choose another one");
        }
    }
}
