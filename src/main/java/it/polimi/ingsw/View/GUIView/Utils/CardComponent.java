package it.polimi.ingsw.View.GUIView.Utils;

import it.polimi.ingsw.View.GUIView.GUISettings;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;


/**
 * Represents a graphical card component within the user interface of the game board.
 * This component encapsulates the visual representation of a card (including its size
 * and imagery managed via {@link ImageManager}) and implements the {@link BoardInteractionStrategy}
 * to handle player selection and drawing actions.
 */
public class CardComponent implements BoardInteractionStrategy {

    private final StackPane cardLayout;
    private final boolean fromTopRow;
    private final boolean fromBuildings;
    private final int index;
    private final BoardActionListener listener;


    /**
     * Constructs a new {@code CardComponent} with the specified game parameters and binds
     * it to an action listener for processing interaction events.
     *
     * @param cardId        the unique text identifier used to retrieve the card's graphics asset
     * @param fromTopRow    {@code true} if the card belongs to the top row layout; {@code false} otherwise
     * @param fromBuildings {@code true} if the card represents a building card; {@code false} if it represents a character card
     * @param index         the zero-based index positioning of this card inside its corresponding container row
     * @param listener      the interaction delegate invoked when a draw action triggers on this component
     */
    public CardComponent(String cardId, boolean fromTopRow, boolean fromBuildings, int index,
                         BoardActionListener listener) {
        this.cardLayout = new StackPane();
        this.fromTopRow = fromTopRow;
        this.fromBuildings = fromBuildings;
        this.index = index;
        this.listener = listener;
        this.cardLayout.getStyleClass().add("game-card");

        Node cardImageNode = ImageManager.getCardNode(cardId);
        this.cardLayout.setPrefSize(GUISettings.Cards.WIDTH, GUISettings.Cards.HEIGHT);
        this.cardLayout.getChildren().add(cardImageNode);

        //TODO: setupTooltip(tooltipText); da guardare dopo per far apparire testo con dettagli carta

        setupInteraction();
    }

    /**
     * Configures the behaviour on interactions with the component,
     * ensuring input propagation is contained within the graphic bounds.
     */
    private void setupInteraction() {
        this.cardLayout.setOnMouseClicked((MouseEvent event) -> {
            if (this.listener != null) {
                handleInteraction();
            }
            event.consume(); // Prevents propagation of mouse click
        });
    }

    /**
     * Retrieves the root JavaFX graphical node associated with this card element.
     *
     * @return the {@link Node} container layout encapsulating the card assets
     */
    public Node getGraphicsNode() {
        return this.cardLayout;
    }

    /**
     * Concrete implementation of {@link BoardInteractionStrategy} method.
     * Notifies the registered {@link BoardActionListener} that the client is requesting to draw
     * this specific card entity.
     */
    @Override
    public void handleInteraction(){
        listener.onDrawCardRequested(fromTopRow, fromBuildings, index);
    }
}