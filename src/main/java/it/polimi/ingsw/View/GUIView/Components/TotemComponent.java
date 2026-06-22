package it.polimi.ingsw.View.GUIView.Components;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.View.GUIView.Utils.ImageManager;
import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.*;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

/**
 * A graphical wrapper representing a physical player's Totem token on the board.
 * <p>
 * It handles the creation, visual styling and
 * real-time movement animations of the token as it transitions between different board
 * components like Offer tiles and Turn Order tiles.
 */
public class TotemComponent {

    private final Node totemNode;
    private static int numPlayers = 2;


    /**
     * Creates a new Totem component, applies a black border effect for visibility,
     * and layers it directly onto the shared board token plane.
     * @param totemLayer The primary Pane layout layer dedicated to hosting all moving board tokens.
     * @param color      The specific color assigned to this player totem.
     */
    public TotemComponent(Pane totemLayer, Color color) {
        this.totemNode = ImageManager.getTotem(color);

        DropShadow borderEffect = new DropShadow();
        borderEffect.setColor(javafx.scene.paint.Color.BLACK);
        borderEffect.setRadius(2.0);
        borderEffect.setSpread(0.7);
        borderEffect.setOffsetX(0);
        borderEffect.setOffsetY(0);

        this.totemNode.setEffect(borderEffect);

        if(!totemLayer.getChildren().contains(totemNode)){
            totemLayer.getChildren().add(totemNode);
        }
        totemNode.setMouseTransparent(true);
        //totemNode.setVisible(false);
    }


    /**
     * Global static setter to update the room size parameters.
     * This parameter dictates the geometric offsets when drawing layout piles.
     * @param numPlayers Total number of active players in the session.
     */
    public static void setNumPlayers(int numPlayers) {
        TotemComponent.numPlayers = numPlayers;
    }


    /**
     * Triggers a translation animation moving the totem token onto the Turn Order tile.
     * <p>
     * It dynamically reads the global scene bounds of the target tile and maps a safe
     * landing coordinate, offsetting position vectors based on player turn order index keys
     * to prevent physical token stacking or visual obstruction.
     * @param tile  The destination Turn Order graphic tile node reference.
     * @param index The relative slot position index within the turn queue track.
     */
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

        transition.setOnFinished(_ -> {
            totemNode.setLayoutX(targetX);
            totemNode.setLayoutY(targetY);

            totemNode.setTranslateX(0);
            totemNode.setTranslateY(0);
        });
        transition.play();
        totemNode.toFront();
    }

    /**
     * Triggers a translation animation moving the totem token onto an Offer track tile.
     * <p>
     * It centers the token onto the target node bounds, effectively marking that tile
     * as occupied for the current game round phase.
     * @param tile The destination Offer tile graphic node reference.
     */
    public void moveTotemToOfferTile(Node tile) {
        Bounds tileBounds = tile.getBoundsInParent();

        double targetX = tileBounds.getMinX() + (tileBounds.getWidth() / 2) - 10;
        double targetY = tileBounds.getMinY() + 20.0;

        double deltaX = targetX - tile.getLayoutX();
        double deltaY = targetY - tile.getLayoutY();

        TranslateTransition transition = new TranslateTransition(Duration.millis(400), totemNode);
        transition.setToX(deltaX);
        transition.setToY(deltaY);

        transition.setOnFinished(_ -> {
            totemNode.setLayoutX(targetX);
            totemNode.setLayoutY(targetY);

            totemNode.setTranslateX(0);
            totemNode.setTranslateY(0);
        });
        transition.play();
    }
}