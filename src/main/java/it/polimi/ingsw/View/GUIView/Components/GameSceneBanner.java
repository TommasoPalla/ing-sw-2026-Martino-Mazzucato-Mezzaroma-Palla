package it.polimi.ingsw.View.GUIView.Components;

import javafx.animation.PauseTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedDeque;


/** Utility for rendering notification banners in the JavaFX GUI.
 * The banner introduces an overlay that prevents the user to click on the background.
 * It supports dismissal via explicit user interaction (screen click)
 *  or automatic fade-out through an asynchronous background timer.
 */
public class GameSceneBanner {
    private final AnchorPane target;

    private final Queue<BannerRequest> bannerQueue;

    private boolean isDisplayBusy;

    /**
     * The full-screen layout layer preventing misclicks
     * and catching click events for early dismissal.
     */
    private StackPane overlay;

    /**
     * The asynchronous background timer handling the automatic dismissal timeout.
     */
    private PauseTransition autoCloseTimer;

    /**Optional callback logic execution hook triggered immediately upon banner closure.
     */
    private Runnable onCloseAction;

    /**
     * Constructs a GameSceneBanner manager targeting a full root container (most of the time being the mainAnchorPane).
     * @param target the root where the banner will be attached.
     */
    public GameSceneBanner(AnchorPane target){
        this.target = target;
        this.bannerQueue = new ConcurrentLinkedDeque<>();
        this.isDisplayBusy = false;
    }

    public void showBanner(String message, double timeout, Runnable onCloseAction, double onsetDelay){
        showBanner(message, null, timeout, onCloseAction, onsetDelay);
    }

    /**
     * Core routine method that initializes the complete
     * graphical banner stack context into the scene.
     * Sets up mouse click monitors for early dismissal and configures non-blocking background
     * timers to execute auto-closure gracefully.
     *
     * @param message       the textual notice string to print inside the label
     * @param customNode    an optional extra graphical component (e.g., a card) to nest below the text description string.
     *                     Pass {@code null} if text-only is required.
     * @param timeout duration threshold in seconds before triggering automatic dismissal.
     * @param onCloseAction asynchronous callback logic runnable context block triggered during teardown phase
     *                      Pass {@code null} if no action is required to be performed.
     */
    public void showBanner(String message, Node customNode, double timeout, Runnable onCloseAction, double onsetDelay) {
        bannerQueue.add(new BannerRequest(message, customNode, timeout, onCloseAction, onsetDelay));

        //Triggers queue execution
        processQueue();
    }

    private void processQueue() {
        if (isDisplayBusy || bannerQueue.isEmpty()) {
            return;
        }

        isDisplayBusy = true;
        BannerRequest currentRequest = bannerQueue.poll();
        this.onCloseAction = currentRequest.onCloseAction();

        overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(0, 0, 0, 0.0);");
        overlay.setAlignment(Pos.TOP_CENTER);
        AnchorPane.setTopAnchor(overlay, 0.0);
        AnchorPane.setBottomAnchor(overlay, 0.0);
        AnchorPane.setLeftAnchor(overlay, 0.0);
        AnchorPane.setRightAnchor(overlay, 0.0);

        VBox container = new VBox();
        StackPane.setAlignment(container, Pos.CENTER);
        container.getStyleClass().add("banner-label");
        container.setPickOnBounds(true);
        container.setMouseTransparent(false);
        container.setMaxWidth(Region.USE_PREF_SIZE);
        container.setMaxHeight(Region.USE_PREF_SIZE);

        Label banner = new Label(currentRequest.message());
        banner.setAlignment(Pos.CENTER);
        banner.getStyleClass().add("banner-text");
        container.getChildren().add(banner);

        if (currentRequest.customNode() != null) {
            /*TODO: per mostrare descrizione carta
            customNode.setOnMouseClicked(event -> {
                showDescription();
            });*/
            container.getChildren().add(currentRequest.customNode());
            StackPane.setAlignment(currentRequest.customNode(), Pos.BASELINE_CENTER);
        }

        overlay.getChildren().add(container);
        overlay.setOnMouseClicked(event -> hideBanner());
        if (currentRequest.onsetDelay() > 0) {
            PauseTransition delay = new PauseTransition(Duration.seconds(currentRequest.onsetDelay()));
            delay.setOnFinished(event -> target.getChildren().add(overlay));
            delay.play();
        } else {
            target.getChildren().add(overlay);
        }

        if(currentRequest.timeout() > 0){
            autoCloseTimer = new PauseTransition(Duration.seconds(currentRequest.timeout() + currentRequest.onsetDelay()));
            autoCloseTimer.setOnFinished(event -> hideBanner());
            autoCloseTimer.play();
        }
    }


    /**Teardown routine that closes the active banner display window context.
     * terminates any ticking background timers and fires any registered post-execution callback runnable.
     */
    public void hideBanner() {
        //if banner has been clicked before the timer ran out
        if(autoCloseTimer != null) {
            autoCloseTimer.stop();
            autoCloseTimer = null;
        }
        if(overlay != null){
            target.getChildren().remove(overlay);
            overlay = null;
            target.setEffect(null);

            if(onCloseAction != null) {
                onCloseAction.run();
                onCloseAction = null;
            }
            isDisplayBusy = false;
            PauseTransition inBetweenDelay = new PauseTransition(Duration.seconds(1));
            inBetweenDelay.setOnFinished(event -> processQueue());
            inBetweenDelay.play();
        }
    }

    /**
     * Internal immutable data record container representing a single unique notification event payload context.
     */
    private record BannerRequest(String message, Node customNode, double timeout, Runnable onCloseAction, double onsetDelay) {}
}
