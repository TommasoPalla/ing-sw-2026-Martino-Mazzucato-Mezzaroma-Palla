package it.polimi.ingsw.View.GUIView.Controllers;

import it.polimi.ingsw.Controller.ClientController.ClientModel;
import it.polimi.ingsw.Controller.ClientController.LightTribe;
import it.polimi.ingsw.CustomException.IllegalClientStateActionException;
import it.polimi.ingsw.CustomException.OccupiedTileException;
import it.polimi.ingsw.CustomException.UIException.InvalidSelectionException;
import it.polimi.ingsw.Enums.*;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;

import it.polimi.ingsw.View.GUIView.Components.ConfirmationDialog;
import it.polimi.ingsw.View.GUIView.Components.GameNotificationManager;
import it.polimi.ingsw.View.GUIView.Components.GameSceneBanner;
import it.polimi.ingsw.View.GUIView.Components.PlayerInfoWidget;
import it.polimi.ingsw.View.GUIView.GUISettings;
import it.polimi.ingsw.View.GUIView.Gui;
import it.polimi.ingsw.View.GUIView.Utils.*;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Map;

import static java.lang.Math.abs;


/**
 * Main UI controller for the core gameplay screen.
 * It handles real-time updates received from the server network layer, shows updates
 * to players and forwards to ClientController player's requests.
 */
public class GameSceneController implements BoardActionListener {
    private Gui gui;
    private String localPlayer;

    private GameSceneBanner banner;
    private GameNotificationManager notificationManager;

    private double defaultColumnSpacing;
    private TranslateTransition transition;
    private boolean isTribeOpen = false;



    @FXML
    private AnchorPane mainAnchor;
    @FXML
    private BorderPane gameBoard;
    @FXML
    private GameBoardController gameBoardController;
    @FXML
    private HBox statusBar;
    @FXML
    private Label eraStatus;
    @FXML
    private Label roundStatus;
    @FXML
    private Label phaseStatus;
    @FXML
    private Label playerStatus;
    @FXML
    private Button leaveGameBtn;
    @FXML
    private ScrollPane rightMenu;
    @FXML
    private VBox infoBoard;
    @FXML
    private VBox tribeRegion;
    @FXML
    private HBox tribeHeader;
    @FXML
    private Label foodReserve;
    @FXML
    private Label prestigePoints;
    @FXML
    private Label charactersNumber;
    @FXML
    private HBox myTribe;
    @FXML
    private Label sustenanceDiscount;
    @FXML
    private Label buildingsDiscount;
    @FXML
    private Label shamanStars;
    @FXML
    private Label distinctInventions;
    @FXML
    private VBox hunterColumn;
    @FXML
    private Label huntersNumber;
    @FXML
    private VBox builderColumn;
    @FXML
    private Label buildersNumber;
    @FXML
    private VBox gathererColumn;
    @FXML
    private Label gatherersNumber;
    @FXML
    private VBox artistColumn;
    @FXML
    private Label artistsNumber;
    @FXML
    private VBox shamanColumn;
    @FXML
    private Label shamansNumber;
    @FXML
    private VBox inventorColumn;
    @FXML
    private Label inventorsNumber;
    @FXML
    private VBox buildingColumn;


    /**
     * Links the main GUI manager and sets up the local player name.
     * Initializes the side-board with other players' info widgets and the game board,
     * which is an inner scene, that specifically manages cards rows and offer track.
     * @param gui The main GUI instance
     */
    public void setup(Gui gui){
        this.gui = gui;
        this.localPlayer = gui.getClientController().getPlayerName();
        initInfoBoard();
        gameBoardController.setup(gui, this::onPassTurnRequested, localPlayer);
    }

    /**
     * JavaFX automatic initialization hook. Sets up default UI states, binds mouse
     * click events to toggle the tribe sliding drawer, and builds utility components
     * like banners and notifications.
     */
    @FXML
    public void initialize() {
        gameBoardController.setActionListener(this);
        tribeRegion.setVisible(false);

        //defines spacing of tribe's columns, which contain localPlayer's cards
        //also initializes tribe drawer transition on-click
        this.defaultColumnSpacing = hunterColumn.getSpacing();
        transition = new TranslateTransition(Duration.millis(300), tribeRegion);
        tribeHeader.setOnMouseClicked(_ -> toggleTribeDrawer());

        //Using platform.runLater to be sure correct dimensions have been calculated
        Platform.runLater(() -> {
            double initialAmountToHide = tribeRegion.getHeight() - tribeHeader.getHeight() - 50.0;
            tribeRegion.setTranslateY(initialAmountToHide);
            //On mouse clicked tribe region will appear/disappear
        });
        this.banner = new GameSceneBanner(mainAnchor);
        this.notificationManager = new GameNotificationManager(mainAnchor);
    }

