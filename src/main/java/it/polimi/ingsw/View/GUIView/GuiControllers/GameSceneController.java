package it.polimi.ingsw.View.GUIView.GuiControllers;

import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.GameBoard.OfferTile;
import it.polimi.ingsw.Model.GameBoard.TurnTile;
import it.polimi.ingsw.View.GUIView.Gui;
import it.polimi.ingsw.View.GUIView.Utils.CardComponent;
import it.polimi.ingsw.View.GUIView.Utils.TileComponent;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.ArrayList;

public class GameSceneController {
    private Gui gui;

    @FXML
    private Label bannerLabel;
    @FXML
    private HBox topRow;
    @FXML
    private HBox bottomRow;
    @FXML
    private HBox topBuildings;
    @FXML
    private HBox bottomBuildings;
    @FXML
    private HBox offerTrack;
    @FXML
    private VBox infoBoard;
    @FXML
    private VBox tribeRegion;
    @FXML
    private HBox myTribe;
    @FXML
    private VBox hunterColumn;
    @FXML
    private VBox builderColumn;
    @FXML
    private VBox gathererColumn;
    @FXML
    private VBox artistColumn;
    @FXML
    private VBox shamanColumn;
    @FXML
    private VBox inventorColumn;

    public void setGUI(Gui gui){
        this.gui = gui;
    }

    @FXML
    public void initialize(){
        topRow.setVisible(false);
        bottomBuildings.setVisible(false);
        //ecc
        topRow.setPrefHeight(HBox.USE_COMPUTED_SIZE);
        topRow.setPrefWidth(HBox.USE_COMPUTED_SIZE);
        //tutti gli altri setPref

        bannerLabel.setText("Game started");
        bannerLabel.setVisible(true);
        PauseTransition delay = new PauseTransition(Duration.seconds(3));

        delay.setOnFinished(event -> {
            bannerLabel.setVisible(false);
            setupInitialGameState();
        });

        delay.play();
    }

    public void setupInitialGameState(){
        initOfferTrack();

    }

    public void updateTopRow(ArrayList<Card> cards) {
        // Svuota i vecchi componenti grafici prima del ripristino della fila
        topRow.getChildren().clear();

        int i = 0;
        // Popola dinamicamente il contenitore
        for (Card card : cards) {

            CardComponent cardRepresentation = new CardComponent(card.getCardID(), true, false,
                    i, this::drawCard);

            // Aggiunge il nodo grafico al layout lineare
            topRow.getChildren().add(cardRepresentation.getGraphicsNode());
        }
    }

    public void updateBottomRow(ArrayList<Card> cards) {
        // Svuota i vecchi componenti grafici prima del ripristino della fila
        bottomRow.getChildren().clear();

        int i = 0;
        // Popola dinamicamente il contenitore
        for (Card card : cards) {

            CardComponent cardRepresentation = new CardComponent(card.getCardID(), false, false,
                    i, this::drawCard);

            // Aggiunge il nodo grafico al layout lineare
            bottomRow.getChildren().add(cardRepresentation.getGraphicsNode());
        }
    }

    public void updateTopBuildings(ArrayList<BuildingCard> cards) {
        topBuildings.getChildren().clear();

        int i = 0;
        for (BuildingCard card : cards) {

            CardComponent cardRepresentation = new CardComponent(card.getCardID(), true, true,
                    i, this::drawCard);

            topBuildings.getChildren().add(cardRepresentation.getGraphicsNode());
        }
    }

    public void updateBottomBuildings(ArrayList<BuildingCard> cards) {
        bottomBuildings.getChildren().clear();

        int i = 0;
        for (BuildingCard card : cards) {

            CardComponent cardRepresentation = new CardComponent(card.getCardID(), false, true,
                    i, this::drawCard);

            bottomBuildings.getChildren().add(cardRepresentation.getGraphicsNode());
        }
    }

    public void initOfferTrack(){
        ArrayList<OfferTile> tiles = gui.getClientController().getLocalModel().getOfferTiles();
//        TurnTile turnTile = gui.getClientController().getLocalModel().getTurnTile();
        offerTrack.getChildren().clear();
        //capire se per turnTile serve component diverso, si getta NumPlayers e dentro il component si fa swtich per
        //selezionare l'immagine giusta. Esempio if(numP == 2) then getNode("tile_2.png")

        int i = 0;
        for(OfferTile tile: tiles){
            TileComponent tileRepresentation = new TileComponent(tile.getTileCode(), this::chooseOfferTile, i);
            offerTrack.getChildren().add(tileRepresentation.getGraphicsNode());
        }
        //creare OfferTIle component e forse turnTileComponent e inserirli qua
        //probabilmente vanno aggiunti i metodi per gettare turnTile e le offerTrack all'inizio
    }


    private void drawCard(boolean fromTopRow, boolean fromBuildings, int index){
        //try {
        gui.getClientController().drawCard(fromTopRow, fromBuildings, index);
    }

    private void chooseOfferTile(int index){
        //try
        gui.getClientController().chooseOfferTile(index);
    }
}
