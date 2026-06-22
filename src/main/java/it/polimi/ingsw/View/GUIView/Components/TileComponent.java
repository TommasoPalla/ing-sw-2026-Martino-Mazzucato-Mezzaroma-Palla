package it.polimi.ingsw.View.GUIView.Components;

import it.polimi.ingsw.View.GUIView.GUISettings;
import it.polimi.ingsw.View.GUIView.Utils.ImageManager;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

/**
 * A graphical wrapper component representing a generic physical board tile in Mesos.
 * <p>
 * It leverages a {@link StackPane} to allow structural layering, making it possible
 * to easily stack totem tokens on top of the tile artwork.
 */
public class TileComponent {
    StackPane tileLayout;

    //constructor used by OfferTileComponent constructor
    public TileComponent() {
        tileLayout = new StackPane();
        tileLayout.getStyleClass().add("tile-style");
    }

    //constructor used for Turn Tiles
    public TileComponent(int numPlayers) {
        this();

        Node tileImageNode = ImageManager.getTurnTile(numPlayers);
        tileLayout.setPrefSize(GUISettings.Tiles.WIDTH, GUISettings.Tiles.HEIGHT);
        tileLayout.getChildren().add(tileImageNode);
    }


    public Node getGraphicNode(){
        return tileLayout;
    }
}