    /**
     * Builds the sidebar widget list representing opposing players' statuses
     * (food reserves, score, active tokens) to ensure full visibility during the match.
     */
    private void initInfoBoard() {
        Map<String, Color> players = gui.getClientController().getLocalModel().getTotemColors();
        for(String player: players.keySet()){
            if(!player.equals(localPlayer)) {
                PlayerInfoWidget playerInfo = new PlayerInfoWidget(player, players.get(player));
                playerInfo.setId(player);
                infoBoard.getChildren().add(playerInfo);
            }
        }
    }


    /**
     * Notifies players about initial bonus food tokens by updating
     * flags and showing a banner.
     * @param initialFood Map containing the starting food amounts for each active player.
     */
    public void showInitialFood(Map<String,Integer> initialFood) {
        StringBuilder messageBuilder = new StringBuilder("Game Started!\nAll of the players receive an initial food bonus!");
        int food;
        String foodToString;

        for (String player: initialFood.keySet()) {
            food = initialFood.get(player);
            foodToString = String.valueOf(food);
            if(player.equals(localPlayer)) {
                messageBuilder.append("\nYou: ");
                AnimationsUtils.animateLabelUpdate(foodReserve, foodToString);
            } else {
                messageBuilder.append("\n").append(player).append(": ");
                PlayerInfoWidget playerWidget = (PlayerInfoWidget) infoBoard.lookup("#" + player);
                playerWidget.updateFoodReserve(food);
            }
            messageBuilder.append(foodToString);
        }
        String message = messageBuilder.toString();

        banner.showBanner(message, 3, () -> tribeRegion.setVisible(true), 1);
    }


    /**
     * Triggers a round transition phase. Refreshes the board card rows, sets active game phases,
     * and shows a banner telling everyone whose turn it is.
     * @param round Current round counter index.
     */
    public void showNewRound(int round) {
        AnimationsUtils.animateLabelUpdate(roundStatus, String.valueOf(round));
        AnimationsUtils.animateLabelUpdate(phaseStatus, "Placing Totem");

        gameBoardController.updateTopRow();
        gameBoardController.updateBottomRow();
        gameBoardController.updateBottomBuildings();
        gameBoardController.updateTopBuildings();

        String firstPlayer = gui.getClientController().getLocalModel().getTurnOrder().getFirst();
        String prefix = (firstPlayer.equals(localPlayer)) ? "your "
                : firstPlayer + "'s ";

        banner.showBanner("Round " + round + "!\nIt's " + prefix + "turn", 1.5, null, 0);
    }


    /**
     * Updates the main view aesthetics and text status labels when the match shifts into a new Era.
     * @param era The new current Era index (I, II, or III).
     */
    public void showNewEra(int era) {
        String prefix = gameBoardController.setDeckImage(era);
        banner.showBanner(prefix + "started!", 1.5, null, 0);
        AnimationsUtils.animateLabelUpdate(eraStatus, String.valueOf(era));
    }


    /**
     * Moves a player's physical totem to a specific target tile on the Offer track.
     * @param playerName Nickname of the player moving their totem.
     * @param index      Target index on the offer track layout.
     */
    public void showTileChosen(String playerName, int index) {
        gameBoardController.moveTotemToOfferTile(playerName, index);
    }


    /**
     * Moves a totem back to the Turn Order tile.
     * @param playerName Nickname of the player.
     * @param index      Target position index within the turn order track.
     */
    public void showTotemToTurnTile(String playerName, int index) {
        gameBoardController.moveTotemToTurnTile(playerName, index);
        gameBoardController.setPassTurnBtnEnabled(false);
    }


    /**
     * Displays a notification message or a banner confirming that a specific player has
     * passed their turn.
     * @param player Nickname of the player who passed.
     */
    public void showTurnPassed(String player) {
        if(player.equals(localPlayer)) {
            String message = "You successfully passed your turn";
            banner.showBanner(message, 1.0, null, 0);
        } else {
            String message = player + " has passed their turn";
            notificationManager.addInfoNotification(message);
        }
    }


