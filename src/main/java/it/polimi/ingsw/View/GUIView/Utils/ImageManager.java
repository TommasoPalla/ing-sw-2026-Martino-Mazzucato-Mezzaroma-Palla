package it.polimi.ingsw.View.GUIView.Utils;

import it.polimi.ingsw.Model.Cards.Card;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.io.InputStream;

/**ImageManager class is used to show game assets images in the GUI.
 * getCardNode method returns the card's image or a placeholder if an exception occur.
 *
 */
public class ImageManager {
    // Path inside the src/main/resources folder
    private static final String RESOURCE_PATH_PREFIX = "/Images/";

    private static final double CARD_WIDTH = 103;
    private static final double CARD_HEIGHT = 152;
    private static final double TILE_WIDTH = 103;
    private static final double TILE_HEIGHT = 153;

    /**
     * Returns the graphical representation of a card given its unique ID.
     * Loads the image from the internal resources if present, otherwise generates a text fallback.
     *
     * @param cardId unique card ID received from the server
     * @return a JavaFX Node ready to be added to the scene
     */
    public static Node getCardNode(String cardId) {
        Node cardNode;
        String fullResourcePath = RESOURCE_PATH_PREFIX + "CardImages/" + cardId + ".png";

        InputStream imageStream = ImageManager.class.getResourceAsStream(fullResourcePath);

        if (imageStream != null) {
            try {
                // Background loading is set false to ensure the stream is read safely before closing
                Image image = new Image(imageStream, CARD_WIDTH, CARD_HEIGHT, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(CARD_WIDTH);
                imageView.setFitHeight(CARD_HEIGHT);
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
                Image image = new Image(imageStream, TILE_WIDTH, TILE_HEIGHT, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(TILE_WIDTH);
                imageView.setFitHeight(TILE_HEIGHT);
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
        String fullPath = RESOURCE_PATH_PREFIX + "Tiles/Turn" + numPlayers /*va castato?*/ + ".png";
        InputStream imageStream = ImageManager.class.getResourceAsStream(fullPath);
        if(imageStream != null) {
            try {
                Image image = new Image(imageStream, TILE_WIDTH, TILE_HEIGHT, true, true);
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(TILE_WIDTH);
                imageView.setFitHeight(TILE_HEIGHT);
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

    private static Node createPlaceholder(String cardID, char offerTileID, int numPlayers){
        StackPane pane = new StackPane();
        pane.setPrefSize(
                cardID != null ? CARD_WIDTH : TILE_WIDTH,
                cardID != null ? CARD_HEIGHT : TILE_HEIGHT
        );

        Rectangle background = new Rectangle(CARD_WIDTH, CARD_HEIGHT);
        background.setFill(Color.LIGHTGRAY);
        background.setStroke(Color.DARKGRAY);
        background.setStrokeWidth(2);
        background.setArcWidth(10);
        background.setArcHeight(10);

        Label textLabel;
        if(cardID != null){
            textLabel = new Label("CARD\nID: " + cardID);
        } else if (numPlayers > 1){
            textLabel = new Label("TURN TILE FOR\n: " + numPlayers + "players");
        } else {
            textLabel = new Label("OFFER TILE\nID: " + offerTileID);
        }
        textLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        textLabel.setTextFill(Color.BLACK);
        textLabel.setAlignment(Pos.CENTER);
        textLabel.setStyle("-fx-text-alignment: center;");

        pane.getChildren().addAll(background, textLabel);
        return pane;
    }
}