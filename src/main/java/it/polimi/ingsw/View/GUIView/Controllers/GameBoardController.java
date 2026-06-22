package it.polimi.ingsw.View.GUIView.Controllers;

import it.polimi.ingsw.Controller.ClientController.ClientModel;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.GameBoard.OfferTile;

import it.polimi.ingsw.View.GUIView.Components.CardComponent;
import it.polimi.ingsw.View.GUIView.Components.OfferTileComponent;
import it.polimi.ingsw.View.GUIView.Components.TileComponent;
import it.polimi.ingsw.View.GUIView.Components.TotemComponent;
import it.polimi.ingsw.View.GUIView.Gui;
import it.polimi.ingsw.View.GUIView.Utils.*;

import javafx.beans.binding.Bindings;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.*;
import java.util.regex.Pattern;


/**
 * UI controller responsible for managing the game board.
 * <p>
 * It synchronizes and renders dynamic card rows,
 * handles deck appearance shifts across historical Eras, updates visibility of tiles, cards
 * and other components regarding 'draw_card' and 'place_totem' game phase. This happens also via
 * pseudo-classes to highlight interactive slots depending on player resources
 * and turn actions.
 */
public class GameBoardController {

    private Gui gui;

    private BoardActionListener actionListener;
    private Runnable onPassTurnRequested;
    private String localPlayer;

    private Map<String, TotemComponent> totems;

    private static final PseudoClass SELECTABLE_PSEUDO = PseudoClass.getPseudoClass("selectable");
    private static final String BACK_ERA_1 = "/Images/CardImages/Era_1.png";
    private static final String BACK_ERA_2 = "/Images/CardImages/Era_2.png";
    private static final String BACK_ERA_3 = "/Images/CardImages/Era_3.png";

    private static final Pattern CHARACTER_CARD_ID = Pattern.compile("^E\\d_C.*$");


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
    private Pane totemLayer;
    @FXML
    private HBox remainingCards;
    @FXML
    private Label cardsTop;
    @FXML
    private Label cardsBottom;
    @FXML
    private Button passTurnBtn;



    @FXML
    public void initialize() {
        topRow.setVisible(false);
        bottomRow.setVisible(false);
        topBuildings.setVisible(false);
        bottomBuildings.setVisible(false);
        remainingCards.setVisible(false);
        setDeckImage(1);

        Tooltip tooltip = new Tooltip("Use it only when you should draw more cards,\nbut no character is present in the row");
        tooltip.setShowDelay(Duration.millis(300));
        passTurnBtn.setTooltip(tooltip);
    }


    /**
     * Binds core execution callback structures and populates initial view elements.
     * @param gui                 The primary GUI client coordinator.
     * @param onPassTurnRequested Runnable callback handling pass turn actions inside the game scene.
     * @param localPlayer         The unique user nickname identifier registered to this machine.
     */
    public void setup(Gui gui, Runnable onPassTurnRequested, String localPlayer) {
        this.gui = gui;
        this.onPassTurnRequested = onPassTurnRequested;
        this.localPlayer = localPlayer;
        updateInitialGameState();
    }


    public void setActionListener(BoardActionListener actionListener) {
        this.actionListener = actionListener;
    }