    /**
     * Handles the specific scenario where a player interacts with the special Tile A slot,
     * giving them extra food tokens and updating their personal reserve.
     * @param player    The nickname of the player.
     * @param foodBonus Amount of food gained.
     */
    public void showFoodBonusTile(String player, int foodBonus) {
        int foodReserve = gui.getClientController().getLocalModel().getPlayerTribe(player).getFoodReserve();
        String suffix = foodBonus + "Food tokens from Tile A";
        if(player.equals(localPlayer)) {
            AnimationsUtils.animateLabelUpdate(this.foodReserve, String.valueOf(foodReserve));
            banner.showBanner("You gained " + suffix , 1.5, null, 0);
        }

        else {
            PlayerInfoWidget playerWidget = (PlayerInfoWidget) infoBoard.lookup("#" + player);
            playerWidget.updateFoodReserve(foodReserve);
            String message = player + " gained " + suffix;
            notificationManager.addInfoNotification(message);
        }
    }


    /**
     * Reflects card drawing selections on the board. Removes the card item from the common layout rows
     * and triggers a banner animation showing the exact card that was taken.
     * @param player         The player drawing the card.
     * @param card           The card data entity being claimed.
     * @param topRow         True if drawn from the top row, false if from the bottom row.
     * @param fromBuildings  True if the item is a building card.
     */
    public void showCardDrawn(String player, Card card, boolean topRow, boolean fromBuildings){
        if(fromBuildings){
            if(topRow){
                gameBoardController.updateTopBuildings();
            } else {
                gameBoardController.updateBottomBuildings();
            }
        } else if(topRow){
            gameBoardController.updateTopRow();
        } else {
            gameBoardController.updateBottomRow();
        }

        if(player.equals(localPlayer)){
            addCardToTribe(card, fromBuildings);

            gameBoardController.updateRemainingCards();
            gameBoardController.toggleSelectableCards(true);
        }

        else {
            double scale = 0.9;
            Node cardImage = ImageManager.getCardNode(card.getCardID(), GUISettings.Cards.WIDTH * scale, GUISettings.Cards.HEIGHT * scale);
            String message = player + " has drawn this card";
            banner.showBanner(message, cardImage, 1.5, null, 0);

            if(!fromBuildings){
                PlayerInfoWidget playerWidget = (PlayerInfoWidget) infoBoard.lookup("#" + player);
                LightTribe tribe = gui.getClientController().getLocalModel().getPlayerTribe(player);
                playerWidget.updateCardDrawn(card, tribe);
            }
            else {
                BuildingCard buildingCard = (BuildingCard) card;
                Tooltip tooltip = new Tooltip(buildingCard.getEffectDescription());
                tooltip.setShowDelay(Duration.millis(200));
                Tooltip.install(cardImage, tooltip);
            }
        }
    }


