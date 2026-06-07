package it.polimi.ingsw.View.GUIView.GuiControllers;

import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.GameBoard.OfferTile;
import it.polimi.ingsw.View.GUIView.GUISettings;
import it.polimi.ingsw.View.GUIView.Gui;
import it.polimi.ingsw.View.GUIView.Utils.BoardActionListener;
import it.polimi.ingsw.View.GUIView.Utils.CardComponent;
import it.polimi.ingsw.View.GUIView.Utils.ImageManager;
import it.polimi.ingsw.View.GUIView.Utils.TileComponent;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.Objects;

public class GameBoardController {

    private Gui gui;
    private BoardActionListener actionListener;
    //private GameSceneController parentController;
    private static final PseudoClass SELECTABLE_PSEUDO = PseudoClass.getPseudoClass("selectable");

    private static final String BACK_ERA_1 = "/Images/CardImages/Era_1.png";
    private static final String BACK_ERA_2 = "/Images/CardImages/Era_2.png";
    private static final String BACK_ERA_3 = "/Images/CardImages/Era_3.png";


    @FXML
    private BorderPane rootPane;
    @FXML
    private HBox topRow;
    @FXML
    private HBox bottomRow;
    @FXML
    private HBox topBuildings;
    @FXML
    private HBox bottomBuildings;
    @FXML
    private ImageView deck;
    @FXML
    private HBox offerTrack;
    @FXML
    private HBox cardsRemaining;


    @FXML
    public void initialize() {
        topRow.setVisible(false);
        bottomRow.setVisible(false);
        topBuildings.setVisible(false);
        bottomBuildings.setVisible(false);
        setDeckImage(1);
    }

    public void setup(Gui gui) {
        this.gui = gui;
        updateInitialGameState();
    }

    public void setActionListener(BoardActionListener actionListener) {
        this.actionListener = actionListener;
    }


    /*public void setParent(GameSceneController gameSceneController) {
        this.parentController = gameSceneController;
    }*/

    public String setDeckImage(int era) {
        String imagePath, prefix;
        switch (era) {
            case 2 -> {
                imagePath = BACK_ERA_2;
                prefix = "Era 2 ";
            }
            case 3 -> {
                imagePath = BACK_ERA_3;
                prefix = "Era 3 ";
            }
            default -> {
                imagePath = BACK_ERA_1;
                prefix = "Era 1 ";   //never actually used
            }
        }
        Image deckImage = new Image(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
        deck.setImage(deckImage);
        //sets clipping
        Rectangle clip = new Rectangle(GUISettings.Cards.WIDTH, GUISettings.Cards.HEIGHT);
        clip.setArcHeight(15);
        clip.setArcWidth(15);
        deck.setClip(clip);
        return prefix;
    }

    private void updateInitialGameState(){
        initOfferTrack();
        updateTopRow();
        updateBottomRow();
        updateBottomBuildings();
        updateTopBuildings();
    }

    private void initOfferTrack(){
        offerTrack.getChildren().clear();
        StackPane turnTileLayout = new StackPane();
        turnTileLayout.getStyleClass().add("tile-style");
        Node tileImageNode = ImageManager.getTurnTile(gui.getClientController().getLocalModel().getNumPlayers());
        turnTileLayout.getChildren().add(tileImageNode);
        offerTrack.getChildren().add(turnTileLayout);

        ArrayList<OfferTile> tiles = gui.getClientController().getLocalModel().getOfferTiles();
        int i = 0;
        for(OfferTile tile: tiles){
            TileComponent tileRepresentation = new TileComponent(tile.getTileCode(), i, actionListener);
            tileRepresentation.getGraphicsNode().pseudoClassStateChanged(SELECTABLE_PSEUDO, false);
            offerTrack.getChildren().add(i+1, tileRepresentation.getGraphicsNode());
            i++;
        }
    }

    public void updateTopRow() {
        // Svuota i vecchi componenti grafici prima del ripristino della fila
        ArrayList<Card> cards = gui.getClientController().getLocalModel().getTopRow();
        topRow.getChildren().clear();

        int i = 0;
        // Popola dinamicamente il contenitore
        for (Card card : cards) {

            CardComponent cardRepresentation = new CardComponent(card.getCardID(), true, false,
                    i, actionListener);

            // Aggiunge il nodo grafico al layout lineare
            topRow.getChildren().add(cardRepresentation.getGraphicsNode());
            i++;
        }
    }

    public void updateBottomRow() {
        ArrayList<Card> cards = gui.getClientController().getLocalModel().getBottomRow();
        bottomRow.getChildren().clear();

        int i = 0;
        // Popola dinamicamente il contenitore
        for (Card card : cards) {

            CardComponent cardRepresentation = new CardComponent(card.getCardID(), false, false,
                    i, actionListener);
            // Aggiunge il nodo grafico al layout lineare
            bottomRow.getChildren().add(cardRepresentation.getGraphicsNode());
            i++;
        }
    }

    public void updateTopBuildings() {
        ArrayList<BuildingCard> cards = gui.getClientController().getLocalModel().getTopBuildings();
        topBuildings.getChildren().clear();

        int i = 0;
        for (BuildingCard building : cards) {

            CardComponent cardRepresentation = new CardComponent(building.getCardID(), true, true,
                    i, actionListener);

            topBuildings.getChildren().add(cardRepresentation.getGraphicsNode());
            i++;
        }
    }

    public void updateBottomBuildings() {
        ArrayList<BuildingCard> cards = gui.getClientController().getLocalModel().getBottomBuildings();
        bottomBuildings.getChildren().clear();

        int i = 0;
        for (BuildingCard building : cards) {

            CardComponent cardRepresentation = new CardComponent(building.getCardID(), false, true,
                    i, actionListener);

            bottomBuildings.getChildren().add(cardRepresentation.getGraphicsNode());
            i++;
        }
    }

    //TODO: quando inizia il round non viene attivato
    public void toggleSelectableTiles(boolean areSelectable) {
        int i = 1;
        for(OfferTile tile: gui.getClientController().getLocalModel().getOfferTiles()) {
            offerTrack.getChildren().get(i).pseudoClassStateChanged(SELECTABLE_PSEUDO,
                    (areSelectable && !tile.isOccupied()));
            i++;
        }
    }

    public void showRowsComponent() {
        topRow.setVisible(true);
        bottomBuildings.setVisible(false);  //necessarily void at the beginning of the game
        topBuildings.setVisible(true);
        bottomRow.setVisible(true);
    }

    public void setBottomBuildingsVisible(boolean visible) {
        bottomBuildings.setVisible(visible);
    }

}