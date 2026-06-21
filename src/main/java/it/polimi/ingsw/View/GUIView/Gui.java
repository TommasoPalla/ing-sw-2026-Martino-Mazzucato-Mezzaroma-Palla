package it.polimi.ingsw.View.GUIView;

import it.polimi.ingsw.Controller.ClientController.ClientController;
import it.polimi.ingsw.Enums.ClientState;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.View.GUIView.Controllers.*;
import it.polimi.ingsw.View.ViewInterface;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class communicates directly with the client controller, has the methods to load and show scenes and a few fields
 * to enable a faster implementation without exchanging obvious and redundant information with the server
 */
/*TODO: verificare per ogni runLater di gameScene se si può togliere il try catch (lasciarlo solo se bisogna caricare
*  risorse grafiche (in quel caso NullPointerException)*/
public class Gui implements ViewInterface {

    private static final Logger LOGGER = Logger.getLogger(Gui.class.getName());

    private ClientController controller;
    private final Stage primaryStage;
    private String nickname;
    private ChooseGameIdController ChooseId;
    private LobbySceneController lobby;
    private ArrayList<String> players;
    private GameSceneController gameScene;
    private boolean isHost;
    private int numPlayers;
    Map<String, Integer> currentRanking;


    public Gui(Stage stage){
        this.primaryStage = stage;
        this.primaryStage.setMaximized(true);
        String logoPath = "/Images/Icons/Logo.png";
        Image logo = new Image(Objects.requireNonNull(getClass().getResourceAsStream(logoPath)));
        this.primaryStage.getIcons().add(logo);
        this.primaryStage.show();
        this.players = new ArrayList<>();
        this.isHost = false;
        this.numPlayers = 0;
    }

    /**
     * associates the client controller of this client to the controller field
     * @param Controller client controller
     */
    public void bindController(ClientController Controller){
        controller = Controller;
    }

    /**
     * returns the controller bound to the gui class
     * @return controller
     */
    public ClientController getClientController(){
        return this.controller;
    }

    /**
     * modifies the private field num
     * @param num num player
     */
    public void setNumPlayers(int num){
        numPlayers=num;
    }

    /**
     * returns the number of players currently sharing a lobby or a game
     * @return num players
     */
    public int getNumPlayers(){
        return(numPlayers);
    }

    /**
     * returns the names of the players currently sharing a lobby or a game
     * @return players
     */
    public ArrayList<String> getPlayers(){
        return players;
    }

    /**
     * empties players field, used when the players leaves a lobby or a game
     */
    public void removePlayers(){
        players.clear();
    }

    /**
     * returns the boolean is host which identifies the property of a player which could be the host of the game
     * @return host
     */
    public boolean returnHost(){
        return this.isHost;
    }

    /**
     * resets the isHost field to default value false
     */
    public void handleLeave(){
        this.isHost = false;
    }

    /**
     * returns the name the user is currently using
     * @return nickname
     */
    public String getName(){
        return nickname;
    }

//    /**
//     * returns the current
//     * @return
//     */
//    public Map<String, Integer> getCurrentRanking(){
//        return currentRanking;
//    }


    /**
     * loads the first scene, after a selected delays the following scene nicknameScene is loaded
     */
    public void introScene() {

        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/IntroScene.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);
            PauseTransition delay = new PauseTransition(Duration.seconds(3));//initial splash of 3 sec

            delay.setOnFinished(event -> {

                try {
                    nicknameScene();

                } catch (Exception e) {
                    e.printStackTrace();;
                }
            });

