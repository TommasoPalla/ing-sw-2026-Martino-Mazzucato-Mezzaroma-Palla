package it.polimi.ingsw.View.GUIView.Components;

import it.polimi.ingsw.View.GUIView.GUISettings;
import it.polimi.ingsw.View.GUIView.Utils.BoardActionListener;
import it.polimi.ingsw.View.GUIView.Utils.BoardInteractionStrategy;
import it.polimi.ingsw.View.GUIView.Utils.ImageManager;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;


/**
 * Represents a graphical tile component within the user interface of the game board.
 * This component encapsulates the visual representation of an offer tile (including its size
 * and imagery managed via {@link ImageManager}) and implements the {@link BoardInteractionStrategy}
 * to handle player selection and drawing actions.
 */
public class OfferTileComponent extends TileComponent implements BoardInteractionStrategy{
    private final int index;
    private final BoardActionListener listener;


    public OfferTileComponent(char id, int index, BoardActionListener listener){
        super();    //initializes StackPane tileLayout
        this.index = index;
        this.listener = listener;

        Node tileImageNode = ImageManager.getOfferTileNode(id);
        this.tileLayout.setPrefSize(GUISettings.Tiles.WIDTH, GUISettings.Tiles.HEIGHT);
        tileLayout.getChildren().add(tileImageNode);
        setupInteraction();
    }

    public void setupInteraction() {
        // Manages interaction on click
        this.tileLayout.setOnMouseClicked((MouseEvent event) -> {
            if (this.listener != null) {
                handleInteraction();
            }
            event.consume();
        });
    }

    @Override
    public void handleInteraction(){
        listener.onPlaceTotemRequested(index);
    }
}