    /**
     * Distributes a visual card node inside the local player's tribe layout.
     * Automatically scales images, hooks descriptive tooltips onto buildings, and updates tribe's flags.
     * This method is only invoked when server notifies that local player has drawn
     * a new card.
     * @param card       The domain model card object being added.
     * @param isBuilding Flag specifying if the card belongs to the buildings row.
     */
    private void addCardToTribe(Card card, boolean isBuilding){
        String id = card.getCardID();
        double scale = 0.9;

        if (isBuilding) {
            Node building = ImageManager.getCardNode(id, GUISettings.Cards.WIDTH * scale, GUISettings.Cards.HEIGHT * scale);

            BuildingCard buildingCard = (BuildingCard) card;
            Tooltip tooltip = new Tooltip(buildingCard.getEffectDescription());
            tooltip.setShowDelay(Duration.millis(200));
            Tooltip.install(building, tooltip);
            buildingColumn.getChildren().add(building);
        }

        else {
            LightTribe tribe = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer);

            int charactersNum = tribe.getPopulationSize();
            AnimationsUtils.animateLabelUpdate(charactersNumber, String.valueOf(charactersNum));

            CharacterCard character = (CharacterCard) card;
            Node characterImage = ImageManager.getCardNode(id, GUISettings.Cards.WIDTH * scale, GUISettings.Cards.HEIGHT * scale);

            int newNumberPerRole;
            switch (character.getRole()) {
                case ARTIST -> {
                    addCardToColumn(artistColumn, characterImage);
                    newNumberPerRole = tribe.getArtistsNumber();
                    AnimationsUtils.animateLabelUpdate(artistsNumber, String.valueOf(newNumberPerRole));
                }
                case BUILDER -> {
                    addCardToColumn(builderColumn, characterImage);
                    newNumberPerRole = tribe.getBuildersNumber();
                    AnimationsUtils.animateLabelUpdate(buildersNumber, String.valueOf(newNumberPerRole));
                }
                case GATHERER -> {
                    addCardToColumn(gathererColumn, characterImage);
                    newNumberPerRole = tribe.getGatherersNumber();
                    AnimationsUtils.animateLabelUpdate(gatherersNumber, String.valueOf(newNumberPerRole));
                }
                case HUNTER -> {
                    addCardToColumn(hunterColumn, characterImage);
                    newNumberPerRole = tribe.getHuntersNumber();
                    AnimationsUtils.animateLabelUpdate(huntersNumber, String.valueOf(newNumberPerRole));
                }
                case INVENTOR -> {
                    addCardToColumn(inventorColumn, characterImage);
                    Map<InventorType, Integer> inventorsPerType = tribe.getInventorsPerType();
                    int typesNum = 0;   //represents number of different inventions this player has
                    newNumberPerRole = 0;   //number of total inventors
                    for(InventorType inventorType: inventorsPerType.keySet()){
                        newNumberPerRole += inventorsPerType.get(inventorType);
                        if(inventorsPerType.get(inventorType) > 0){
                            typesNum++;
                        }
                    }
                    AnimationsUtils.animateLabelUpdate(inventorsNumber, String.valueOf(newNumberPerRole));
                    AnimationsUtils.animateLabelUpdate(distinctInventions, String.valueOf(typesNum));
                }
                case SHAMAN -> {
                    addCardToColumn(shamanColumn, characterImage);
                    newNumberPerRole = tribe.getShamansNumber();
                    AnimationsUtils.animateLabelUpdate(shamansNumber, String.valueOf(newNumberPerRole));
                }
            }
        }
    }


    /**
     * Appends a character card node at the top of its specific role column.
     * Dynamically shifts the vertical layout overlap (negative spacing) depending on the card stack
     * size, keeping cards visible and aligned without overflowing the container.
     * @param column   The target Vbox column representing a character role.
     * @param cardNode The graphical Node of the card image.
     */
    public void addCardToColumn(VBox column, Node cardNode) {
        column.getChildren().addFirst(cardNode);

        //calculates column spacing
        int cardCount = column.getChildren().size();
        if(cardCount > 1) {
            double columnHeight = column.getPrefHeight();
            double cardHeight = GUISettings.Cards.HEIGHT;

            double requiredSpacing = (columnHeight - cardHeight) / (cardCount - 1) - cardHeight;
            column.setSpacing(Math.min(requiredSpacing, defaultColumnSpacing));

        } else {
            column.setSpacing(0);
        }
        tribeRegion.applyCss();
        tribeRegion.layout();

        //updates tribe drawer position
        double amountToHide = tribeRegion.getHeight() - tribeHeader.getHeight() - 50;
        if (!isTribeOpen) {
            transition.stop();
            tribeRegion.setTranslateY(amountToHide);
            transition.setToY(amountToHide);
        }
    }


    /**
     * Syncs client state with highlighted actions. Locks or unlocks board items
     * (cards or offer tiles) so players are suggested to click only objects allowed.
     * @param player      The player currently allowed to make a choice.
     * @param clientState The current interaction phase constraints for that player.
     */
    public void showNewCurrentPlayer(String player, ClientState clientState){
        String message;
        ClientModel localModel = gui.getClientController().getLocalModel();

        switch (clientState) {
            case ClientState.NOT_IN_TURN -> {
                if(!player.equals(localModel.getTurnOrder().getFirst())) {
                    GamePhase phase = localModel.getCurrentPhase();
                    String phaseString = (phase == GamePhase.ON_DRAW) ? " to draw"
                            : " to place the totem";
                    message = "It's " + player + "'s turn" + phaseString;
                    notificationManager.addInfoNotification(message);
                }

                gameBoardController.showRemainingCards(false);
                gameBoardController.toggleSelectableCards(false);
                //selectableTiles are "hidden" after the totem is placed
            }

            case ClientState.PLACE_TOTEM -> {
                if(!player.equals(localModel.getTurnOrder().getFirst())) {
                    message = "It's your turn to place the totem";
                    banner.showBanner(message, 1.5, null, 0);
                }
                gameBoardController.showRemainingCards(false);
                gameBoardController.toggleSelectableCards(false);
                gameBoardController.toggleSelectableTiles(true);
            }

            default -> {
                if(!player.equals(localModel.getTurnOrder().getFirst())) {
                    message = "It's your turn to draw";
                    banner.showBanner(message, 1.5, null, 0);
                }
                gameBoardController.updateRemainingCards();
                gameBoardController.showRemainingCards(true);
                gameBoardController.toggleSelectableCards(true);
            }
        }
        if(player.equals(localPlayer)) {
            AnimationsUtils.animateLabelUpdate(playerStatus, "You");
        } else {
            AnimationsUtils.animateLabelUpdate(playerStatus, player);
        }
    }

    /**
     * Changes state configurations when major match phase cycles move forward
     * (e.g., transitioning between placement and drawing).
     * @param phase The incoming game phase.
     */
    public void showNewGamePhase(GamePhase phase){
        StringBuilder messageBuilder = new StringBuilder("It's time to ");
        String currentPlayer = gui.getClientController().getLocalModel().getTurnOrder().getFirst();

        if(phase == GamePhase.ON_DRAW) {
            messageBuilder.append("draw cards");
            messageBuilder.append(currentPlayer.equals(localPlayer)
                    ? "\nIt's your turn!"
                    : "\nIt's " + currentPlayer + "'s turn");
            gameBoardController.toggleSelectableTiles(false);
            AnimationsUtils.animateLabelUpdate(phaseStatus, "Drawing Cards");
            String message = messageBuilder.toString();
            banner.showBanner(message, 2, null, 0);
        }

        else if (phase == GamePhase.START_TURN){
            messageBuilder.append("place totems");
            messageBuilder.append(currentPlayer.equals(localPlayer)
                    ? "\nIt's your turn!"
                    : "\nIt's " + currentPlayer + "'s turn");
            gameBoardController.toggleSelectableCards(false);
            AnimationsUtils.animateLabelUpdate(phaseStatus, "Placing Totem");
            String message = messageBuilder.toString();
            banner.showBanner(message, 2, null, 0);

        } else {
            gameBoardController.toggleSelectableTiles(false);
            gameBoardController.toggleSelectableCards(false);
            AnimationsUtils.animateLabelUpdate(phaseStatus, "End Game");
        }
    }


    /**
     * Tallies and displays deltas for resource updates right after resolving a card event.
     * @param player       The player affected by the event.
     * @param eventType    The nature of the resolved card event.
     * @param foodModified The positive or negative change in food tokens.
     * @param ppModified   The positive or negative change in Prestige Points.
     */
    public void showEventEffects(String player, EventType eventType, int foodModified, int ppModified) {
        AnimationsUtils.animateLabelUpdate(phaseStatus, "Event Resolution");
        LightTribe tribe = gui.getClientController().getLocalModel().getPlayerTribe(player);

        if(player.equals(localPlayer)) {
            StringBuilder message = new StringBuilder(String.valueOf(eventType));
            message.append(" event resolved!\nYou ");
            String variation = (foodModified > 0) ? "gained "
                                                : "lost ";
            message.append(variation).append(String.valueOf(abs(foodModified))).append(" Food Tokens");
            variation = (ppModified > 0) ? "\nYou gained "
                                        : "\nYou lost ";
            message.append(variation).append(String.valueOf(abs(ppModified))).append(" prestige Points");
            banner.showBanner(message.toString(), 2, null, 0);

            String foodReserve = String.valueOf(tribe.getFoodReserve());
            AnimationsUtils.animateLabelUpdate(this.foodReserve, foodReserve);
            String prestigePoints = String.valueOf(tribe.getPrestigePoints());
            AnimationsUtils.animateLabelUpdate(this.prestigePoints, prestigePoints);
        }

        else {
            PlayerInfoWidget playerWidget = (PlayerInfoWidget) infoBoard.lookup("#" + player);
            //cannot use foodModified and ppModified because they are deltas,
            // it is more reliable to get absolute values from Local Model
            int food = tribe.getFoodReserve();
            playerWidget.updateFoodReserve(food);
            int pp = tribe.getPrestigePoints();
            playerWidget.updatePrestigePoints(pp);

            String message = eventType.toString() + " event\n" + player + ": " + foodModified + " food" +
                    ppModified + " prestige Points";
            notificationManager.addInfoNotification(message);
        }
    }


    /**
     * Animates resource counter shifts whenever a player spends or acquires food tokens.
     * @param player       The target player nickname.
     * @param deltaFood    The change delta value.
     * @param foodQuantity The final total food amount.
     */
    public void showFoodModified(String player, int deltaFood, int foodQuantity){
        String suffix = abs(deltaFood) + " Food tokens";
        String prefix;

        if(player.equals(localPlayer)){
            AnimationsUtils.animateLabelUpdate(foodReserve, String.valueOf(foodQuantity));
            prefix = (deltaFood > 0) ? "You gained "
                    : "You lost ";
            banner.showBanner(prefix.concat(suffix), 1.5, null, 0);
        }

        else {
            PlayerInfoWidget playerWidget = (PlayerInfoWidget) infoBoard.lookup("#" + player);
            playerWidget.updateFoodReserve(foodQuantity);
            prefix = (deltaFood > 0) ? player + " gained "
                    : player + " lost ";
            notificationManager.addInfoNotification(prefix.concat(suffix));
        }
    }



    public void showPrestigeModified(String player, int deltaPP, int finalPP) {
        String suffix = abs(deltaPP) + " Prestige Points";
        String prefix;

        if(player.equals(localPlayer)){
            AnimationsUtils.animateLabelUpdate(prestigePoints, String.valueOf(finalPP));
            prefix = (deltaPP > 0) ? "You gained "
                    : "You lost ";
            if(gui.getClientController().getLocalModel().getCurrentPhase() != GamePhase.END_GAME) {
                banner.showBanner(prefix.concat(suffix), 1.5, null, 0);
            }
        }

        else {
            PlayerInfoWidget playerWidget = (PlayerInfoWidget) infoBoard.lookup("#" + player);
            playerWidget.updatePrestigePoints(finalPP);
            prefix = (deltaPP > 0) ? player + " gained "
                    : player + " lost ";
            notificationManager.addInfoNotification(prefix.concat(suffix));
        }
    }

    public void showShamanStarsModified(String playerName, int stars) {
        if(playerName.equals(localPlayer)) {
            AnimationsUtils.animateLabelUpdate(this.shamanStars, String.valueOf(stars));
        }
        else {
            PlayerInfoWidget playerInfoWidget = (PlayerInfoWidget) infoBoard.lookup("#" + playerName);
            playerInfoWidget.updateShamanStars(stars);
        }
    }

    public void showBuildersDiscountModified(String playerName, int discount) {
        if (playerName.equals(localPlayer)) {
            AnimationsUtils.animateLabelUpdate(this.buildingsDiscount, String.valueOf(discount));
        }
        else {
            PlayerInfoWidget playerInfoWidget = (PlayerInfoWidget) infoBoard.lookup(("#" + playerName));
            playerInfoWidget.updateBuildersDiscount(discount);
        }
    }

    public void showGatherersDiscountModified(String playerName, int discount) {
        if (playerName.equals(localPlayer)) {
            AnimationsUtils.animateLabelUpdate(this.sustenanceDiscount, String.valueOf(discount));
        }
        else {
            PlayerInfoWidget playerInfoWidget = (PlayerInfoWidget) infoBoard.lookup(("#" + playerName));
            playerInfoWidget.updateGathererDiscount(discount);
        }
    }


    /**
     * Clears all gameplay elements and moves the player to the final scoreboard scene.
     */
    public void showEndGame() {
        gameBoardController.clearRows();
        gameBoardController.showRemainingCards(false);
        AnimationsUtils.animateLabelUpdate(phaseStatus, "End Game");

        ClientModel model = gui.getClientController().getLocalModel();

        int finalPP = model.getPlayerTribe(localPlayer).getPrestigePoints();
        AnimationsUtils.animateLabelUpdate(prestigePoints, String.valueOf(finalPP));
        int finalFood = model.getPlayerTribe(localPlayer).getFoodReserve();
        AnimationsUtils.animateLabelUpdate(foodReserve, String.valueOf(finalFood));

        for(Node node: infoBoard.getChildren()) {
            if(node instanceof PlayerInfoWidget playerWidget) {
                String name = playerWidget.getPlayerName();
                finalPP = model.getPlayerTribe(name).getPrestigePoints();
                finalFood = model.getPlayerTribe(name).getFoodReserve();
                playerWidget.updatePrestigePoints(finalPP);
                playerWidget.updateFoodReserve(finalFood);
            }
        }

        banner.showBanner("Game Ended!\nYou will be brought to final ranking", 3, () -> gui.rankingScene(), 0);
    }


    /**
     * Drives the slide transition animation for opening or closing the tribe details panel.
     */
    private void toggleTribeDrawer() {
        transition.stop();
        double amountToHide = tribeRegion.getHeight() - tribeHeader.getHeight() - 50.0;
        if (isTribeOpen) {
            transition.setFromY(-35.0);
            transition.setToY(amountToHide);
            isTribeOpen = false;
        } else {
            transition.setFromY(amountToHide);
            transition.setToY(-35.0);
            isTribeOpen = true;
        }
        transition.play();
    }

    /**
     * Action callback fired when the user selects a specific card from the board.
     * Forwards the action request to the client logic controller layer.
     */
    @Override
    public void onDrawCardRequested(boolean fromTopRow, boolean fromBuildings, int index){
        try {
            gui.getClientController().drawCard(fromTopRow, fromBuildings, index);
        } catch (IllegalClientStateActionException | InvalidSelectionException e) {
            if(e.getCause() != null) {
                notificationManager.addWarning(e.getMessage() + e.getCause().getMessage());
            } else {
                notificationManager.addWarning(e.getMessage());
            }
        }
    }


    /**
     * Action callback fired when the user selects an empty track slot during the totem placement phase.
     */
    @Override
    public void onPlaceTotemRequested(int index){
        try {
            gui.getClientController().chooseOfferTile(index);
            gameBoardController.toggleSelectableTiles(false);
        } catch (IllegalClientStateActionException | OccupiedTileException e) {
            if(e.getCause() != null) {
                notificationManager.addWarning(e.getMessage() + e.getCause().getMessage());
            }
            notificationManager.addWarning(e.getMessage());
        }
    }


    /**
     * Explicitly requests passing actions once a player has completed all permitted card draws.
     */
    public void onPassTurnRequested() {
        try {
            gui.getClientController().passTurn();
            gameBoardController.toggleSelectableCards(false);
        } catch (IllegalClientStateActionException e) {
            notificationManager.addWarning(e.getMessage());
        }
    }


    /**
     * Triggers a pop-up modal confirmation asking the player if they really want to leave
     * the current match.
     */
    @FXML
    public void handleLeaveGame() {
        ConfirmationDialog dialog = new ConfirmationDialog("Leave Game", "Are you sure you want to leave?" +
                "\nThis will end the match for all other players\nand you will not be able to return.");
        dialog.showAndWait().ifPresent(response -> {
            if(response.getButtonData() == ButtonBar.ButtonData.YES) {
                try {
                    gui.getClientController().leaveGame();
                } catch (IllegalClientStateActionException e) {
                    notificationManager.addWarning(e.getMessage());
                }
            }
        });
    }


    /**
     * Displays a critical error screen and disconnects players when network loss or
     * an unexpected client exit forces the game session to end abruptly.
     * @param playerName The nickname of the player who dropped/left.
     */
    public void showCriticalDisconnection(String playerName) {
        String message = (playerName.equals(localPlayer)) ? "You"
                                                        : playerName;
        String suffix = " left the game. You will be brought to setup.";
        banner.showBanner(message.concat(suffix), 3, this::goBackToSetup, 0);
    }

    /**
     * Redirects the window scene layout root back to the initial setup/match-making screen.
     */
    public void goBackToSetup() {
        try {
            gui.showCreationChoiceScene();
        } catch (IOException e) {
            notificationManager.addWarning("failed to going back to setup scene");
            throw new RuntimeException();
        }
    }

    /**
     * Forces a warning notification to display.
     * @param errorMessage Text details explaining what went wrong.
     */
    public void showError(String errorMessage) {
        notificationManager.addWarning(errorMessage);
    }
}