            delay.play();

        } catch (Exception e) {
            e.printStackTrace();;
        }
    }//-> nickname

    @Override
    public void showNewAvailableGames() {

    }

    @Override
    public void showNameSet(String newName) {

    }

    @Override
    public void showNewCurrentPlayer(String playerName, ClientState clientState) {
        Platform.runLater(() -> {
            if(gameScene != null) {
                try {
                    gameScene.showNewCurrentPlayer(playerName, clientState);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to set new current player", e);
                }
            }
        });
    }

    /**
     * confirms the game was successfully created and transfers the host to the lobby
     * @param gameID gameID
     */
    @Override
    public void showGameCreated(int gameID) {
        Platform.runLater(() -> {//avoids to throw exception required by lobbyScene
            try{
                lobbyScene();
            }catch(Exception e){
                LOGGER.log(Level.SEVERE, "Failed to display lobby scene", e);
            }
        });
    }

    /**
     * notifies the players in the lobby a new player joined it
     * @param playerName playerName
     */
    @Override
    public void showPlayerJoinedLobby(String playerName) {
        Platform.runLater(() -> {//avoids to throw exception required by lobbyScene
            try{
                lobby.notifyPlayerJoined(playerName);
                //ChooseId.loadGames(controller.getAvailableGames());//private controllers keep the reference to the active ones
                players.add(playerName);
            }catch(Exception e){
                LOGGER.log(Level.WARNING, "Failed to display player joining lobby", e);
            }
        });
    }

    /**
     * notifies the players in the lobby a player left it
     * @param playerName playerName
     * @param oldColor old color
     */
    @Override
    public void showPlayerLeftLobby(String playerName, Color oldColor) {
        lobby.notifyPlayerLeft(playerName);
        ChooseId.loadGames(controller.getAvailableGames());
        players.remove(playerName);
    }

    /**
     * notifies the user the host left and they are the new host
     */
    @Override
    public void showNewHost() {
        this.isHost = true;
        lobby.newHost();
    }

    /**
     *gives the user all the information about the lobby they just joined names and totems already chosen
     * of other users sharing the same lobby
     * @param gameID id og game joined
     * @param playerNames users in the lobby
     * @param totemColors totem already taken
     */
    @Override
    public void showSuccessfullyJoinedGame(int gameID, ArrayList<String> playerNames, Map<String, Color> totemColors) {
        for(String playerName : playerNames){
            /*
            mezzo sbagliato. siccome notifyPlayerJoined era un metodo pensato per chi e' gia' in lobby
            prende il nome passato e lo stampa dicendo "ha joinato". in questo caso pero' non e' chi
            e' passato per parametro a joinare perche' era gia' in lobby. e' una cosa di cui possiamo
            non preoccuparci*/
            lobby.notifyPlayerJoined(playerName);
            lobby.joined();
        }
        players=playerNames;
    }

    /**
     * notifies the users of the lobby that the game was started
     */
    @Override
    public void showGameStarted() {
        Platform.runLater(() -> {
            boolean success = playGameScene();
            if(!success) {
                if(lobby != null) {
                    lobby.getBanner().showBanner("Critical ERROR: cannot load GameScene. App will be terminated.",
                            2.0, () -> System.exit(1), 0);
                    LOGGER.log(Level.SEVERE, "App is being terminated due to a critical error");
                }
            }
        });
    }


    @Override
    public void showStartRound(int round) {
        Platform.runLater(() -> {
            if (gameScene != null) {
                try {
                    gameScene.showNewRound(round);
                } catch(Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to display start round", e);
                }
            }
        });
    }

    /**
     * notifies the lobby a totem was chosen
     * @param playerName name of user who chose a totem
     * @param totemColor totem color chosen
     */
    @Override
    public void showChosenTotemColor(String playerName, Color totemColor) {
        lobby.notifyTotemChosen(playerName, totemColor);
    }

    @Override
    public void showInitialFood(Map<String, Integer> initialFood) {
        Platform.runLater(() -> {
            if (gameScene != null) {
                try {
                    gameScene.showInitialFood(initialFood);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to display initial food", e);
                }
            }
        });

    }

    @Override
    public void showTileChosen(String playerName, int index) {
        Platform.runLater(() -> {
            if (gameScene != null) {
                try {
                    gameScene.showTileChosen(playerName, index);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "failed to show tile chosen", e);
                }
            }
        });
    }

    @Override
    public void showFoodBonusTile(String playerName, int foodBonus) {
        Platform.runLater(() -> {
            if(gameScene != null) {
                try {
                    gameScene.showFoodBonusTile(playerName, foodBonus);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to show food bonus from Tile A", e);
                }
            }
        });
    }

    @Override
    public void showCardDrawn(String player, Card card, boolean topRow, boolean fromBuildings) {
        Platform.runLater(() -> {
            if(gameScene != null) {
                try {
                    gameScene.showCardDrawn(player, card, topRow, fromBuildings);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to display card drawn", e);
                }
            }
        });
    }

    @Override
    public void showEraChanged(int era) {
        Platform.runLater(() -> {
            if(gameScene != null) {
                try {
                    gameScene.showNewEra(era);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to display era change", e);
                }
            }
        });
    }

    @Override
    public void showTurnPassed(String playerThatPassed, String newCurrentPlayer) {
        Platform.runLater(() -> {
            if(gameScene != null) {
                try {
                    gameScene.showTurnPassed(playerThatPassed);
                } catch(Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to show player has passed turn", e);
                }
            }
        });
    }

    @Override
    public void showNewGamePhase(GamePhase newGamePhase) {
        Platform.runLater(() -> {
            if (gameScene != null) {
                gameScene.showNewGamePhase(newGamePhase);
            }
        });
    }

    @Override
    public void showFoodModified(String playerName, int deltaFood, int finalFood) {
        Platform.runLater(() -> {
            if(gameScene != null) {
                try {
                    gameScene.showFoodModified(playerName, deltaFood, finalFood);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to show food update", e);
                }
            }
        });
    }

    @Override
    public void showPrestigePointsModified(String playerName, int deltaPP, int finalPP) {
        Platform.runLater(() -> {
            if(gameScene != null) {
                try {
                    gameScene.showPrestigeModified(playerName, deltaPP, finalPP);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to show prestige update", e);
                }
            }
        });
    }

    @Override
    public void showShamanStarsModified(String playerName, int stars) {
        Platform.runLater(() -> {
            if(gameScene != null) {
                try {
                    gameScene.showShamanStarsModified(playerName, stars);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to show shaman stars update", e);
                }
            }
        });
    }

    @Override
    public void showBuildersDiscountModified(String playerName, int discount) {
        Platform.runLater(() -> {
            if(gameScene != null) {
                try {
                    gameScene.showBuildersDiscountModified(playerName, discount);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to show builders discount update", e);
                }
            }
        });
    }

    @Override
    public void showGatherersDiscountModified(String playerName, int discount) {
        Platform.runLater(() -> {
            if(gameScene != null) {
                try {
                    gameScene.showGatherersDiscountModified(playerName, discount);
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, "Failed to show gatherers discount update", e);
                }
            }
        });
    }

    @Override
    public void showTotemToTurnTile(String playerName, int index) {
        Platform.runLater(() -> {
            if(gameScene != null) {
                try {
                    gameScene.showTotemToTurnTile(playerName, index);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to show totem going back to turn tile", e);
                }
            }
        });
    }

    @Override
    public void showEvent(String playerName, EventType eventType, int foodModified, int ppModified) {
        Platform.runLater(() -> {
            if(gameScene != null) {
                try {
                    gameScene.showEventEffects(playerName, eventType, foodModified, ppModified);
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to show event effects", e);
                }
            }
        });
    }

    /**
     * notifies users in the game or lobby that a player was disconnected
     * @param disconnectedPlayerName name of disconnected player
     * @param oldColor color of totem of disconnected player
     */
    @Override
    public void showForceQuit(String disconnectedPlayerName, Color oldColor) {
        Platform.runLater(() -> {
            if(gameScene != null) {
               gameScene.showCriticalDisconnection(disconnectedPlayerName);
            } else if(lobby != null) {
                lobby.notifyPlayerLeft(disconnectedPlayerName);
            }
        });
    }

    @Override
    public void showEndGame(Map<String, Integer> finalRanking) {
        Platform.runLater(() -> {
            if(gameScene != null) {
                try {
                    currentRanking = finalRanking;
                    gameScene.showEndGame();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Failed to show game ended", e);
                }
            }
        });
    }

    @Override
    public void showLeaderboardInfo(int playerPosition) {

    }

    @Override
    public void showEndGameLeft() {

    }

    /**
     * notifies a problem with the connection
     * @param errorMessage description of problem
     */
    @Override
    public void showError(String errorMessage) {
        Platform.runLater(() -> {
            if(gameScene != null) {
                gameScene.showError(errorMessage);
            }
            if(lobby != null) {
                lobby.showError(errorMessage);
            }
        });
    }

    /**
     * loads the scene where the user is asked to join or create a game
     * @throws IOException loader exception
     */
    public void showCreationChoiceScene() throws IOException {

        Platform.runLater(() -> {//serve a thread, carica la scena appena possibile, lambda e esempio di uso gui con thread
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/chooseToCreate.fxml"));
                Parent root = loader.load();
                ChooseToCreateController Controller = loader.getController();
                Controller.setGUI(this);
                Scene scene = new Scene(root);
                primaryStage.setScene(scene);
                controller.setClientState(ClientState.CONNECTING);
            }catch (Exception e){
                System.out.println("Error: " + e);
            }

        });
    }//->handleJoin, handleCreate

    /**
     * loads the scene where the user is asked which game to join
     */
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
        }catch (Exception e){
            System.out.println("Error: " + e);
        }
    }

    /**
     * loads the scene where the user is asked their username
     */
    public void nicknameScene() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/ChooseNickName.fxml"));
            Parent root = loader.load();
            ChooseNickNameController controller = loader.getController();
            controller.setGUI(this);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

        }catch (Exception e){
            System.out.println("Error: " + e);
        }
    }//->handle Nickname

    /**
     * loads the scene where the user is asked to choose a totem
     */
    public void chooseTotemScene() {
        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/ChooseTotemScene.fxml"));
            Parent root = loader.load();
            ChooseTotemController controller = loader.getController();
            controller.setGUI(this);
            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

        }catch(Exception e){
            System.out.println("Error: " + e);

        }
    }

    public boolean playGameScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/GameScene.fxml"));
            Parent root = loader.load();

            if (root instanceof AnchorPane) {
                AnchorPane rootAnchor = (AnchorPane) root;
                rootAnchor.setPrefWidth(primaryStage.getWidth());
                rootAnchor.setPrefHeight(primaryStage.getHeight());
            }

            gameScene = loader.getController();
            gameScene.setup(this);

            Scene scene = new Scene(root);
            scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/gameSceneStyle.css")).toExternalForm());

            primaryStage.setScene(scene);
            return true;
        } catch (IOException e){
            LOGGER.log(Level.SEVERE, "Error loading GameScene", e);
            return false;
        } catch (NullPointerException e) {
            LOGGER.log(Level.WARNING, "Style.css or FXML resource path not found (NullPointerException)", e);
            return false;
        }
    }

    /**
     * loads the lobby scene where the user can leave or choose a totem
     * @throws IOException loader exception
     */
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

        }catch(IOException e){
            System.out.println("Error: " + e);
        }
    }

    /**
     * loads the scene where the user is asked how many players do they want the game they created to host
     */
    public void chooseNumberOfPlayers() {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/ChooseNumberOfPlayers.fxml"));
            Parent root = loader.load();
            ChooseNumberOfPlayersController controller = loader.getController();
            controller.setGUI(this);

            Scene scene = new Scene(root);
            primaryStage.setScene(scene);

        }catch (Exception e){
            System.out.println("Error: " + e);
        }
    }

    /**
     * loads the scene where the finished game's leaderboard and server's own leaderboard is displayed
     */
    public void rankingScene(){

        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML_files/RankingScene.fxml"));
                Parent root = loader.load();
                RankingSceneController controller = loader.getController();
                controller.setGUI(this);
                controller.populateRanking(this.currentRanking);
                controller.populateLeaderboard(getClientController().getLocalModel().getDbLeaderboard());

                Scene scene = new Scene(root);
                primaryStage.setScene(scene);

            }catch (Exception e){
                System.out.println("Error: " + e);
            }

        });
    }

    /**
     * calls the scene where user is asked which game to join
     */
    public void handleJoin(){
        showGameIdScene();
    }

    /**
     * calls scene where user is asked how many players do they want their game to host and initializes local fields
     */
    public void handleCreate(){
        chooseNumberOfPlayers();
        players.add(nickname);
        this.isHost = true;
    }

    /**
     * calls the scene where user is asked to choose a username, after they decided to update it
     */
    public void handleNickChange(){
        nicknameScene();
    }

    /**
     * calls the lobby scene after a game was successfully chosen and joined, notifies the clientController
     * @param ID id of chosen game
     * @throws IOException loads exception
     */
    public void handleGameID(int ID) throws IOException{
        lobbyScene();
        controller.joinGame(ID);
    }

    /**
     * calls the scene where user is asked to join or create a game, update local fields, notifies the choice to
     * client controller
     * @param Nickname chosen nickname shared with client controller
     * @throws IOException loader exception
     */
    public void handleNickname(String Nickname) throws IOException{
        nickname = Nickname;
        controller.setPlayerName(nickname);
        showCreationChoiceScene();
    }

    /**
     * the number of desired players to take part to the created game was chosen and is shared with client controller
     * @param number number of players
     * @throws IOException loader exception
     */
    public void handleNumber(int number) throws IOException{
        controller.setClientState(ClientState.SETUP);
        controller.createGame(number);
    }
}