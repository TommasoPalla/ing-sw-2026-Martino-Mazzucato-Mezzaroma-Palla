package it.polimi.ingsw.View.GUIView.GuiControllers;

import it.polimi.ingsw.Controller.ClientController.ClientModel;
import it.polimi.ingsw.Controller.ClientController.LightTribe;
import it.polimi.ingsw.CustomException.IllegalClientStateActionException;
import it.polimi.ingsw.CustomException.OccupiedTileException;
import it.polimi.ingsw.CustomException.UIException.InvalidSelectionException;
import it.polimi.ingsw.Enums.*;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.View.GUIView.Components.GameNotificationManager;
import it.polimi.ingsw.View.GUIView.Components.GameSceneBanner;
import it.polimi.ingsw.View.GUIView.Components.PlayerInfoWidget;
import it.polimi.ingsw.View.GUIView.GUISettings;
import it.polimi.ingsw.View.GUIView.Gui;
import it.polimi.ingsw.View.GUIView.Utils.*;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.Map;


//TODO: gestire tutte le interazioni dei totem
//TODO aggiungere icone sopra colonne di personaggi

public class GameSceneController implements BoardActionListener {
    private Gui gui;

    private GameSceneBanner banner;
    private GameNotificationManager notificationManager;

    private String localPlayer;

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


    public void setup(Gui gui){
        this.gui = gui;
        this.localPlayer = gui.getClientController().getPlayerName();
        initInfoBoard();
        gameBoardController.setup(gui, localPlayer);
    }

    @FXML
    public void initialize() {
        gameBoardController.setActionListener(this);
        tribeRegion.setVisible(false);

        this.defaultColumnSpacing = hunterColumn.getSpacing();
        transition = new TranslateTransition(Duration.millis(300), tribeRegion);
        tribeHeader.setOnMouseClicked(event -> toggleTribeDrawer());

        //Using platform.runLater to be sure correct dimensions have been calculated
        javafx.application.Platform.runLater(() -> {
            double initialAmountToHide = tribeRegion.getHeight() - tribeHeader.getHeight() - 50.0;
            tribeRegion.setTranslateY(initialAmountToHide);
            //On mouse clicked tribe region will appear/disappear
        });
        this.banner = new GameSceneBanner(mainAnchor);
        this.notificationManager = new GameNotificationManager(mainAnchor);
    }


    private void initInfoBoard() {
        Map<String, Color> players = gui.getClientController().getLocalModel().getTotemColors();
        for(String player: players.keySet()){
            if(!player.equals(localPlayer)) {
                PlayerInfoWidget playerInfo = new PlayerInfoWidget(player, players.get(player), gui);
                playerInfo.setId(player);
                infoBoard.getChildren().add(playerInfo);
            }
        }
    }


    private void addCardToTribe(Card card, boolean isBuilding){
        String id = card.getCardID();
        if (isBuilding) {
            Node building = ImageManager.getCardNode(id);
            buildingColumn.getChildren().add(building);
            //buildings effects are activated by notifyEffect
        }

        else {
            LightTribe tribe = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer);

            int charactersNum = tribe.getPopulationSize();
            AnimationsUtils.animateLabelUpdate(charactersNumber, String.valueOf(charactersNum));

            CharacterCard character = (CharacterCard) card;
            Node characterImage = ImageManager.getCardNode(id);

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

    public void addCardToColumn(VBox column, Node cardNode) {
        column.getChildren().addFirst(cardNode);

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
        double amountToHide = tribeRegion.getHeight() - tribeHeader.getHeight() - 50;
        if (!isTribeOpen) {
            transition.stop();
            tribeRegion.setTranslateY(amountToHide);
            transition.setToY(amountToHide);
        }
    }

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

        banner.showBanner(message, 4, () -> tribeRegion.setVisible(true), 1);
    }

    public void showNewRound(int round) {
        AnimationsUtils.animateLabelUpdate(roundStatus, String.valueOf(round));
        AnimationsUtils.animateLabelUpdate(phaseStatus, GamePhase.START_TURN.toString());

        gameBoardController.updateTopRow();
        gameBoardController.updateBottomRow();
        gameBoardController.updateBottomBuildings();
        gameBoardController.updateTopBuildings();

        String firstPlayer = gui.getClientController().getLocalModel().getTurnOrder().getFirst();
        String prefix = (firstPlayer.equals(localPlayer)) ? "your "
                : firstPlayer + "'s ";

        banner.showBanner("Round " + round + "!\nIt's " + prefix + "turn", 1.5, null, 0);
    }

    public void showNewEra(int era) {
        String prefix = gameBoardController.setDeckImage(era);
        banner.showBanner(prefix + "started!", 1.5, null, 0);
        AnimationsUtils.animateLabelUpdate(eraStatus, String.valueOf(era));
    }

    public void showTileChosen(String playerName, int index) {
        gameBoardController.moveTotemToOfferTile(playerName, index);
    }

    public void showTotemToTurnTile(String playerName, int index) {
        gameBoardController.moveTotemToTurnTile(playerName, index);
    }

