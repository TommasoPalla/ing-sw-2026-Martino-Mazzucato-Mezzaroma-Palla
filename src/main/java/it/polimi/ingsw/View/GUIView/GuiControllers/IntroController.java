package it.polimi.ingsw.View.GUIView.GuiControllers;

import javafx.fxml.FXML;

import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.image.ImageView;


public class IntroController {
    @FXML
    private ImageView background;

    @FXML
    private StackPane root;

    public void initialize() {

        background.setImage(
                new Image(getClass().getResource("/Images/Background/mesosBoxQuality.png").toExternalForm())
        );

        background.setPreserveRatio(true);
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());
    }
}
