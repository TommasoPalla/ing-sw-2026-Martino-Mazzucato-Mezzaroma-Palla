package it.polimi.ingsw.View.GUIView.Components;

import javafx.animation.PauseTransition;
import javafx.beans.binding.Bindings;
import javafx.event.Event;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;


/**
 * UI component responsible for managing and displaying transient toast notifications
 * and operational warnings in real-time.
 * <p>
 * It creates a dynamic vertical stack container anchored at the bottom-right corner of the
 * screen, handling automatic fade/removal timers and manual close actions for individual alerts.
 */
public class GameNotificationManager {
    private final AnchorPane target;
    private VBox notificationContainer;

    public GameNotificationManager(AnchorPane anchorPane) {
        this.target = anchorPane;
        initContainer();
    }


    /**
     * Sets up the core vertical container stack. Configures geometry anchors,
     * applies CSS styling tokens, and sets up property bindings so the layout container
     * completely hides itself when no alerts are actively present on screen.
     */
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


    /**
     * Factory workflow method that dynamically constructs, styles, and injects a notification row
     * component into the display stack.
     * <p>
     * It binds text wrappers, configures custom structural button styles, handles event
     * bubbling protection (consuming mouse clicks so they don't trigger board events underneath),
     * and sets up the background countdown transitions.
     * @param notificationType Discriminator category determining the layout look ("warning" vs "info").
     * @param message          The actual descriptive text.
     * @param timeout          Lifespan duration bounds before trigger fires.
     */
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
            timer.setOnFinished(_ -> removeNotification(notification));
        }

        //timer is passed to finalTimer because Lambda Functions require final variables
        final PauseTransition finalTimer = timer;
        closeButton.setOnAction(_ -> {
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


    /**
     * Safely ejects an active notification row from the layout queue stack, effectively releasing
     * its memory allocations and allowing the background binding to automatically shrink the view space.
     * @param notification The target HBox container being discarded.
     */
    private void removeNotification(HBox notification) {
        notificationContainer.getChildren().remove(notification);
    }
}