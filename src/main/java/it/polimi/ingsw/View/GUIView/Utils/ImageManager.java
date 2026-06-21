package it.polimi.ingsw.View.GUIView.Utils;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.View.GUIView.GUISettings;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;

/**ImageManager class is used to show game assets images in the GUI.
 */
public class ImageManager {
    // Path inside the src/main/resources folder
    private static final String RESOURCE_PATH_PREFIX = "/Images/";

    /**
     * Returns the graphical representation of a card given its unique ID.
     * Loads the image from the internal resources if present, otherwise generates a text fallback.
     *
     * @param cardId unique card ID received from the server
     * @return a JavaFX Node ready to be added to the scene
     */
    public static Node getCardNode(String cardId, double width, double height) {
        Node cardNode;
        String fullResourcePath = RESOURCE_PATH_PREFIX + "CardImages/" + cardId + ".png";

        InputStream imageStream = ImageManager.class.getResourceAsStream(fullResourcePath);

        if (imageStream != null) {
            try {
                // Background loading is set false to ensure the stream is read safely before closing. NO??
                Image image = new Image(imageStream, GUISettings.Cards.WIDTH, GUISettings.Cards.HEIGHT, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imageView.setFitWidth(width);
                imageView.setFitHeight(height);

                Rectangle clip = new Rectangle(width, height);
                clip.setArcHeight(15);
                clip.setArcWidth(15);
                imageView.setClip(clip);

                cardNode = imageView;
            } catch (Exception e) {
                System.err.println("Error rendering internal image resource for ID " + cardId + ". Using fallback.");
                cardNode = createPlaceholder(cardId, 'Z', 0);
            }
        } else {
            cardNode = createPlaceholder(cardId, 'Z', 0);
        }
        return cardNode;
    }

    public static Node getOfferTileNode(char id){
        Node tileNode;
        String fullPath = RESOURCE_PATH_PREFIX + "Tiles/Offer" + id + ".png";

        InputStream imageStream = ImageManager.class.getResourceAsStream(fullPath);

        if(imageStream != null) {
            try {
                Image image = new Image(imageStream, GUISettings.Tiles.WIDTH, GUISettings.Tiles.HEIGHT, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(GUISettings.Tiles.WIDTH);
                imageView.setFitHeight(GUISettings.Tiles.HEIGHT);

                //sets clip to have 10 pixel border radius in tile Images
                Rectangle clip = new Rectangle(GUISettings.Tiles.WIDTH, GUISettings.Tiles.HEIGHT);
                clip.setArcHeight(15);
                clip.setArcWidth(15);
                imageView.setClip(clip);

                tileNode = imageView;
            } catch (Exception e) {
                System.err.println("Error rendering internal image resource offer tile for ID " + id + ". Using fallback.");
                tileNode = createPlaceholder(null, id, 0);
            }
        } else {
            tileNode = createPlaceholder(null, id, 0);
        }
        return tileNode;
    }

    public static Node getTurnTile(int numPlayers){
        Node tileNode;
        String fullPath = RESOURCE_PATH_PREFIX + "Tiles/Turn" + numPlayers + ".png";
        InputStream imageStream = ImageManager.class.getResourceAsStream(fullPath);
        if(imageStream != null) {
            try {
                Image image = new Image(imageStream, GUISettings.Tiles.WIDTH, GUISettings.Tiles.HEIGHT, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(GUISettings.Tiles.WIDTH);
                imageView.setFitHeight(GUISettings.Tiles.HEIGHT);
                //sets clip to have 10 pixel border radius in tile Images
                Rectangle clip = new Rectangle(GUISettings.Tiles.WIDTH, GUISettings.Tiles.HEIGHT);
                clip.setArcHeight(15);
                clip.setArcWidth(15);
                imageView.setClip(clip);
                tileNode = imageView;
            } catch (Exception e) {
                System.err.println("Error rendering internal image resource for " + numPlayers + "players. Using fallback.");
                tileNode = createPlaceholder(null, 'Z', numPlayers);
            }
        } else {
            tileNode = createPlaceholder(null, 'Z', numPlayers);
        }
        return tileNode;
    }

    public static Node getTotem(Color color) {
        Node totemNode = null;
        String fullPath = RESOURCE_PATH_PREFIX + "Totems/" + color.toString() + ".png";
        InputStream imageStream = ImageManager.class.getResourceAsStream(fullPath);

        if(imageStream != null) {
            try {
                Image image = new Image(imageStream, GUISettings.Totems.WIDTH, GUISettings.Totems.HEIGHT, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(GUISettings.Totems.WIDTH);
                imageView.setFitHeight(GUISettings.Totems.HEIGHT);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                totemNode = imageView;
            } catch (Exception e) {
                System.err.println("Error rendering internal image resource for " + color);
            }
        }
        return totemNode;
    }

    private static Node createPlaceholder(String cardID, char offerTileID, int numPlayers){
        StackPane pane = new StackPane();
        pane.setPrefSize(
                cardID != null ? GUISettings.Cards.WIDTH : GUISettings.Tiles.WIDTH,
                cardID != null ? GUISettings.Cards.HEIGHT : GUISettings.Tiles.HEIGHT
        );

        Rectangle background = new Rectangle(
                cardID != null ? GUISettings.Cards.WIDTH : GUISettings.Tiles.WIDTH,
                cardID != null ? GUISettings.Cards.HEIGHT : GUISettings.Tiles.HEIGHT);
        background.getStyleClass().add("placeholder-style");

        Label textLabel;
        if(cardID != null){
            textLabel = new Label("CARD\nID: " + cardID);
        } else if (numPlayers > 1){
            textLabel = new Label("TURN TILE FOR\n: " + numPlayers + "players");
        } else {
            textLabel = new Label("OFFER TILE\nID: " + offerTileID);
        }
        pane.getChildren().addAll(background, textLabel);
        return pane;
    }
}