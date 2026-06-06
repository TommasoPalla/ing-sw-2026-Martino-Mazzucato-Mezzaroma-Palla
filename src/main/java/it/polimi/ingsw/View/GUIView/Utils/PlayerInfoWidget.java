package it.polimi.ingsw.View.GUIView.Utils;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.InventorType;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.View.GUIView.GUISettings;
import it.polimi.ingsw.View.GUIView.Gui;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;

import java.util.Map;
import java.util.Objects;


/**PlayerInfoWidget class represents a dynamic widget that shows brief information
 * about other players' tribes (different from the client). It represents various
 * icons with labels in grid-like structure. From 1 to 4 instances of this class
 * will appear on InfoBoard component, depending on number of players.
 *
 */
public class PlayerInfoWidget extends GridPane{

    private final Label nameLabel;
    private final Label foodReserve;
    private final Label prestigePoints;
    private final Label charactersNum;
    private final Label sustenanceDiscount;
    private final Label shamanStars;
    private final Label buildingsDiscount;
    private final Label inventionsNum;
    private final Label shamansNum;
    private final Label buildersNum;
    private final Label inventorsNum;
    private final Label gatherersNum;
    private final Label huntersNum;
    private final Label artistsNum;

    private final Gui gui;

    /**Initializes the player's widget shown on Info Board (on the right of the screen).
     * Sets player's name and totem icon, initializes to 0 all labels referring to
     * player's tribe and loads all the related icons.
     *
     * @param name Name of the player the widget refers to
     * @param totemColor Color of the totem chosen by the player, used to show the correct icon
     * @param gui reference to main GUI class to get tribe's information, in order to show them
     */
    public PlayerInfoWidget(String name, Color totemColor, Gui gui){
        super();
        this.setMinWidth(GUISettings.PlayerWidget.WIDTH - 20);
        this.setPrefWidth(GUISettings.PlayerWidget.WIDTH);
        this.setMaxWidth(GUISettings.PlayerWidget.WIDTH + 40);
        this.setMaxHeight(GUISettings.PlayerWidget.HEIGHT);

        this.setAlignment(Pos.TOP_CENTER);
        this.setPadding(new Insets(10));
        this.setHgap(10);
        this.setVgap(12);
        this.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());
        this.getStyleClass().add("player-widget");
        this.gui = gui;

