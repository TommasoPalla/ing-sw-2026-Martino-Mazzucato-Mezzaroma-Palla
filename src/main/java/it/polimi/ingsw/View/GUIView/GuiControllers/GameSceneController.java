package it.polimi.ingsw.View.GUIView.GuiControllers;

import it.polimi.ingsw.CustomException.IllegalClientStateActionException;
import it.polimi.ingsw.CustomException.OccupiedTileException;
import it.polimi.ingsw.Enums.*;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.GameBoard.OfferTile;
import it.polimi.ingsw.View.GUIView.GUISettings;
import it.polimi.ingsw.View.GUIView.Gui;
import it.polimi.ingsw.View.GUIView.Utils.*;
import javafx.animation.PauseTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import javafx.scene.image.ImageView;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;


//TODO: gestire tutte le interazioni dei totem
//TODO aggiungere icone sopra colonne di personaggi

public class GameSceneController implements BoardActionListener {
    private Gui gui;
    private GameSceneBanner banner;
    private GameNotificationManager notificationManager;
    private boolean isTribeOpen = false;
    private String localPlayer;
    private TranslateTransition transition;

    private static final String BACK_ERA_1 = "/Images/CardImages/Era_1.png";
    private static final String BACK_ERA_2 = "/Images/CardImages/Era_2.png";
    private static final String BACK_ERA_3 = "/Images/CardImages/Era_3.png";

