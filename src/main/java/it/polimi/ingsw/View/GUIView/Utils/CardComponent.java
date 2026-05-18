package it.polimi.ingsw.View.GUIView.Utils;

import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;

/**
 * Rappresenta la View polimorfica di una singola carta all'interno della GUI.
 * Incapsula uno StackPane per permettere la stratificazione degli elementi grafici.
 */
public class CardComponent{

    private final StackPane cardLayout;
    private final boolean fromTopRow;
    private final boolean fromBuidings;
    private final int index;
    private final CardClickListener listener;

    /**
     * Costruttore del componente grafico della carta.
     *
     * @param cardId Identificativo univoco della carta per recuperare i dati e l'immagine.
     */
    public CardComponent(String cardId, boolean fromTopRow, boolean fromBuildings, int index, CardClickListener listener) {
        this.cardLayout = new StackPane();
        this.fromTopRow = fromTopRow;
        this.fromBuidings = fromBuildings;
        this.index = index;
        this.listener = listener;

        // Applica uno stile CSS generico per definire dimensioni base o cursori
        this.cardLayout.getStyleClass().add("game-card");

        // 1. Recupero del background grafico dal tuo ImageManager
        Node cardImageNode = ImageManager.getCardNode(cardId);

        // 2. Inserimento dell'immagine nello StackPane (Livello 0 - Sfondo)
        this.cardLayout.getChildren().add(cardImageNode);

        //setupTooltip(tooltipText); da guardare dopo per far apparire testo con dettagli carta
        // 3. Configurazione del comportamento cliccabile
        setupInteraction();
    }

    /**
     * Inizializza i listener per gestire l'interazione dell'utente con la carta.
     */
    private void setupInteraction() {
        // Rende la carta visibilmente interattiva cambiando il cursore al passaggio del mouse

        // Gestisce il click del mouse sulla carta
        this.cardLayout.setOnMouseClicked((MouseEvent event) -> {
            if (this.listener != null) {
                // Quando l'utente clicca, la carta passa al listener tutti i dati richiesti
                this.listener.onCardClicked(this.fromTopRow, this.fromBuidings, this.index);
            }
            event.consume(); // Previene la propagazione incontrollata dell'evento nella scena
        });
    }

    public Node getGraphicsNode() {
        // Esibisce solo il nodo radice, nascondendo l'implementazione interna dello StackPane
        return this.cardLayout;
    }
}