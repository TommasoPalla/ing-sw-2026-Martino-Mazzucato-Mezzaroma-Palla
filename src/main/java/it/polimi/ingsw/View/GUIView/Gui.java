package it.polimi.ingsw.View.GUIView;

import it.polimi.ingsw.Controller.ClientController.ClientController;
import it.polimi.ingsw.View.GUIView.GuiControllers.connectionTypeSceneController;
import it.polimi.ingsw.View.ViewInterface;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Gui implements ViewInterfaceGui {

    private ClientController controller;

    private final Stage primaryStage;


    public Gui(Stage stage){

        this.primaryStage = stage;
    }


    @Override
    public void startGame() {

    }

    @Override
    public void showConnectionTypeScene() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/ConnectionTypeScene.fxml"));

        Parent root = loader.load();

        connectionTypeSceneController controller =
                loader.getController();

        controller.setGUI(this);

        Scene scene = new Scene(root);

        primaryStage.setScene(scene);

        primaryStage.show();

    }

    @Override
    public void showGameIdScene() {

    }

    @Override
    public void nicknameScene() {

    }

    @Override
    public void chooseTotemScene() {

    }

    @Override
    public void playGameScene() {

    }

    public void handleSocket(){
        //setta clientcontroller con socket, comunica a server
    }
    public void handleRMI(){
        //setta clientcontroller con socket, comunica a server
    }
}
