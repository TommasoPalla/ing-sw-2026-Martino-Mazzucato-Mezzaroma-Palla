package it.polimi.ingsw.View.GUIView;

import it.polimi.ingsw.Controller.ClientController.ClientController;
import it.polimi.ingsw.View.GUIView.Controllers.RankingSceneController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;

public class GuiTests extends Application {


    @Override
    public void start(Stage stage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/RankingScene.fxml"));
        Parent root = loader.load();

        RankingSceneController controller = loader.getController();
        controller.populateRanking(Map.of("Alice", 42, "Bob", 35, "Charlie", 28));
        controller.populateLeaderboard(List.of("Alice: 120", "Bob: 98", "Charlie: 75"));

        stage.setScene(new Scene(root));
        stage.show();
    }
}