        ColumnConstraints colIcon = new ColumnConstraints();
        colIcon.setMinWidth(GUISettings.Icons.ICON_WIDTH);
        colIcon.setHgrow(Priority.NEVER);
        ColumnConstraints colLabel = new ColumnConstraints();
        colLabel.setMinWidth(15);
        colLabel.setHgrow(Priority.ALWAYS);
        this.getColumnConstraints().addAll(colIcon, colLabel, colIcon, colLabel, colIcon, colLabel);
        //Graphical layout
        //Row 0: totem, playerName and food
        ImageView totemImage = new ImageView();
        totemImage.setFitWidth(GUISettings.Icons.BIG_ICON_WIDTH);
        totemImage.setFitHeight(GUISettings.Icons.BIG_ICON_HEIGHT);
        totemImage.setPreserveRatio(true);
        String totemImagePath = "/Images/Totems/" + totemColor.toString() + ".png";
        try {
            Image totemImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(totemImagePath)));
            totemImage.setImage(totemImg);
            //TODO: non sembra funzionare lo stile
            totemImage.setStyle("-fx-border-width: 1; -fx-border-color: #FF6B35; -fx-background-radius: 20");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.add(totemImage, 0, 0);

        //name
        nameLabel = new Label(name);
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 18");
        this.add(nameLabel, 1, 0, 2, 1);

        //food
        ImageView foodImage = new ImageView();
        foodImage.setFitWidth(GUISettings.Icons.BIG_ICON_WIDTH);
        foodImage.setFitHeight(GUISettings.Icons.BIG_ICON_HEIGHT);
        foodImage.setPreserveRatio(true);
        String foodImagePath = "/Images/Icons/Food.png";
        try {
            Image foodImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(foodImagePath)));
            foodImage.setImage(foodImg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.add(foodImage, 3, 0, 2, 1);
        foodReserve = new Label("0");
        this.add(foodReserve, 5, 0);

        //row 1 prestige, characters numbers and sustenance discount
        //prestige
        ImageView prestigeImage = new ImageView();
        prestigeImage.setFitWidth(GUISettings.Icons.ICON_WIDTH);
        prestigeImage.setFitHeight(GUISettings.Icons.ICON_HEIGHT);
        prestigeImage.setPreserveRatio(true);
        String prestigeImgPath = "/Images/Icons/Prestige.png";
        try {
            Image prestigeImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(prestigeImgPath)));
            prestigeImage.setImage(prestigeImg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.add(prestigeImage, 0, 1);
        prestigePoints = new Label("0");
        this.add(prestigePoints, 1, 1);

        //characters
        ImageView charactersImage = new ImageView();
        charactersImage.setFitWidth(GUISettings.Icons.ICON_WIDTH);
        charactersImage.setFitHeight(GUISettings.Icons.ICON_HEIGHT);
        charactersImage.setPreserveRatio(true);
        String charactersImagePath = "/Images/Icons/Characters.png";
        try {
            Image charactersImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(charactersImagePath)));
            charactersImage.setImage(charactersImg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.add(charactersImage, 2, 1);
        charactersNum = new Label("0");
        this.add(charactersNum, 3, 1);

        //sustenanceDiscount
        ImageView sustenanceDiscountImage = new ImageView();
        sustenanceDiscountImage.setFitWidth(GUISettings.Icons.ICON_WIDTH);
        sustenanceDiscountImage.setFitHeight(GUISettings.Icons.ICON_HEIGHT);
        sustenanceDiscountImage.setPreserveRatio(true);
        String discountImagePath = "/Images/Icons/Sustenance.png";
        try {
            Image sustenanceImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(discountImagePath)));
            sustenanceDiscountImage.setImage(sustenanceImg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.add(sustenanceDiscountImage, 4, 1);
        sustenanceDiscount = new Label("0");
        this.add(sustenanceDiscount, 5, 1);

        //row 2: shamanic stars, buildingsDiscount and InventionsNumber
        //shamanStars
        ImageView starsImage = new ImageView();
        starsImage.setFitWidth(GUISettings.Icons.ICON_WIDTH);
        starsImage.setFitHeight(GUISettings.Icons.ICON_HEIGHT);
        starsImage.setPreserveRatio(true);
        String starsImagePath = "/Images/Icons/ShamanStar.png";
        try {
            Image starsImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(starsImagePath)));
            starsImage.setImage(starsImg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.add(starsImage, 0, 2);
        shamanStars = new Label("0");
        this.add(shamanStars, 1, 2);

        //buildingDiscount
        ImageView buildingsDiscountImage = new ImageView();
        buildingsDiscountImage.setFitWidth(GUISettings.Icons.ICON_WIDTH);
        buildingsDiscountImage.setFitHeight(GUISettings.Icons.ICON_HEIGHT);
        buildingsDiscountImage.setPreserveRatio(true);
        String buildingsDiscountImagePath = "/Images/Icons/BuildingsDiscount.png";
        try {
            Image discountImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(buildingsDiscountImagePath)));
            buildingsDiscountImage.setImage(discountImg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.add(buildingsDiscountImage, 2, 2);
        buildingsDiscount = new Label("0");
        this.add(buildingsDiscount, 3, 2);

        //inventions Num
        ImageView inventionsImage = new ImageView();
        inventionsImage.setFitWidth(GUISettings.Icons.ICON_WIDTH);
        inventionsImage.setFitHeight(GUISettings.Icons.ICON_HEIGHT);
        inventionsImage.setPreserveRatio(true);
        String inventionsImgPath = "/Images/Icons/Inventions.png";
        try {
            Image inventionsImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(inventionsImgPath)));
            inventionsImage.setImage(inventionsImg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.add(inventionsImage, 4, 2);
        inventionsNum = new Label("0");
        this.add(inventionsNum, 5, 2);

        //row 3: shamans, builders, inventors
        //shamans
        ImageView shamansImage = new ImageView();
        shamansImage.setFitWidth(GUISettings.Icons.ICON_WIDTH);
        shamansImage.setFitHeight(GUISettings.Icons.ICON_HEIGHT);
        shamansImage.setPreserveRatio(true);
        String shamanImgPath = "/Images/Icons/Shaman.png";
        try {
            Image shamanImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(shamanImgPath)));
            shamansImage.setImage(shamanImg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.add(shamansImage, 0, 3);
        shamansNum = new Label("0");
        this.add(shamansNum, 1, 3);

        //builders
        ImageView buildersImage = new ImageView();
        buildersImage.setFitWidth(GUISettings.Icons.ICON_WIDTH);
        buildersImage.setFitHeight(GUISettings.Icons.ICON_HEIGHT);
        buildersImage.setPreserveRatio(true);
        String buildersImgPath = "/Images/Icons/Builder.png";
        try {
            Image builderImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(buildersImgPath)));
            buildersImage.setImage(builderImg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.add(buildersImage, 2, 3);
        buildersNum = new Label("0");
        this.add(buildersNum, 3, 3);

        //inventors
        ImageView inventorsImage = new ImageView();
        inventorsImage.setFitWidth(GUISettings.Icons.ICON_WIDTH);
        inventorsImage.setFitHeight(GUISettings.Icons.ICON_HEIGHT);
        inventorsImage.setPreserveRatio(true);
        String inventorsImgPath = "/Images/Icons/Inventor.png";
        try {
            Image inventorImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(inventorsImgPath)));
            inventorsImage.setImage(inventorImg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.add(inventorsImage, 4, 3);
        inventorsNum = new Label("0");
        this.add(inventorsNum, 5, 3);

        //row 4: gatherers, hunters, artists
        //gatherers
        ImageView gatherersImage = new ImageView();
        gatherersImage.setFitWidth(GUISettings.Icons.ICON_WIDTH);
        gatherersImage.setFitHeight(GUISettings.Icons.ICON_HEIGHT);
        gatherersImage.setPreserveRatio(true);
        String gatherersImgPath = "/Images/Icons/Gatherer.png";
        try {
            Image gathererImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(gatherersImgPath)));
            gatherersImage.setImage(gathererImg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.add(gatherersImage, 0, 4);
        gatherersNum = new Label("0");
        this.add(gatherersNum, 1, 4);

        //hunters
        ImageView huntersImage = new ImageView();
        huntersImage.setFitWidth(GUISettings.Icons.ICON_WIDTH);
        huntersImage.setFitHeight(GUISettings.Icons.ICON_HEIGHT);
        huntersImage.setPreserveRatio(true);
        String huntersImgPath = "/Images/Icons/Hunter.png";
        try {
            Image huntersImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(huntersImgPath)));
            huntersImage.setImage(huntersImg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.add(huntersImage, 2, 4);
        huntersNum = new Label("0");
        this.add(huntersNum, 3, 4);

        //artists
        ImageView artistsImage = new ImageView();
        artistsImage.setFitWidth(GUISettings.Icons.ICON_WIDTH);
        artistsImage.setFitHeight(GUISettings.Icons.ICON_HEIGHT);
        artistsImage.setPreserveRatio(true);
        String artistsImgPath = "/Images/Icons/Artist.png";
        try {
            Image artistsImg = new Image(Objects.requireNonNull(getClass().getResourceAsStream(artistsImgPath)));
            artistsImage.setImage(artistsImg);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.add(artistsImage, 4, 4);
        artistsNum = new Label("0");
        this.add(artistsNum, 5, 4);
    }

    /**Called by the gameSceneController to update number of characters in this tribe
     * and other labels specific to the CharacterRole of the card
     *
     * @param card card drawn by this player, caller of this method
     *            checks that it is an instance of CharacterCard
     */
    public void updateCardDrawn(Card card){
        int newCharNum = Integer.parseInt(charactersNum.getText()) + 1;
        AnimationsUtils.animateLabelUpdate(charactersNum, String.valueOf(newCharNum));

        CharacterCard character = (CharacterCard) card;
        int newNum;
        String playerName = nameLabel.getText();

        switch (character.getRole()) {
            case ARTIST -> {
                newNum = gui.getClientController().getLocalModel().getPlayerTribe(playerName).getArtistsNumber();
                AnimationsUtils.animateLabelUpdate(artistsNum, String.valueOf(newNum));
            }
            case BUILDER -> {
                newNum = gui.getClientController().getLocalModel().getPlayerTribe(playerName).getBuildersNumber();
                AnimationsUtils.animateLabelUpdate(buildersNum, String.valueOf(newNum));
                int newDiscount = gui.getClientController().getLocalModel().getPlayerTribe(playerName).getBuildersDiscount();
                AnimationsUtils.animateLabelUpdate(buildingsDiscount, String.valueOf(newDiscount));
            }
            case GATHERER -> {
                newNum = gui.getClientController().getLocalModel().getPlayerTribe(playerName).getGatherersNumber();
                AnimationsUtils.animateLabelUpdate(gatherersNum, String.valueOf(newNum));
                int newDiscount = gui.getClientController().getLocalModel().getPlayerTribe(playerName).getGatherersDiscount();
                AnimationsUtils.animateLabelUpdate(sustenanceDiscount, String.valueOf(newDiscount));
            }
            case HUNTER -> {
                newNum = gui.getClientController().getLocalModel().getPlayerTribe(playerName).getHuntersNumber();
                AnimationsUtils.animateLabelUpdate(huntersNum, String.valueOf(newNum));
            }
            case INVENTOR -> {
                Map<InventorType, Integer> inventors = gui.getClientController().getLocalModel().
                        getPlayerTribe(playerName).getInventorsPerType();
                newNum = 0;
                int typesNum = 0;   //represents number of different inventions this player has
                for(InventorType inventorType: inventors.keySet()){
                    newNum += inventors.get(inventorType);
                    if(inventors.get(inventorType) > 0){
                        typesNum++;
                    }
                }
                AnimationsUtils.animateLabelUpdate(inventorsNum, String.valueOf(newNum));
                AnimationsUtils.animateLabelUpdate(inventionsNum, String.valueOf(typesNum));
            }
            case SHAMAN -> {
                newNum = gui.getClientController().getLocalModel().getPlayerTribe(playerName).getShamansNumber();
                AnimationsUtils.animateLabelUpdate(shamansNum, String.valueOf(newNum));
                int newStars = gui.getClientController().getLocalModel().getPlayerTribe(playerName).getShamansStars();
                AnimationsUtils.animateLabelUpdate(shamanStars, String.valueOf(newStars));
            }
        }
    }

    /**called by the gameSceneController to update foodQuantity label
     * @param currFood new value of player's food
     */
    public void updateFoodReserve(int currFood){
        AnimationsUtils.animateLabelUpdate(foodReserve, String.valueOf(currFood));
    }

    public int getFoodReserve() {
        return Integer.parseInt(foodReserve.getText());
    }

    public void updatePrestigePoints(int pp) {
        AnimationsUtils.animateLabelUpdate(prestigePoints, String.valueOf(pp));
    }
    public int getPrestigePoints() {
        return Integer.parseInt(prestigePoints.getText());
    }
}