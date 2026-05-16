package it.polimi.ingsw.View.GUIView;

import it.polimi.ingsw.Controller.ClientController.ClientController;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Networking.Shared.ServerConnection;
import it.polimi.ingsw.View.GUIView.GuiControllers.ChooseGameIdController;
import it.polimi.ingsw.View.GUIView.GuiControllers.ChooseNickNameController;
import it.polimi.ingsw.View.GUIView.GuiControllers.ChooseNumberOfPlayersController;
import it.polimi.ingsw.View.GUIView.GuiControllers.ChooseToCreateController;
import it.polimi.ingsw.View.ViewInterface;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Map;

public class Gui implements ViewInterfaceGui, ViewInterface {

    private ClientController controller;

    private ServerConnection connection;

    private final Stage primaryStage;

    private String nickname;

    private int gameID;


    public Gui(Stage stage){

        this.primaryStage = stage;
    }

    public void bindController( ClientController Controller){
        controller = Controller;
    }

    public ClientController getClientController(){
        return this.controller;
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
    }

    @Override
    public void notifyNewAvailableGames() {

    }

    @Override
    public void notifyNameModified(String newName) {

    }

    @Override
    public void notifyGameCreated(int gameID) {

    }

    @Override
    public void notifyPlayerJoinedLobby(String playerName) {

    }

    @Override
    public void notifyPlayerLeftLobby(String playerName) {

    }

    @Override
    public void notifySuccessfullyJoinedGame(int gameID) {

    }

    @Override
    public void notifyGameStarted() {

    }

    @Override
    public void notifyChosenTotemColor(String playerName, Color totemColor) {

    }

    @Override
    public void notifyGiveInitialFood(Map<String, Integer> initialFood) {

    }

    @Override
    public void showCreationChoiceScene() throws IOException {

        Platform.runLater(() -> {//serve a thread, carica la scena appena possibile, lambda e esempio di uso gui con thread
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/chooseToCreate.fxml"));

                Parent root = loader.load();

                ChooseToCreateController controller = loader.getController();

                controller.setGUI(this);

                Scene scene = new Scene(root);

                primaryStage.setScene(scene);

                primaryStage.show();
            }catch (Exception e){
                System.out.println("Error: " + e);
            }

        });
    }

    @Override
    public void showGameIdScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/ChooseGameId.fxml"));
            Parent root = loader.load();
            ChooseGameIdController Controller = loader.getController();
            Controller.setGUI(this);//important to pass the gui, this way I can access the valid controller
            Controller.loadGames(controller.getAvailableGames());

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
    }

    @Override
    public void chooseTotemScene() {

    }

    @Override
    public void playGameScene() {

    }

    @Override
    public void lobbyScene() {

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
    }

    public void handleGameID(int ID){
        gameID = ID;
    }

    public void handleNickname(String Nickname) throws IOException{
        nickname = Nickname;
        controller.setPlayerName(nickname);
        showCreationChoiceScene();
    }
    public void handleNumber(int number){
        controller.createGame(number);
        gameID = controller.getLocalModel().getGameId();

    }
}