    /**
     * Swaps the back artwork of the common layout deck depending on the active game Era.
     * Also clips corners to ensure uniform rounded rendering.
     * @param era Target era match index (1, 2, or 3).
     * @return A prefix string describing the newly entered era.
     */
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
        Rectangle clip = new Rectangle(115.0, 171.0);
        clip.setArcHeight(15);
        clip.setArcWidth(15);
        deck.setClip(clip);
        return prefix;
    }


    /**
     * Binds container visibility properties directly to whether they contain child elements,
     * initializes the physical offer track layout, and forces an initial redraw on all card rows.
     */
    private void updateInitialGameState(){
        topRow.visibleProperty().bind(Bindings.isNotEmpty(topRow.getChildren()));
        topBuildings.visibleProperty().bind(Bindings.isNotEmpty(topBuildings.getChildren()));
        bottomRow.visibleProperty().bind(Bindings.isNotEmpty(bottomRow.getChildren()));
        bottomBuildings.visibleProperty().bind(Bindings.isNotEmpty(bottomBuildings.getChildren()));

        setPassTurnBtnEnabled(false);
        initOfferTrack();
        updateTopRow();
        updateBottomRow();
        updateBottomBuildings();
        updateTopBuildings();
    }


    /**
     * Assembles the main horizontal Offer Track components.
     * Clears old slots, appends a shared turn order tile at index 0, registers subsequent offer tile
     * components from the local model, and anchors active player totem tokens onto their default positions.
     */
    private void initOfferTrack(){
        totemLayer.setPickOnBounds(false);

        offerTrack.getChildren().clear();
        ClientModel localModel = gui.getClientController().getLocalModel();

        int numPlayers = localModel.getNumPlayers();
        TileComponent turnTile = new TileComponent(numPlayers);
        offerTrack.getChildren().addFirst(turnTile.getGraphicNode());

        ArrayList<OfferTile> tiles = localModel.getOfferTiles();
        int i = 0;
        for(OfferTile tile: tiles){
            OfferTileComponent tileRepresentation = new OfferTileComponent(tile.getTileCode(), i, actionListener);
            tileRepresentation.getGraphicNode().pseudoClassStateChanged(SELECTABLE_PSEUDO, false);
            offerTrack.getChildren().add(i+1, tileRepresentation.getGraphicNode());
            i++;
        }

        //initializes totem graphical components
        Map<String, Color> totems = localModel.getTotemColors();
        List<String> turnOrder = localModel.getTurnOrder();
        int index = 0;
        this.totems = new HashMap<>();
        TotemComponent.setNumPlayers(localModel.getNumPlayers());

        for(String player: turnOrder) {
            Color color = totems.get(player);
            TotemComponent totemNode = new TotemComponent(totemLayer, color);
            this.totems.put(player, totemNode);
            moveTotemToTurnTile(player, index);
            index++;
        }
    }

    /**
     * Clears and repopulates the upper card row based on updated local model data.
     */
    public void updateTopRow() {
        ArrayList<Card> cards = gui.getClientController().getLocalModel().getTopRow();
        topRow.getChildren().clear();

        int numPlayers = gui.getClientController().getLocalModel().getNumPlayers();
        int i = 0;
        for (Card card : cards) {

            CardComponent cardRepresentation = new CardComponent(card, true, false,
                    i, actionListener, numPlayers);
            cardRepresentation.getGraphicsNode().pseudoClassStateChanged(SELECTABLE_PSEUDO, false);

            topRow.getChildren().add(cardRepresentation.getGraphicsNode());
            i++;
        }
    }

    public void updateBottomRow() {
        ArrayList<Card> cards = gui.getClientController().getLocalModel().getBottomRow();
        bottomRow.getChildren().clear();

        int numPlayers = gui.getClientController().getLocalModel().getNumPlayers();
        int i = 0;
        for (Card card : cards) {

            CardComponent cardRepresentation = new CardComponent(card, false, false,
                    i, actionListener, numPlayers);
            cardRepresentation.getGraphicsNode().pseudoClassStateChanged(SELECTABLE_PSEUDO, false);

            bottomRow.getChildren().add(cardRepresentation.getGraphicsNode());
            i++;
        }
    }

    public void updateTopBuildings() {
        ArrayList<BuildingCard> cards = gui.getClientController().getLocalModel().getTopBuildings();
        topBuildings.getChildren().clear();

        int numPlayers = gui.getClientController().getLocalModel().getNumPlayers();
        int i = 0;
        for (BuildingCard building : cards) {

            CardComponent cardRepresentation = new CardComponent(building, true, true,
                    i, actionListener, numPlayers);
            cardRepresentation.getGraphicsNode().pseudoClassStateChanged(SELECTABLE_PSEUDO, false);

            topBuildings.getChildren().add(cardRepresentation.getGraphicsNode());
            i++;
        }
    }

    public void updateBottomBuildings() {
        ArrayList<BuildingCard> cards = gui.getClientController().getLocalModel().getBottomBuildings();
        bottomBuildings.getChildren().clear();

        int numPlayers = gui.getClientController().getLocalModel().getNumPlayers();
        int i = 0;
        for (BuildingCard building : cards) {

            CardComponent cardRepresentation = new CardComponent(building, false, true,
                    i, actionListener, numPlayers);
            cardRepresentation.getGraphicsNode().pseudoClassStateChanged(SELECTABLE_PSEUDO, false);

            bottomBuildings.getChildren().add(cardRepresentation.getGraphicsNode());
            i++;
        }
    }

    /**
     * Toggles the CSS "selectable" highlight state for all open Offer tiles on the track layout,
     * making sure already occupied tiles cannot be highlighted or targeted.
     * @param areSelectable True if the user is currently expected to place a totem.
     */
    public void toggleSelectableTiles(boolean areSelectable) {
        int i = 1;
        for(OfferTile tile: gui.getClientController().getLocalModel().getOfferTiles()) {
            offerTrack.getChildren().get(i).pseudoClassStateChanged(SELECTABLE_PSEUDO,
                    (areSelectable && !tile.isOccupied()));
            i++;
        }
    }


    /**
     * Evaluates game state constraints and updates the highlight states for all active cards.
     * <p>
     * It dynamically checks if the player has remaining card draw allowances, evaluates food reserves
     * against building discount parameters, and distinguishes event cards from standard
     * claimable characters. If no legal character choices exist but the player still holds draw tokens,
     * it automatically triggers the "Pass Turn" safety button display.
     * @param areSelectable True if it is currently the local player's turn to pick cards.
     */
    public void toggleSelectableCards(boolean areSelectable) {
        //boolean variable is used to assess that player can still draw a character, if 'false' "passTurnBtn" will be shown
        boolean foundCharacter = false;

        ClientModel localModel = gui.getClientController().getLocalModel();
        int remainingAbove = localModel.getPlayerTribe(localPlayer).getRemainingAbove();
        int remainingBelow = localModel.getPlayerTribe(localPlayer).getRemainingBelow();

        ArrayList<BuildingCard> modelTopBuildings = localModel.getTopBuildings();
        //to prevent misalignment and race condition
        int bottomLimit = Math.min(topBuildings.getChildren().size(), modelTopBuildings.size());

        int buildersDiscount = localModel.getPlayerTribe(localPlayer).getBuildersDiscount();
        int foodReserve = localModel.getPlayerTribe(localPlayer).getFoodReserve();

        for(int i = 0; i < bottomLimit; i++) {
            BuildingCard building = modelTopBuildings.get(i);
            int buildingCost = building.getCost() - buildersDiscount;

            boolean canPick = (foodReserve >= buildingCost) && (remainingAbove > 0);
            boolean isCurrentBuildingSelectable = areSelectable && canPick;

            topBuildings.getChildren().get(i).pseudoClassStateChanged(SELECTABLE_PSEUDO, isCurrentBuildingSelectable);
            topBuildings.getChildren().get(i).applyCss();
        }

        ArrayList<BuildingCard> modelBottomBuildings = localModel.getBottomBuildings();
        //to prevent misalignment and race condition
        bottomLimit = Math.min(bottomBuildings.getChildren().size(), modelBottomBuildings.size());
        for(int i = 0; i < bottomLimit; i++) {
            BuildingCard building = modelBottomBuildings.get(i);
            int buildingCost = building.getCost() - buildersDiscount;

            boolean canPick = (foodReserve >= buildingCost) && (remainingBelow > 0);
            boolean isCurrentBuildingSelectable = areSelectable && canPick;

            bottomBuildings.getChildren().get(i).pseudoClassStateChanged(SELECTABLE_PSEUDO, isCurrentBuildingSelectable);
            bottomBuildings.getChildren().get(i).applyCss();
        }

        ArrayList<Card> modelTopRow = localModel.getTopRow();
        //to prevent misalignment and race condition
        bottomLimit = Math.min(topRow.getChildren().size(), modelTopRow.size());

        for(int i = 0; i < bottomLimit; i++) {
            Card card = modelTopRow.get(i);
            boolean isCharacter = CHARACTER_CARD_ID.matcher(card.getCardID()).matches();

            boolean isCurrentCardSelectable = areSelectable && isCharacter && (remainingAbove > 0);
            topRow.getChildren().get(i).pseudoClassStateChanged(SELECTABLE_PSEUDO, isCurrentCardSelectable);
            topRow.getChildren().get(i).applyCss();

            //foundCharacter will result 'true' if at least once isCurrentCardSelectable has been 'true'
            foundCharacter = foundCharacter || isCurrentCardSelectable;
        }


        ArrayList<Card> modelBottomRow = localModel.getBottomRow();
        //to prevent misalignment and race condition
        bottomLimit = Math.min(bottomRow.getChildren().size(), modelBottomRow.size());

        for(int i = 0; i < bottomLimit; i++) {
            Card card = modelBottomRow.get(i);
            boolean isCharacter = CHARACTER_CARD_ID.matcher(card.getCardID()).matches();

            boolean isCurrentCardSelectable = areSelectable && isCharacter && (remainingBelow > 0);
            bottomRow.getChildren().get(i).pseudoClassStateChanged(SELECTABLE_PSEUDO, isCurrentCardSelectable);
            bottomRow.getChildren().get(i).applyCss();

            foundCharacter = foundCharacter || isCurrentCardSelectable;
        }

        if(!foundCharacter && areSelectable && (remainingAbove != 0 ||  remainingBelow != 0)) {
            setPassTurnBtnEnabled(true);
        }
    }


    /**
     * Fetches remaining upper/lower row draw allocations from the model and updates the text counters.
     */
    public void updateRemainingCards() {
        int topCount = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getRemainingAbove();
        int bottomCount = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getRemainingBelow();
        AnimationsUtils.animateLabelUpdate(cardsTop, String.valueOf(topCount));
        AnimationsUtils.animateLabelUpdate(cardsBottom, String.valueOf(bottomCount));
    }

    /**
     * Controls the visibility state of the remaining card count panel layer.
     * * @param isVisible True to show the info pane wrapper on screen.
     */
    public void showRemainingCards(boolean isVisible) {
        remainingCards.setVisible(isVisible);
    }


    /**
     * Flushes out all visual elements currently attached to the common board card container lists.
     */
    public void clearRows() {
        topRow.getChildren().clear();
        topBuildings.getChildren().clear();
        bottomBuildings.getChildren().clear();
        bottomRow.getChildren().clear();
    }


    public void moveTotemToTurnTile(String playerName, int index) {
        TotemComponent totem = this.totems.get(playerName);
        Node tile = offerTrack.getChildren().getFirst();
        totem.moveTotemToTurnTile(tile, index);
    }


    public void moveTotemToOfferTile(String playerName, int index) {
        TotemComponent totem = this.totems.get(playerName);
        Node tile = offerTrack.getChildren().get(index + 1); //plus one because at index 0 offer track has the turnTile
        totem.moveTotemToOfferTile(tile);
    }

    public void setPassTurnBtnEnabled(boolean enabled) {
        passTurnBtn.setVisible(enabled);
        passTurnBtn.setDisable(!enabled);
    }


    /**
     * FXML action trigger fired when the pass turn button is clicked.
     * Locks down interaction properties to prevent double-clicks and alerts the parent listener.
     */
    @FXML
    private void handlePassTurn() {
        passTurnBtn.setDisable(true);

        if(onPassTurnRequested != null) {
            onPassTurnRequested.run();
        }
    }
}