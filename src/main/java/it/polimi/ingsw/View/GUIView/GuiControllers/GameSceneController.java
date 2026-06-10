package it.polimi.ingsw.View.GUIView.GuiControllers;

import it.polimi.ingsw.CustomException.IllegalClientStateActionException;
import it.polimi.ingsw.CustomException.OccupiedTileException;
import it.polimi.ingsw.CustomException.UIException.InvalidSelectionException;
import it.polimi.ingsw.Enums.*;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.Game.Game;
import it.polimi.ingsw.View.GUIView.GUISettings;
import it.polimi.ingsw.View.GUIView.Gui;
import it.polimi.ingsw.View.GUIView.Utils.*;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
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
            //building.setID(card.getCardID());
            buildingColumn.getChildren().add(building);
            //buildings effects are activated by notifyEffect
        }

        else {
            int charactersNum = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getPopulationSize();
            AnimationsUtils.animateLabelUpdate(charactersNumber, String.valueOf(charactersNum));

            CharacterCard character = (CharacterCard) card;
            Node characterImage = ImageManager.getCardNode(id);

            int newNumberPerRole;
            switch (character.getRole()) {
                case ARTIST -> {
                    addCardToColumn(artistColumn, characterImage);
                    newNumberPerRole = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getArtistsNumber();
                    AnimationsUtils.animateLabelUpdate(artistsNumber, String.valueOf(newNumberPerRole));
                }
                case BUILDER -> {
                    addCardToColumn(builderColumn, characterImage);
                    newNumberPerRole = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getBuildersNumber();
                    AnimationsUtils.animateLabelUpdate(buildersNumber, String.valueOf(newNumberPerRole));
                    int buildingDiscount = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getBuildersDiscount();
                    AnimationsUtils.animateLabelUpdate(buildingsDiscount, String.valueOf(buildingDiscount));
                    int prestigePoints = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getPrestigePoints();
                    AnimationsUtils.animateLabelUpdate(this.prestigePoints, String.valueOf(prestigePoints));
                }
                case GATHERER -> {
                    addCardToColumn(gathererColumn, characterImage);
                    newNumberPerRole = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getGatherersNumber();
                    AnimationsUtils.animateLabelUpdate(gatherersNumber, String.valueOf(newNumberPerRole));
                    int gathererDiscount = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getGatherersDiscount();
                    AnimationsUtils.animateLabelUpdate(this.sustenanceDiscount, String.valueOf(gathererDiscount));
                }
                case HUNTER -> {
                    addCardToColumn(hunterColumn, characterImage);
                    newNumberPerRole = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getHuntersNumber();
                    AnimationsUtils.animateLabelUpdate(huntersNumber, String.valueOf(newNumberPerRole));
                }
                //todo: add invntor types
                case INVENTOR -> {
                    addCardToColumn(inventorColumn, characterImage);
                    Map<InventorType, Integer> inventorsPerType = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getInventorsPerType();
                    newNumberPerRole = inventorsPerType.values().stream()
                            .mapToInt(Integer::intValue)
                            .sum();
                    AnimationsUtils.animateLabelUpdate(inventorsNumber, String.valueOf(newNumberPerRole));
                }
                case SHAMAN -> {
                    addCardToColumn(shamanColumn, characterImage);
                    newNumberPerRole = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getShamansNumber();
                    AnimationsUtils.animateLabelUpdate(shamansNumber, String.valueOf(newNumberPerRole));
                    int shamanStars = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getShamansStars();
                    AnimationsUtils.animateLabelUpdate(this.shamanStars, String.valueOf(shamanStars));
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


    //può essere che i banner si sovrappongano, verificare
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

    public GameNotificationManager getNotificationManager() {
        return this.notificationManager;
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

    //TODO: non viene invocato correttamente
    public void showNewEra(int era) {
        String prefix = gameBoardController.setDeckImage(era);
        banner.showBanner(prefix + "started!", 1.5, null, 0);
        AnimationsUtils.animateLabelUpdate(eraStatus, String.valueOf(era));
    }

    public void showFoodBonusTile(String player, int foodBonus) {
        int foodReserve = gui.getClientController().getLocalModel().getPlayerTribe(player).getFoodReserve();
        if(player.equals(localPlayer)) {
            AnimationsUtils.animateLabelUpdate(this.foodReserve, String.valueOf(foodReserve));
            banner.showBanner("You gained " + foodBonus + "units of food from Tile A", 1.5, null, 0);
        } else {
            PlayerInfoWidget playerWidget = (PlayerInfoWidget) infoBoard.lookup("#" + player);
            playerWidget.updateFoodReserve(foodReserve);
            String message = player + " gained " + foodBonus + " units of food from Tile A";
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
        String prefix = "You have drawn ";
        if(player.equals(localPlayer)){
            addCardToTribe(card, fromBuildings);

            gameBoardController.updateRemainingCards();
            gameBoardController.toggleSelectableCards(true);
        } else if(!fromBuildings){
            PlayerInfoWidget playerWidget = (PlayerInfoWidget) infoBoard.lookup("#" + player);
            playerWidget.updateCardDrawn(card);
            prefix = player + "has drawn ";
        }

        banner.showBanner(prefix + "this card", cardImage, 1.5, null, 0);
    }

    public void showNewCurrentPlayer(String player, ClientState clientState){
        String message;
        switch (clientState) {
            case ClientState.NOT_IN_TURN -> {
                if(!player.equals(gui.getClientController().getLocalModel().getTurnOrder().getFirst())) {
                    GamePhase phase = gui.getClientController().getLocalModel().getCurrentPhase();
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
                if(!player.equals(gui.getClientController().getLocalModel().getTurnOrder().getFirst())) {
                    message = "It's your turn to place the totem";
                    banner.showBanner(message, 1.5, null, 0);
                }
                gameBoardController.showRemainingCards(false);
                gameBoardController.toggleSelectableCards(false);
                gameBoardController.toggleSelectableTiles(true);
            }

            default -> {
                if(!player.equals(gui.getClientController().getLocalModel().getTurnOrder().getFirst())) {
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
            banner.showBanner(message.toString(), 1.5, null, 0);

            String foodReserve = String.valueOf(gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getFoodReserve());
            AnimationsUtils.animateLabelUpdate(this.foodReserve, foodReserve);
            String prestigePoints = String.valueOf(gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getPrestigePoints());
            AnimationsUtils.animateLabelUpdate(this.prestigePoints, prestigePoints);
        } else {
            PlayerInfoWidget playerWidget = (PlayerInfoWidget) infoBoard.lookup("#" + player);
            //cannot use foodModified and ppModified because they are deltas,
            // it is more reliable to get absolute values from Local Model
            int food = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getFoodReserve();
            playerWidget.updateFoodReserve(food);
            int pp = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getPrestigePoints();
            playerWidget.updatePrestigePoints(pp);

            String message = eventType.toString() + " event\n" + player + ": " + foodModified + " food" +
                    ppModified + " prestige Points";
            notificationManager.addInfoNotification(message);
        }
    }

    //foodQuantity is new value of player's foodReserve
    public void showFoodModified(String player, int foodQuantity){
        String prefix;
        int deltaFood;
        if(player.equals(localPlayer)){
            deltaFood =  foodQuantity - Integer.parseInt(foodReserve.getText());
            AnimationsUtils.animateLabelUpdate(foodReserve, String.valueOf(foodQuantity));
            prefix = (deltaFood > 0) ? "You gained "
                    : "You lost ";
        } else {
            PlayerInfoWidget playerWidget = (PlayerInfoWidget) infoBoard.lookup("#" + player);
            deltaFood = foodQuantity - playerWidget.getFoodReserve();
            playerWidget.updateFoodReserve(foodQuantity);
            prefix = (deltaFood > 0) ? player + " gained "
                    : player + " lost ";
        }
        banner.showBanner(prefix + deltaFood + "units of food", 1.0, null, 0);
    }

    public void showPrestigeModified(String player, int pp) {
        String prefix;
        int deltaPrestige;

        String clientPlayer = gui.getClientController().getPlayerName();
        if(player.equals(clientPlayer)){
            deltaPrestige =  pp - Integer.parseInt(prestigePoints.getText());
            AnimationsUtils.animateLabelUpdate(prestigePoints, String.valueOf(pp));
            prefix = (deltaPrestige > 0) ? "You gained "
                    : "You lost ";
        } else {
            PlayerInfoWidget playerWidget = (PlayerInfoWidget) infoBoard.lookup("#" + player);
            deltaPrestige = pp - playerWidget.getPrestigePoints();
            playerWidget.updatePrestigePoints(pp);
            prefix = (deltaPrestige > 0) ? player + " gained "
                    : player + " lost ";
        }
        banner.showBanner(prefix + deltaPrestige + " prestige points", 1.0, null, 0);
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
}