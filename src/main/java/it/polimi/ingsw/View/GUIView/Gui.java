package it.polimi.ingsw.View.GUIView;

import it.polimi.ingsw.Controller.ClientController.ClientController;
import it.polimi.ingsw.View.ViewInterface;
import javafx.stage.Stage;

public class Gui implements ViewInterface {

    private ClientController controller;

    private final Stage primaryStage;


    public Gui(Stage stage){

        this.primaryStage = stage;
    }


    @Override
    public void startGame() {

    }

    @Override
    public void connectionTypeScene() {

    }

    @Override
    public void gameIdScene() {

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
}
