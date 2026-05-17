package it.polimi.ingsw.View.GUIView.GuiControllers;

import it.polimi.ingsw.View.GUIView.Gui;
import it.polimi.ingsw.View.GUIView.Utils.ImageManager;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.Pane;

public class GameSceneController {
    private Gui gui;


    @FXML
    private Pane cardSlot01;


    public void setGUI(Gui gui){
        this.gui = gui;
    }

    @FXML
    public void initialize() {
        // Esempio: Mostriamo la carta "01" all'avvio o alla risposta del server
        displayCard("E1_B_3");//esempio
    }

    public void displayCard(String cardId) {
        // 1. Otteniamo il nodo grafico (ImageView o StackPane di fallback) dall'ImageManager
        Node cardGraphicalNode = ImageManager.getCardNode(cardId);

        // 2. Puliamo lo slot da eventuali carte precedenti (es. se la carta nel mercato cambia)
        cardSlot01.getChildren().clear();

        // 3. Inseriamo la carta all'interno dello slot FXML
        cardSlot01.getChildren().add(cardGraphicalNode);
    }

}
