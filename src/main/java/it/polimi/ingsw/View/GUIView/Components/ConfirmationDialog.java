package it.polimi.ingsw.View.GUIView.Components;

import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.Objects;


public class ConfirmationDialog extends Dialog<ButtonType> {

    private static final ButtonType YES_BUTTON = new ButtonType("Yes", ButtonBar.ButtonData.YES);
    private static final ButtonType NO_BUTTON = new ButtonType("No", ButtonBar.ButtonData.NO);

    public ConfirmationDialog(String title, String description) {
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UNDECORATED);

        getDialogPane().sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.setFill(Color.TRANSPARENT);
                if (newScene.getWindow() instanceof Stage stage) {
                    stage.initStyle(StageStyle.TRANSPARENT);
                }
            }
        });

        DialogPane dialogPane = getDialogPane();
        dialogPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/gameSceneStyle.css")).toExternalForm());
        dialogPane.getStyleClass().add("custom-dialog-pane");

        VBox content = new VBox(15);
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("custom-dialog-title");
        Label descriptionLabel = new Label(description);
        descriptionLabel.getStyleClass().add("custom-dialog-description");
        content.getChildren().addAll(titleLabel, descriptionLabel);
        dialogPane.setContent(content);

        dialogPane.getButtonTypes().addAll(YES_BUTTON, NO_BUTTON);

        dialogPane.lookupButton(YES_BUTTON).getStyleClass().add("dialog-button-yes");
        dialogPane.lookupButton(NO_BUTTON).getStyleClass().add("dialog-button-no");

    }
}
