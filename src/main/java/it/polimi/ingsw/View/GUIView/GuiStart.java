package it.polimi.ingsw.View.GUIView;

import it.polimi.ingsw.Controller.ClientController.ClientController;
import javafx.application.Application;
import javafx.stage.Stage;


public class GuiStart extends Application {

    public static ClientController controller;

    public static void bindController(ClientController Controller){
        controller = Controller;
    }

    @Override
    public void start(Stage stage) throws Exception {
        Gui gui = new Gui(stage);
        gui.bindController(controller);

        controller.bindView(gui);

        gui.introScene();
    }

    public static void main(String[] args) {
        launch(args);
    }
}