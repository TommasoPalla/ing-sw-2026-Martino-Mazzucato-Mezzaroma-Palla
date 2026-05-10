package it.polimi.ingsw.View.GUIView;

import java.io.IOException;

public interface ViewInterfaceGui {
    void startGame();
    void showConnectionTypeScene() throws IOException;
    void showGameIdScene();
    void nicknameScene();
    void chooseTotemScene();
    void playGameScene();
}
