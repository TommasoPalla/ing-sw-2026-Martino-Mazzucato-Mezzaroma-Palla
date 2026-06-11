package it.polimi.ingsw.View.GUIView.Components;

import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class GameNotificationManager {
    private final AnchorPane target;
    private VBox notificationContainer;

    public GameNotificationManager(AnchorPane anchorPane) {
        this.target = anchorPane;
        initContainer();
    }

    private void initContainer() {
        notificationContainer = new VBox();
        notificationContainer.setAlignment(Pos.BOTTOM_RIGHT);

        //maybe unnecessary
        notificationContainer.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        notificationContainer.getStyleClass().add("notification-container-style");
        AnchorPane.setBottomAnchor(notificationContainer, 40.0);
        AnchorPane.setRightAnchor(notificationContainer, 30.0);

        //the container will disappear when it has no children
        notificationContainer.visibleProperty().bind(Bindings.isNotEmpty(notificationContainer.getChildren()));
        notificationContainer.managedProperty().bind(notificationContainer.visibleProperty());

        target.getChildren().add(notificationContainer);
        notificationContainer.toFront();
    }

    public void addWarning(String errorMessage) {
        addWarning(errorMessage, 3);
    }

    public void addWarning(String errorMessage, double timeout){
       addNotification("warning", errorMessage, timeout);
    }

    public void addInfoNotification(String message) {
        addInfoNotification(message, 3);
    }
    public void addInfoNotification(String message, double timeout) {
        addNotification("info", message, timeout);
    }

    private void addNotification (String notificationType, String message, double timeout) {
        HBox notification = new HBox();
        notification.setAlignment(Pos.CENTER_LEFT);
        notification.setMaxWidth(Region.USE_PREF_SIZE);

        Label messageLabel = new Label(message);
        messageLabel.setMaxWidth(200);
        messageLabel.setWrapText(true);
        HBox.setHgrow(messageLabel, Priority.ALWAYS);

        Button closeButton = new Button("X");
        closeButton.setFocusTraversable(false);

        if(notificationType.equals("warning")) {
            notification.getStyleClass().add("warning-style");
            messageLabel.getStyleClass().add("warning-text");
            closeButton.getStyleClass().add("warning-button");
        } else {
            notification.getStyleClass().add("notification-style");
            messageLabel.getStyleClass().add("notification-text");
            closeButton.getStyleClass().add("notification-button");
        }
        notification.getChildren().addAll(messageLabel, closeButton);

        PauseTransition timer = null;
        if(timeout > 0) {
            timer = new PauseTransition(Duration.seconds(timeout));
            timer.setOnFinished(event -> removeNotification(notification));
        }

        //timer is passed to finalTimer because Lambda Functions require final variables
        final PauseTransition finalTimer = timer;
        closeButton.setOnAction(event -> {
            if (finalTimer != null) {
                finalTimer.stop();
            }
            removeNotification(notification);
        });

        notificationContainer.getChildren().add(notification);
        //a click on the notification HBox should do nothing, this line prevents
        //the click to bubble up to the underlying components
        notification.setOnMouseClicked(Event::consume);

        if (finalTimer != null) {
            finalTimer.play();
        }

        //to be certain notification Container is set in front of other components
        notificationContainer.toFront();
    }

    private void removeNotification(HBox notification) {
        notificationContainer.getChildren().remove(notification);
    }
}