package it.polimi.ingsw.View.GUIView;

import java.io.IOException;

public interface ViewInterfaceGui {
    void introScene () throws IOException;
    void showCreationChoiceScene() throws IOException;
    void showGameIdScene();
    void nicknameScene();
    void chooseTotemScene();
    void chooseNumberOfPlayers();
    boolean playGameScene();
    void lobbyScene() throws IOException;
    void rankingScene();
}