    public void showFoodBonusTile(String player, int foodBonus) {
        int foodReserve = gui.getClientController().getLocalModel().getPlayerTribe(player).getFoodReserve();
        String suffix = foodBonus + "units of food from Tile A";
        if(player.equals(localPlayer)) {
            AnimationsUtils.animateLabelUpdate(this.foodReserve, String.valueOf(foodReserve));
            banner.showBanner("You gained " + suffix , 1.5, null, 0);
        } else {
            PlayerInfoWidget playerWidget = (PlayerInfoWidget) infoBoard.lookup("#" + player);
            playerWidget.updateFoodReserve(foodReserve);
            String message = player + " gained " + suffix;
            notificationManager.addInfoNotification(message);
        }
    }

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

        Node cardImage = ImageManager.getCardNode(card.getCardID());
        String prefix = player + "has drawn ";
        if(player.equals(localPlayer)){
            addCardToTribe(card, fromBuildings);

            prefix = "You have drawn ";
            gameBoardController.updateRemainingCards();
            gameBoardController.toggleSelectableCards(true);
        }
        else if(!fromBuildings){
            PlayerInfoWidget playerWidget = (PlayerInfoWidget) infoBoard.lookup("#" + player);
            playerWidget.updateCardDrawn(card);
        }

        banner.showBanner(prefix + "this card", cardImage, 1.5, null, 0);
    }

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
        AnimationsUtils.animateLabelUpdate(playerStatus, player);
    }


    //fare un po' di prove per vedere che stampa effettivamente il prossimo giocatore
    public void showNewGamePhase(GamePhase phase){
        StringBuilder messageBuilder = new StringBuilder("It's time to ");
        String currentPlayer = gui.getClientController().getLocalModel().getTurnOrder().getFirst();
        if(phase == GamePhase.ON_DRAW) {
            messageBuilder.append("draw cards");
            messageBuilder.append(currentPlayer.equals(localPlayer)
                    ? "\nIt's your turn!"
                    : "\nIt's " + currentPlayer + "'s turn");
            gameBoardController.toggleSelectableTiles(false);
        } else if (phase == GamePhase.START_TURN){
            messageBuilder.append("place totems");
            messageBuilder.append(currentPlayer.equals(localPlayer)
                    ? "\nIt's your turn!"
                    : "\nIt's " + currentPlayer + "'s turn");
            gameBoardController.toggleSelectableCards(false);
        } else {
            messageBuilder.append("to declare the winner");
            gameBoardController.toggleSelectableTiles(false);
            gameBoardController.toggleSelectableCards(false);

        }
        String message = messageBuilder.toString();
        banner.showBanner(message, 2, null, 0);
        AnimationsUtils.animateLabelUpdate(phaseStatus, phase.toString());
    }


    public void showEventEffects(String player, EventType eventType, int foodModified, int ppModified) {
        AnimationsUtils.animateLabelUpdate(phaseStatus, GamePhase.ON_EVENT.toString());
        LightTribe tribe = gui.getClientController().getLocalModel().getPlayerTribe(player);

        if(player.equals(localPlayer)) {
            StringBuilder message = new StringBuilder(String.valueOf(eventType));
            message.append(" event resolved!\nYou ");
            String variation = (foodModified > 0) ? "gained "
                                                : "lost ";
            message.append(variation).append(String.valueOf(foodModified)).append(" of food");
            variation = (ppModified > 0) ? "\nYou gained "
                                        : "\nYou lost ";
            message.append(variation).append(String.valueOf(ppModified)).append(" prestige Points");
                    //.append("\nThe effects of this event also applied to the other players");
            /*TODO: vorrei mettere l'immagine dell'evento ma risalire a quale era fosse non è facile,
               è già stata tolta dal tabellone?*/
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

    //foodQuantity is new value of player's foodReserve
    public void showFoodModified(String player, int deltaFood, int foodQuantity){
        String suffix = deltaFood + " units of food";
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
        String suffix = deltaPP + " Prestige Points";
        String prefix;

        if(player.equals(localPlayer)){
            AnimationsUtils.animateLabelUpdate(prestigePoints, String.valueOf(finalPP));
            prefix = (deltaPP > 0) ? "You gained "
                    : "You lost ";
            banner.showBanner(prefix.concat(suffix), 1.5, null, 0);
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

    public void showEndGame(Map<String, Integer> finalRanking) {
        //
        AnimationsUtils.animateLabelUpdate(phaseStatus, GamePhase.END_GAME.toString());
        //
    }


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

    @Override
    public void onDrawCardRequested(boolean fromTopRow, boolean fromBuildings, int index){
        try {
            gui.getClientController().drawCard(fromTopRow, fromBuildings, index);
        } catch (IllegalClientStateActionException | InvalidSelectionException e) {
            if(e.getCause() != null) {
                notificationManager.addWarning(e.getMessage() + e.getCause());
            } else {
                notificationManager.addWarning(e.getMessage());
            }
        }
    }

    @Override
    public void onPlaceTotemRequested(int index){
        try {
            gui.getClientController().chooseOfferTile(index);
            gameBoardController.toggleSelectableTiles(false);
        } catch (IllegalClientStateActionException | OccupiedTileException e) {
            if(e.getCause() != null) {
                notificationManager.addWarning(e.getMessage() + e.getCause());
            }
            notificationManager.addWarning(e.getMessage());
        }
    }

    public GameNotificationManager getNotificationManager() {
        return this.notificationManager;
    }
}