package it.polimi.ingsw.View.GUIView.Components;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.View.GUIView.GUISettings;
import it.polimi.ingsw.View.GUIView.Utils.ImageManager;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

public class TotemComponent {

    private final Pane totemLayer;
    private final Node totemNode;
    private static int numPlayers = 2;

    public TotemComponent(Pane totemLayer, Color color) {
        this.totemLayer = totemLayer;
        this.totemNode = ImageManager.getTotem(color);

        if(!this.totemLayer.getChildren().contains(totemNode)){
            this.totemLayer.getChildren().add(totemNode);
        }
        totemNode.setMouseTransparent(true);
        //totemNode.setVisible(false);
    }

    public static void setNumPlayers(int numPlayers) {
        TotemComponent.numPlayers = numPlayers;
    }

    public void moveTotemToTurnTile(Node tile, int index) {
        //bounds represent x and y coordinates where tile is set in the parent (OfferTrack) and its width and height
        Bounds tileBounds = tile.getBoundsInParent();

        double targetX = tileBounds.getMinX() + (tileBounds.getWidth() / 2) - 15 + 5 * (numPlayers - index);
        double stepY = tileBounds.getHeight() / (2 * numPlayers);
        double targetY = tileBounds.getMinY() + stepY * index + 10;

        double deltaX = targetX - totemNode.getLayoutX();
        double deltaY = targetY - totemNode.getLayoutY();

        TranslateTransition transition = new TranslateTransition(Duration.millis(400), totemNode);
        transition.setToX(deltaX);
        transition.setToY(deltaY);

        transition.setOnFinished(event -> {
            totemNode.setLayoutX(targetX);
            totemNode.setLayoutY(targetY);

            totemNode.setTranslateX(0);
            totemNode.setTranslateY(0);
        });
        transition.play();
        totemNode.toFront();
    }

    public void moveTotemToOfferTile(Node tile) {
        Bounds tileBounds = tile.getBoundsInParent();

        double targetX = tileBounds.getMinX() + (tileBounds.getWidth() / 2) - 10;
        double targetY = tileBounds.getMinY() + 20.0;

        double deltaX = targetX - tile.getLayoutX();
        double deltaY = targetY - tile.getLayoutY();

        TranslateTransition transition = new TranslateTransition(Duration.millis(400), totemNode);
        transition.setToX(deltaX);
        transition.setToY(deltaY);

        transition.setOnFinished(event -> {
            totemNode.setLayoutX(targetX);
            totemNode.setLayoutY(targetY);

            totemNode.setTranslateX(0);
            totemNode.setTranslateY(0);
        });
        transition.play();
    }
}