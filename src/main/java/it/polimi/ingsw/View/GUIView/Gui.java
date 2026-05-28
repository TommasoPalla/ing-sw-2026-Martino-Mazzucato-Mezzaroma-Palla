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

public class Gui implements ViewInterfaceGui, ViewInterface {

    private ClientController controller;
    private final Stage primaryStage;
    private String nickname;
    private ChooseGameIdController ChooseId;
    private LobbySceneController lobby;
    private ArrayList<String> players;
    private GameSceneController gameScene;

    private ClientState guiState = ClientState.SETUP;       //va capito come sfruttare sta cosa e gestirla bene

    public Gui(Stage stage){

        this.primaryStage = stage;
        this.players = new ArrayList<>();
    }

    public void bindController(ClientController Controller){
        controller = Controller;
    }

    public ClientController getClientController(){
        return this.controller;
    }

    public ArrayList<String> getPlayers(){
        return players;
    }
    public void removeMe(){
        players.remove(nickname);
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
            primaryStage.show();
            PauseTransition delay = new PauseTransition(Duration.seconds(3));//splash iniziale dura 3 secondi

            delay.setOnFinished(event -> {

                try {
                    nicknameScene();

                } catch (Exception e) {
                    System.out.println("Error: " + e);
                }
            });

            delay.play();

        } catch (Exception e) {

            System.out.println("Error: " + e);
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

    }

    @Override
    public void notifyGameCreated(int gameID) {
        Platform.runLater(() -> {//avoids to throw exception required by lobbyScene
            try{
                lobbyScene();
            }catch(Exception e){
                e.printStackTrace();
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
                e.printStackTrace();
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
        }
        players=playerNames;
    }

    @Override
    public void notifyGameStarted() {
        //try catch da fare meglio
            playGameScene();

    }

    @Override
    public void notifyStartRound(int round) {

    }

    @Override
    public void notifyChosenTotemColor(String playerName, Color totemColor) {

    }

    @Override
    public void notifyGiveInitialFood(Map<String, Integer> initialFood) {

    }

    @Override
    public void notifyTileChosen(String playerName, int index) {

    }

    @Override
    public void notifyCardDrawn(String player, Card card, boolean topRow, boolean fromBuildings) {
        if(gameScene != null){
            Platform.runLater(() -> {
                gameScene.showCardDrawn(player, card, topRow, fromBuildings);
            });
        }
    }

    @Override
    public void notifyNewGamePhase(GamePhase newGamePhase) {

    }

    @Override
    public void notifyFood(String playerName, int food) {

    }

    @Override
    public void notifyPrestigePoints(String playerName, int pp) {

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/ChooseNickName.fxml"));
            Parent root = loader.load();
            ChooseNickNameController controller = loader.getController();
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
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e){
            System.out.println("Error: "+ e);
        }

    }

    @Override
    public void lobbyScene() throws IOException{
        try {
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
