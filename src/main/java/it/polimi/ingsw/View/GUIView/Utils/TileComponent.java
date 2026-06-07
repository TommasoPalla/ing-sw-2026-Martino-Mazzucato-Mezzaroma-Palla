package it.polimi.ingsw.View.GUIView.Utils;

import it.polimi.ingsw.View.GUIView.GUISettings;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;


/**
 * Represents a graphical tile component within the user interface of the game board.
 * This component encapsulates the visual representation of a offer tile (including its size
 * and imagery managed via {@link ImageManager}) and implements the {@link BoardInteractionStrategy}
 * to handle player selection and drawing actions.
 */
public class TileComponent implements BoardInteractionStrategy{

    private final StackPane tileLayout;
    private final int index;
    private final BoardActionListener listener;


    public TileComponent(char id, int index, BoardActionListener listener){
        this.tileLayout = new StackPane();
        this.tileLayout.getStyleClass().add("tile-style");
        this.index = index;
        this.listener = listener;

        Node tileImageNode = ImageManager.getOfferTileNode(id);
        this.tileLayout.setPrefSize(GUISettings.Tiles.WIDTH, GUISettings.Tiles.HEIGHT);
        tileLayout.getChildren().add(tileImageNode);
        setupInteraction();
    }

    public void setupInteraction() {
        //TODO: tooltip

        // Manages interaction on click
        this.tileLayout.setOnMouseClicked((MouseEvent event) -> {
            if (this.listener != null) {
                handleInteraction();
            }
            event.consume();
        });
    }

    public Node getGraphicsNode(){
        return tileLayout;
    }

    @Override
    public void handleInteraction(){
        listener.onPlaceTotemRequested(index);
    }
}