    @FXML
    private AnchorPane mainAnchor;
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
        updateInitialGameState();
    }

    @FXML
    public void initialize() {
        topRow.setVisible(false);
        bottomRow.setVisible(false);
        topBuildings.setVisible(false);
        bottomBuildings.setVisible(false);
        tribeRegion.setVisible(false);
        setDeckImage(1);

        transition = new TranslateTransition(Duration.millis(300), tribeRegion);

        //Using platform.runLater to be sure correct dimensions have been calculated
        javafx.application.Platform.runLater(() -> {
            double amountToHide = tribeRegion.getHeight() - tribeHeader.getHeight() - 50.0;
            tribeRegion.setTranslateY(amountToHide);
            //On mouse clicked tribe region will appear/disappear
            tribeHeader.setOnMouseClicked(event -> {
                toggleTribeDrawer(amountToHide);
            });
        });
        this.banner = new GameSceneBanner(mainAnchor);
        this.notificationManager = new GameNotificationManager(mainAnchor);
    }


    private void updateInitialGameState(){
        initOfferTrack();
        initInfoBoard();
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
            TileComponent tileRepresentation = new TileComponent(tile.getTileCode(), i, this);
            offerTrack.getChildren().add(tileRepresentation.getGraphicsNode());
        }
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


    private void updateTopRow() {
        // Svuota i vecchi componenti grafici prima del ripristino della fila
        ArrayList<Card> cards = gui.getClientController().getLocalModel().getTopRow();
        topRow.getChildren().clear();

        int i = 0;
        // Popola dinamicamente il contenitore
        for (Card card : cards) {

            CardComponent cardRepresentation = new CardComponent(card.getCardID(), true, false,
                    i, this);

            // Aggiunge il nodo grafico al layout lineare
            topRow.getChildren().add(cardRepresentation.getGraphicsNode());
        }
    }

    private void updateBottomRow() {
        ArrayList<Card> cards = gui.getClientController().getLocalModel().getBottomRow();
        bottomRow.getChildren().clear();

        int i = 0;
        // Popola dinamicamente il contenitore
        for (Card card : cards) {

            CardComponent cardRepresentation = new CardComponent(card.getCardID(), false, false,
                    i, this);

            // Aggiunge il nodo grafico al layout lineare
            bottomRow.getChildren().add(cardRepresentation.getGraphicsNode());
        }
    }

    private void updateTopBuildings() {
        ArrayList<BuildingCard> cards = gui.getClientController().getLocalModel().getTopBuildings();
        topBuildings.getChildren().clear();

        int i = 0;
        for (BuildingCard building : cards) {

            CardComponent cardRepresentation = new CardComponent(building.getCardID(), true, true,
                    i, this);

            topBuildings.getChildren().add(cardRepresentation.getGraphicsNode());
        }
    }

    private void updateBottomBuildings() {
        ArrayList<BuildingCard> cards = gui.getClientController().getLocalModel().getBottomBuildings();
        bottomBuildings.getChildren().clear();

        int i = 0;
        for (BuildingCard building : cards) {

            CardComponent cardRepresentation = new CardComponent(building.getCardID(), false, true,
                    i, this);

            bottomBuildings.getChildren().add(cardRepresentation.getGraphicsNode());
        }
    }

    private void addCardToTribe(Card card, boolean isBuilding){
        String id = card.getCardID();
        if (isBuilding) {
            /*notifyPrestige e notifyFood fanno lo stesso lavoro
            int newPrestige = gui.getClientController().getLocalModel().getPlayerTribe(player).getPrestigePoints();
            prestigePoints.setText(String.valueOf(newPrestige));

            int newFood = gui.getClientController().getLocalModel().getPlayerTribe(player).getFoodReserve();
            foodReserve.setText(String.valueOf(newFood));*/

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
                    artistColumn.getChildren().addFirst(characterImage);
                    newNumberPerRole = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getArtistsNumber();
                    AnimationsUtils.animateLabelUpdate(artistsNumber, String.valueOf(newNumberPerRole));
                }
                case BUILDER -> {
                    builderColumn.getChildren().addFirst(characterImage);
                    newNumberPerRole = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getBuildersNumber();
                    AnimationsUtils.animateLabelUpdate(buildersNumber, String.valueOf(newNumberPerRole));
                    /*int newPrestige = gui.getClientController().getLocalModel().getPlayerTribe(player).getPrestigePoints();
                    prestigePoints.setText(String.valueOf(newPrestige));
                    int builderDiscount = gui.getClientController().getLocalModel().getPlayerTribe(player).getBuildersDiscount();
                    this.buildingsDiscount.setText(String.valueOf(builderDiscount));*/
                }
                case GATHERER -> {
                    gathererColumn.getChildren().addFirst(characterImage);
                    newNumberPerRole = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getGatherersNumber();
                    AnimationsUtils.animateLabelUpdate(gatherersNumber, String.valueOf(newNumberPerRole));
                    /*int sustenanceDiscount = gui.getClientController().getLocalModel().getPlayerTribe(player).getBuildersDiscount();
                    this.sustenanceDiscount.setText(String.valueOf(sustenanceDiscount));*/
                }
                case HUNTER -> {
                    hunterColumn.getChildren().addFirst(characterImage);
                    newNumberPerRole = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getHuntersNumber();
                    AnimationsUtils.animateLabelUpdate(huntersNumber, String.valueOf(newNumberPerRole));

                    /*int newFood = gui.getClientController().getLocalModel().getPlayerTribe(player).getFoodReserve();
                    foodReserve.setText(String.valueOf(newFood));*/
                }
                case INVENTOR -> {
                    inventorColumn.getChildren().addFirst(characterImage);
                    Map<InventorType, Integer> inventorsPerType = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getInventorsPerType();
                    newNumberPerRole = inventorsPerType.values().stream()
                            .mapToInt(Integer::intValue)
                            .sum();
                    AnimationsUtils.animateLabelUpdate(inventorsNumber, String.valueOf(newNumberPerRole));
                }
                case SHAMAN -> {
                    shamanColumn.getChildren().addFirst(characterImage);
                    newNumberPerRole = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getShamansNumber();
                    AnimationsUtils.animateLabelUpdate(shamansNumber, String.valueOf(newNumberPerRole));
                    int shamanStars = gui.getClientController().getLocalModel().getPlayerTribe(localPlayer).getShamansStars();
                    AnimationsUtils.animateLabelUpdate(this.shamanStars, String.valueOf(shamanStars));
                }
            }
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

        banner.showBanner(message, 4, this::showRowsComponent, 1);
    }

    public void showRowsComponent() {
        topRow.setVisible(true);
        bottomBuildings.setVisible(false);  //necessarily void at the beginning of the game
        topBuildings.setVisible(true);
        bottomRow.setVisible(true);
        tribeRegion.setVisible(true);
    }

    public GameNotificationManager getNotificationManager() {
        return this.notificationManager;
    }

    public void showNewRound(int round) {
        AnimationsUtils.animateLabelUpdate(roundStatus, String.valueOf(round));
        AnimationsUtils.animateLabelUpdate(phaseStatus, GamePhase.START_TURN.toString());

        updateTopRow();
        updateBottomRow();
        updateBottomBuildings();
        updateTopBuildings();

        //verifcare che sia il giocatore giusto (già aggiornato)
        String firstPlayer = gui.getClientController().getLocalModel().getTurnOrder().getFirst();
        String prefix = (firstPlayer.equals(localPlayer)) ? "your "
                : firstPlayer + "'s ";

        AnimationsUtils.animateLabelUpdate(playerStatus, firstPlayer);
        banner.showBanner("Round " + round + "!\nIt's " + prefix + "turn", 1.5, null, 0);
    }

    public void showNewEra(int era) {
        String prefix = setDeckImage(era);
        banner.showBanner(prefix + "started!", 1.5, null, 0);
        AnimationsUtils.animateLabelUpdate(eraStatus, String.valueOf(era));
        bottomBuildings.setVisible(true); //sarebbe meglio spostarlo per pulizia
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


    private String setDeckImage (int era) {
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

    public void showCardDrawn(String player, Card card, boolean topRow, boolean fromBuildings){
        Node cardImage = ImageManager.getCardNode(card.getCardID());
        String prefix = "You have ";
        if(player.equals(localPlayer)){
            addCardToTribe(card, fromBuildings);
            //prefix  = "You have ";
        } else if(!fromBuildings){
            PlayerInfoWidget playerWidget = (PlayerInfoWidget) infoBoard.lookup("#" + player);
            playerWidget.updateCardDrawn(card);
            prefix = player + "has drawn ";
        } /*else {
            int foodQuantity = gui.getClientController().getLocalModel().getPlayerTribe(player).getFoodReserve();
            showFoodModified(player, foodQuantity);
            prefix = player + "has drawn ";
        }*/

        banner.showBanner(prefix + "this card", cardImage, 1.5, null, 0);

        if(fromBuildings){
            if(topRow){
                updateTopBuildings();
            } else {
                updateBottomBuildings();
            }
        } else if(topRow){
            updateTopRow();
        } else {
            updateBottomRow();
        }
    }

    public void showNewCurrentPlayer(String player, ClientState clientState){
        //si potrebbe aggiungere che quando il player non quello in locale per lui le carte non sono evidenziate ecc
        String gamePhase;
        if(clientState == ClientState.DRAW_CARD) {
            gamePhase = " to draw";
        } else {    //if (clientState == ClientState.PLACE_TOTEM)
            gamePhase = " to place the totem";
        }

        String message;
        if(!player.equals(gui.getClientController().getLocalModel().getTurnOrder().getFirst())) {
            if(localPlayer.equals(player)){
                message = "It's your turn" + gamePhase;
                banner.showBanner(message, 1.5, null, 0);
            } else {
                message = "It's " + player + "'s turn" + gamePhase;
                notificationManager.addInfoNotification(message);
            }
        }
        AnimationsUtils.animateLabelUpdate(playerStatus, player);
    }

    //TODO: legata a notifyNewGamePhase
    public void showNewGamePhase(GamePhase phase){
        //
         //
        AnimationsUtils.animateLabelUpdate(phaseStatus, phase.toString());
    }


    public void showEventEffects(String player, EventType eventType, int foodModified, int ppModified) {
        AnimationsUtils.animateLabelUpdate(phaseStatus, GamePhase.ON_EVENT.toString());
        if(player.equals(localPlayer)) {
            StringBuilder message = new StringBuilder(String.valueOf(eventType));
            message.append(" event resolved!\nYou ");
            String variation = (foodModified > 0) ? "gained "
                                                : "lost ";
            message.append(variation).append(String.valueOf(foodModified));
            variation = (ppModified > 0) ? "\nYou gained "
                                        : "\nYou lost ";
            message.append(variation).append(String.valueOf(ppModified));
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

    /**
     * Gestisce l'animazione di salita e discesa del pannello della tribù
     */
    private void toggleTribeDrawer(double translationAmount) {
        if (isTribeOpen) {
            // Se è aperto, lo riposizioniamo verso il basso nascondendo le carte
            transition.setToY(translationAmount);
            isTribeOpen = false;
        } else {
            // Se è chiuso, lo riportiamo alla sua posizione Y naturale facendolo salire
            transition.setToY(-35.0);
            isTribeOpen = true;
        }
        // Avvia l'animazione fluida
        transition.play();
    }

    @Override
    public void onDrawCardRequested(boolean fromTopRow, boolean fromBuildings, int index){
        //try {
        gui.getClientController().drawCard(fromTopRow, fromBuildings, index);
    }

    @Override
    public void onPlaceTotemRequested(int index){
        try {
            gui.getClientController().chooseOfferTile(index);
        } catch (IllegalClientStateActionException | OccupiedTileException e) {
            notificationManager.addWarning(e.getMessage());
        }
    }
}