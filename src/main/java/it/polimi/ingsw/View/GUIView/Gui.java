package it.polimi.ingsw.View.GUIView;

import it.polimi.ingsw.Controller.ClientController.ClientController;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Networking.RMI.RMIServerAdapter;
import it.polimi.ingsw.Networking.Shared.ServerConnection;
import it.polimi.ingsw.Networking.Socket.SocketServerAdapter;
import it.polimi.ingsw.View.GUIView.GuiControllers.ChooseGameIdController;
import it.polimi.ingsw.View.GUIView.GuiControllers.ChooseNickNameScene;
import it.polimi.ingsw.View.GUIView.GuiControllers.chooseToCreateController;
import it.polimi.ingsw.View.ViewInterface;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

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
                    showCreationChoiceScene();//CREATION SCENE

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
    public void notifyChosenTotemColor(String playerName, Color totemColor) {

    }

    @Override
    public void showCreationChoiceScene() throws IOException {

        Platform.runLater(() -> {//serve a thread, carica la scena appena possibile, lambda e esempio di uso gui con thread
            try {

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/chooseToCreate.fxml"));

                Parent root = loader.load();

                chooseToCreateController controller = loader.getController();

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

            ChooseGameIdController controller = loader.getController();

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

            ChooseNickNameScene controller = loader.getController();

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
    public void showNumberOfPlayers() {

    }

    @Override
    public void playGameScene() {

    }

    @Override
    public void chooseNumberOfPlayers() {

    }

    public void handleJoin(){
        showGameIdScene();
    }
    public void handleCreate(){
        chooseNumberOfPlayers();
    }

    public void handleGameID(int ID){
        gameID = ID;
        nicknameScene();
    }

    public void handleNickname(String Nickname){
        nickname = Nickname;
        controller.joinGame(nickname, gameID);
    }
    public void handleNumber(int number){
        controller.createGame(number);
        gameID = controller.getLocalModel().getGameId();
    }
}
