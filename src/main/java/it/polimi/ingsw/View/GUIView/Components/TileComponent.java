package it.polimi.ingsw.View.GUIView.Components;

import it.polimi.ingsw.View.GUIView.GUISettings;
import it.polimi.ingsw.View.GUIView.Utils.ImageManager;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;

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
