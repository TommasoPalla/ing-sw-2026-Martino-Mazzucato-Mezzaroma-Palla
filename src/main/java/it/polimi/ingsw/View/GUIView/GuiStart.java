package it.polimi.ingsw.View.GUIView;

import javafx.application.Application;
import javafx.stage.Stage;


public class GuiStart extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        start(stage);
        Gui gui = new Gui(stage);
        gui.showConnectionTypeScene();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
