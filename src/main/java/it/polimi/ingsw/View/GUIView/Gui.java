package it.polimi.ingsw.View.GUIView;

import it.polimi.ingsw.Controller.ClientController.ClientController;
import it.polimi.ingsw.Enums.ClientState;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.View.GUIView.GuiControllers.*;
import it.polimi.ingsw.View.ViewInterface;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Gui implements ViewInterfaceGui, ViewInterface {

    private static final Logger LOGGER = Logger.getLogger(Gui.class.getName());

    private ClientController controller;
    private final Stage primaryStage;
    private String nickname;
    private ChooseGameIdController ChooseId;
    private LobbySceneController lobby;
    private ArrayList<String> players;
    private GameSceneController gameScene;
    private boolean isHost;
    private ClientState guiState = ClientState.SETUP;       //va capito come sfruttare sta cosa e gestirla bene
    private int numPlayers;

    public Gui(Stage stage){

        this.primaryStage = stage;
        this.players = new ArrayList<>();
        this.isHost = false;
        this.numPlayers = 0;
    }

    public void bindController(ClientController Controller){
        controller = Controller;
    }

    public ClientController getClientController(){
        return this.controller;
    }

    public void setNumPlayers(int num){
        numPlayers=num;
    }

    public int getNumPlayers(){
        return(numPlayers);
    }

    public ArrayList<String> getPlayers(){
        return players;
    }
    public void removePlayers(){
        players.clear();
    }

    public boolean returnHost(){
        return this.isHost;
    }

    public void handleLeave(){
        this.isHost = false;
    }

    @Override
    public void runView() {

    }

    @Override
    public void introScene() {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/IntroScene.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.setMaximized(true);
            primaryStage.show();
            PauseTransition delay = new PauseTransition(Duration.seconds(3));//splash iniziale dura 3 secondi

            delay.setOnFinished(event -> {

                try {
                    nicknameScene();

                } catch (Exception e) {
                    e.printStackTrace();;
                }
            });

            delay.play();

        } catch (Exception e) {
            e.printStackTrace();;
        }
    }//-> nickname

    @Override
    public void notifyNewAvailableGames() {

    }

    @Override
    public void notifyNameSet(String newName) {

    }

    @Override
    public void notifyNewCurrentPlayer(String playerName, ClientState clientState) {
        if(gameScene != null) {
            Platform.runLater(() -> {
                try {
                    gameScene.showNewCurrentPlayer(playerName);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to set new current player", e);
                }
            });
        }
    }

    @Override
    public void notifyGameCreated(int gameID) {
        Platform.runLater(() -> {//avoids to throw exception required by lobbyScene
            try{
                lobbyScene();
            }catch(Exception e){
                LOGGER.log(Level.SEVERE, "Failed to display lobby scene", e);
            }
        });
    }

    @Override
    public void notifyPlayerJoinedLobby(String playerName) {
        Platform.runLater(() -> {//avoids to throw exception required by lobbyScene
            try{
                lobby.notifyPlayerJoined(playerName);
                //ChooseId.loadGames(controller.getAvailableGames());//private controllers keep the reference to the active ones
                players.add(playerName);
            }catch(Exception e){
                LOGGER.log(Level.WARNING, "Failed to display player joining lobby", e);
            }
        });
    }

    @Override
    public void notifyPlayerLeftLobby(String playerName, Color oldColor) {
        lobby.notifyPlayerLeft(playerName);
        ChooseId.loadGames(controller.getAvailableGames());
        players.remove(playerName);
    }

    @Override
    public void notifyNewHost() {
        this.isHost = true;
        lobby.newHost();
    }

    @Override
    public void notifySuccessfullyJoinedGame(int gameID, ArrayList<String> playerNames, Map<String, Color> totemColors) {
        for(String playerName : playerNames){
            /*
            mezzo sbagliato. siccome notifyPlayerJoined era un metodo pensato per chi e' gia' in lobby
            prende il nome passato e lo stampa dicendo "ha joinato". in questo caso pero' non e' chi
            e' passato per parametro a joinare perche' era gia' in lobby. e' una cosa di cui possiamo
            non preoccuparci*/
            lobby.notifyPlayerJoined(playerName);
            //TODO: da fare anche la notifica di scelta dei totem in base a totemColors (gia' presi)
            lobby.joined();
        }
        players=playerNames;
    }

    @Override
    public void notifyGameStarted() {
        //try catch da fare meglio
        Platform.runLater(() -> {
            playGameScene();
        });

    }

    @Override
    public void notifyStartRound(int round) {
        if(gameScene != null) {
            Platform.runLater(() -> {
                try {
                    gameScene.showNewRound(round);
                } catch(Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to display start round", e);
                }
            });
        }
    }

    @Override
    public void notifyChosenTotemColor(String playerName, Color totemColor) {

    }

    @Override
    public void notifyGiveInitialFood(Map<String, Integer> initialFood) {
        if (gameScene != null) {
            Platform.runLater(() -> {
                try {
                    gameScene.showInitialFood(initialFood);
                } catch (Exception e){
                    LOGGER.log(Level.SEVERE, "Failed to display initial food", e);
                }
            });
        }
    }

    @Override
    public void notifyTileChosen(String playerName, int index) {

    }

    @Override
    public void notifyFoodBonusTile(String playerName, int foodBonus) {

    }

    @Override
    public void notifyCardDrawn(String player, Card card, boolean topRow, boolean fromBuildings) {
        if(gameScene != null){
            Platform.runLater(() -> {
                try {
                    gameScene.showCardDrawn(player, card, topRow, fromBuildings);
                } catch(Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to display card drawn", e);
                }
            });
        }
    }

    @Override
    public void notifyEraChanged(int era) {
        if (gameScene != null) {
            Platform.runLater(() -> {
                try {
                    gameScene.showNewEra(era);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to display era change", e);
                }
            });
        }
    }

    @Override
    public void notifyTurnPassed(String playerThatPassed, String newCurrentPlayer) {

    }

    @Override
    public void notifyNewGamePhase(GamePhase newGamePhase) {

    }

    @Override
    public void notifyFood(String playerName, int food) {
        if(gameScene != null){
            Platform.runLater(() -> {
                try {
                    gameScene.showFoodModified(playerName, food);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to show food update", e);
                }
            });
        }
    }

    @Override
    public void notifyPrestigePoints(String playerName, int pp) {
        if(gameScene != null){
            Platform.runLater(() -> {
                try {
                    gameScene.showPrestigeModified(playerName, pp);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to show prestige update", e);
                }
            });
        }
    }

    @Override
    public void notifyEvent(EventType eventType, int foodModified, int ppModified) {

    }

    @Override
    public void notifyForceQuit(String disconnectedPlayerName, Color oldColor) {

    }

    @Override
    public void notifyPlayerDisconnected(String playerName, boolean hadColor) {

    }

    @Override
    public void notifyEndGame(Map<String, Integer> finalRanking) {
        if(gameScene != null) {
            Platform.runLater(() -> {
                try {
                    gameScene.showEndGame(finalRanking);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to show game ended", e);
                }
            });
        }
    }

    @Override
    public void notifyLeaderboardInfo(int playerPosition) {

    }

    @Override
    public void showCreationChoiceScene() throws IOException {

        Platform.runLater(() -> {//serve a thread, carica la scena appena possibile, lambda e esempio di uso gui con thread
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/chooseToCreate.fxml"));
                Parent root = loader.load();
                ChooseToCreateController Controller = loader.getController();
                Controller.setGUI(this);
                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
                primaryStage.show();
                controller.setClientState(ClientState.CONNECTING);
            }catch (Exception e){
                System.out.println("Error: " + e);
            }

        });
    }//->handleJoin, handleCreate

    @Override
    public void showGameIdScene() {
        try {
            controller.setClientState(ClientState.SETUP);
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/ChooseGameId.fxml"));
            Parent root = loader.load();
            ChooseGameIdController Controller = loader.getController();
            Controller.setGUI(this);//important to pass the gui, this way I can access the valid controller
            Controller.loadGames(controller.getAvailableGames());
            ChooseId=Controller;

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
            Platform.runLater(() -> {
                primaryStage.setMaximized(true);
            });
        }catch (Exception e){
            System.out.println("Error: " + e);
        }
    }

    @Override
    public void nicknameScene() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/ChooseNickName.fxml"));
            Parent root = loader.load();
            ChooseNickNameController controller = loader.getController();
            controller.setGUI(this);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        }catch (Exception e){
            System.out.println("Error: " + e);
        }
    }//->handle Nickname

    @Override
    public void chooseTotemScene() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/ChooseTotemScene.fxml"));
            Parent root = loader.load();
            ChooseTotemController controller = loader.getController();
            controller.setGUI(this);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        }catch(Exception e){
            System.out.println("Error: " + e);

        }
    }

    @Override
    public void playGameScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/GameScene.fxml"));
            Parent root = loader.load();

            GameSceneController controller = loader.getController();
            controller.setGUI(this);
            gameScene = controller;

            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());

            primaryStage.setScene(scene);
            Platform.runLater(() -> {
                primaryStage.setMaximized(false);
                primaryStage.setMaximized(true);
            });
        } catch (IOException e){
            LOGGER.log(Level.SEVERE, "Error loading GameScene", e);
        }
    }

    @Override
    public void lobbyScene() throws IOException{
        try {
            guiState = ClientState.IN_LOBBY;
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/LobbyScene.fxml"));
            Parent root = loader.load();
            LobbySceneController Controller = loader.getController();
            Controller.setGUI(this);
            lobby = Controller;
            Controller.init();

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        }catch(IOException e){
            System.out.println("Error: " + e);
        }
    }

    @Override
    public void chooseNumberOfPlayers() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/ChooseNumberOfPlayers.fxml"));
            Parent root = loader.load();
            ChooseNumberOfPlayersController controller = loader.getController();
            controller.setGUI(this);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            primaryStage.show();
        }catch (Exception e){
            System.out.println("Error: " + e);
        }
    }


    public void handleJoin(){
        showGameIdScene();
    }
    public void handleCreate(){
        chooseNumberOfPlayers();
        players.add(nickname);
        this.isHost = true;
    }
    public void handleNickChange(){
        nicknameScene();
    }

    public void handleGameID(int ID) throws IOException{
        lobbyScene();
        controller.joinGame(ID);
    }

    public void handleNickname(String Nickname) throws IOException{
        nickname = Nickname;
        controller.setPlayerName(nickname);
        showCreationChoiceScene();
    }
    public void handleNumber(int number) throws IOException{
        controller.setClientState(ClientState.SETUP);
        controller.createGame(number);
    }
}