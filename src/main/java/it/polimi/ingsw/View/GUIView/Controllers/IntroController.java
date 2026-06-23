package it.polimi.ingsw.View.GUIView.Controllers;

import javafx.fxml.FXML;

import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.image.ImageView;
/**
 * this class controls the "IntroScene" scene, the control is limited to the introduction image to be shown
 */
public class IntroController {
    @FXML
    private ImageView background;

    @FXML
    private StackPane root;
    /**
     * standard method to set the background
     */
    public void initialize() {

        background.setImage(
                new Image(getClass().getResource("/Images/Background/mesosBoxQuality.png").toExternalForm())
        );

        background.setPreserveRatio(true);
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());
    }
}
