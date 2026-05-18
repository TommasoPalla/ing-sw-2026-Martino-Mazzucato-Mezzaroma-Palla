package it.polimi.ingsw.View.GUIView.Utils;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

public class TileComponent {
    private final StackPane cardLayout;
    private final TileClickListener listener;
    private final int index;

    public TileComponent(char id, TileClickListener listener, int index){
        this.cardLayout = new StackPane();
        this.cardLayout.getStyleClass().add("game-card");
        this.listener = listener;
        this.index = index;

        Node tileImageNode = ImageManager.getOfferTileNode(id);

        cardLayout.getChildren().add(tileImageNode);

        setupInteraction();
    }

    public void setupInteraction() {
        // Rende la carta visibilmente interattiva cambiando il cursore al passaggio del mouse

        // Gestisce il click del mouse sulla carta
        this.cardLayout.setOnMouseClicked((MouseEvent event) -> {
            if (this.listener != null) {
                // Quando l'utente clicca, la carta passa al listener tutti i dati richiesti
                this.listener.onTileClicked(this.index);
            }
            event.consume(); // Previene la propagazione incontrollata dell'evento nella scena
        });
    }

    public Node getGraphicsNode(){
        return cardLayout;
    }
}
