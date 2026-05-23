package it.polimi.ingsw.View.TUIView;

import it.polimi.ingsw.Controller.ClientController.ClientController;
import it.polimi.ingsw.CustomException.IllegalClientStateActionException;
import it.polimi.ingsw.CustomException.OccupiedTileException;
import it.polimi.ingsw.CustomException.UIException.*;

import it.polimi.ingsw.Enums.*;
import it.polimi.ingsw.Controller.ClientController.LightTribe;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.GameBoard.OfferTile;
import it.polimi.ingsw.View.GamePlayers;
import it.polimi.ingsw.View.ViewInterface;

import java.util.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TUIView implements ViewInterface {
    final private ClientController clientController;
    final private CommandParser commandParser;
    private String player;
    private TUIState tuiState;

    public TUIView(ClientController clientController) {
        this.clientController = clientController;
        this.commandParser = new CommandParser(clientController);
        /*
         * This attribute defines which state of the TUI the player is currently visualizing.
         */
        this.tuiState = TUIState.SETUP;
        // Starts the thread of this TUI, using 'run()' as Thread.run() method
        Thread commandListenThread = new Thread(this::runView);
        commandListenThread.start();
    }


    @Override
    public void runView() {
        System.out.println("Welcome to MESOS!");
        System.out.println("Choose your nickname:");
        boolean nameVerified = false;
        do {
            Scanner scanner = new Scanner(System.in);
            String playerName = scanner.nextLine();
            try {
                if (playerName.isEmpty()) {
                    System.out.println("You have to enter a name!");
                }
                else {
                    nameVerified = true;
                    clientController.setPlayerName(playerName);
                }
            } catch(IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        } while (!nameVerified);
        changeClientState(ClientState.SETUP);
        // ciclo di ascolto comandi
        Scanner commandScanner = new Scanner(System.in);
        while(!Thread.currentThread().isInterrupted()) {
            String command = commandScanner.nextLine();
            parseCommand(command);
        }
    }

    /**
     * TUI view of the offerTrack
     */
//    private void offerTrackTUIView(String args) {
//        if (!args.trim().isEmpty()) {
//            throw new IllegalArgumentException("This method doesn't require arguments.");
//        }
//        tuiState = TUIState.SHOW_OFFER_TRACK;
//        //printOfferTrack();
//    }

    //  WORK IN PROGRESS
    private String[] cardTUIView(Card card) {
        if (card instanceof CharacterCard characterCard) {
            CharacterRole role = characterCard.getRole();
        }
        return null;
    }

    // PARSECOMMAND E COMMANDPARSERSELECTOR NON SONO FINALI E TANTO MENO CORRETTI!!!!
    /**
     * Takes the input command and if it's valid, calls the command router.
     * @param command The string entered by the player.
     */
    private void parseCommand(String command) {
        String regex = "^([a-zA-Z_]+)\\((.*)\\)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(command);
        if(matcher.matches()){
            CommandType commandType = null;     //!!!STAVOLTA VA FATTO PER FORZA!!!
            try {
                commandType = CommandType.valueOf(matcher.group(1).toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid command, please try again...");
                return;
            }
            try {
                commandParserSelector(commandType, matcher);
            }
            //sequenza di catch da gestire
            //per ora è gestito il caso di drawCard,
            //stampa "cant draw this card" + "insufficient food / cant draw event"
            catch(InvalidSelectionException | IllegalArgumentException | NotEnoughPlayersException |
                  NotTheHostException | IllegalClientStateActionException | OccupiedTileException e){
                String causeMsg = (e.getCause() != null) ? e.getCause().getMessage() : "";
                System.out.println(e.getMessage() + causeMsg);
            }
        }
        else{
            System.out.println("ERROR: Invalid command, please try again or enter \"help()\" to know the available commands.");
        }
    }

    /**
     * Selects the correct method based on the command type and sends it to the command parser.
     * @param commandType the type of command chose by the player
     * @param matcher the matcher containing the information about the input string, like the
     *                arguments of the command.
     */
    private void commandParserSelector(CommandType commandType, Matcher matcher ) throws InvalidSelectionException, IllegalArgumentException {
        String argsString = matcher.group(2);

        switch (commandType) {
            case CREATE_GAME            -> commandParser.parseCreateGame(argsString);
            case JOIN_GAME              -> joinAvailableGames();
            case LEAVE_GAME             -> clientController.leaveGame();
            case START_GAME             -> clientController.startGame(player);
            case MODIFY_NAME            -> commandParser.parseModifyName(argsString);
            case CHOOSE_TOTEM_COLOR     -> commandParser.parseChooseTotemColor(argsString);
            case SHOW_OFFER_TRACK       -> printOfferTrack();
            case SHOW_TOP_ROW           -> printTopRow();
            case SHOW_BOTTOM_ROW        -> printBottomRow();
            case SHOW_MY_TRIBE          -> printTribe(player);
            case SHOW_OTHER_TRIBE       -> printTribe(commandParser.parseOtherTribe(argsString));
            case DRAW_CARD              -> commandParser.parseDrawCard(argsString);
            case PLACE_TOTEM            -> commandParser.parseChooseOfferTile(argsString);
            case HELP                   -> printAvailableActions(clientController.getClientState(), true);
            default                     -> throw new IllegalArgumentException("ERROR: Invalid command, please try again or enter \"help()\" to know the available commands.");
        }
    }

    /**
     * Prints to terminal the tribe of the player
     */
    private void printTribe(String playerName) {
        LightTribe localTribe = clientController.getLocalModel().getPlayerTribe(playerName);
        if(localTribe != null) {
            if(playerName.equals(this.player))
                System.out.println("\n === YOUR TRIBE ===");
            else
                System.out.println("\n=== " + playerName.toUpperCase() + "'S TRIBE ===");
            System.out.printf("%s: %d  %s: %d  %s: %d  %s: %d\n",
                    TuiIcons.FOOD, localTribe.getFoodReserve(),
                    TuiIcons.PRESTIGE_POINTS, localTribe.getPrestigePoints(),
                    TuiIcons.POPULATION, localTribe.getPopulationSize(),
                    TuiIcons.SHAMANS_STARS, localTribe.getShamansStars());

            System.out.printf("DISCOUNTS: %s: %d  %s: %d\n",
                    TuiIcons.BUILDERS_DISCOUNT, localTribe.getBuildersDiscount(),
                    TuiIcons.GATHERERS_DISCOUNT, localTribe.getGatherersDiscount());

            System.out.println("\nPOPULATION:");
            List<CharacterCard> allCharacters = localTribe.getPopulation().values().stream()
                    .flatMap(List::stream).collect(Collectors.toList());
            printHorizontal(allCharacters.stream().map(this::renderCardBox).collect(Collectors.toList()));

            System.out.println("\nBUILDING:");
            printHorizontal(localTribe.getBuildings().stream().map(this::renderCardBox).collect(Collectors.toList()));
            System.out.println();
        } else {
            System.out.println(playerName + "'s tribe not found.");
        }
    }

    private int getVisualLen(String input){
        //Removes ANSI encoding from input string
        String noAnsiString = input.replaceAll("\u001B\\[[;\\d]*m", "");
        int visualLen = 0;
        for(int i = 0; i < noAnsiString.length(); i++){
            int codePoint = noAnsiString.codePointAt(i);

            if(Character.isSupplementaryCodePoint(codePoint)){
                visualLen += 2;
                i++;
            }
            else
                visualLen += 1;
        }
        return visualLen;
    }

    private List<String> renderCardBox(Card card) {
        List<String> lines = new ArrayList<>();
        int width = 22;
        String border = "+" + "-".repeat(width - 2) + "+";

        lines.add(border);

        Map<String, String> stats = card.getDisplayStats();
        for (Map.Entry<String, String> entry : stats.entrySet()) {
            String statLine;
            if (entry.getValue().isEmpty())
                statLine = entry.getKey();
            else
                statLine = entry.getKey() + " " + entry.getValue();

            int visualLen = getVisualLen(statLine);
            int padding = (width - 4) - visualLen;

            lines.add("| " + statLine + " ".repeat(Math.max(0, padding)) + " |");
        }

        while (lines.size() < 7) {
            lines.add(String.format("| %-" + (width - 4) + "s |", ""));
        }

        String cardID = card.getCardID();
        if (cardID.length() > width - 4) cardID = cardID.substring(0, width - 7) + "...";
        lines.add(String.format("| %-" + (width - 4) + "s |", cardID));

        lines.add(border);
        return lines;
    }

    private void printHorizontal(List<List<String>> allBoxes) {
        if (allBoxes.isEmpty()) {
            System.out.println(" (NONE) ");
            return;
        }
        int maxLines = allBoxes.stream().mapToInt(List::size).max().orElse(0);
        for (int i = 0; i < maxLines; i++) {
            StringBuilder row = new StringBuilder();
            for (List<String> box : allBoxes) {
                if (i < box.size()) row.append(box.get(i)).append("  ");
                else row.append(" ".repeat(20)).append("  ");
            }
            System.out.println(row);
        }
    }

    /**
     * Prints to terminal the top row of the offerTrack
     */
    private void printTopRow() {
        if(clientController.getLocalModel().getTopRow().isEmpty()) {
            System.out.println("TOP ROW IS EMPTY");
            return;
        }
        printHorizontal(clientController.getLocalModel().getTopRow().stream().map(this::renderCardBox).collect(Collectors.toList()));
    }

    /**
     * Prints to terminal the bottom row of the offerTrack
     */
    private void printBottomRow() {
        if(clientController.getLocalModel().getBottomRow().isEmpty()) {
            System.out.println("BOTTOM ROW IS EMPTY");
            return;
        }
        printHorizontal(clientController.getLocalModel().getBottomRow().stream().map(this::renderCardBox).collect(Collectors.toList()));
    }

    private void printOfferTrack() {
        // COSTRUZIONE TURN TILE ---------------------------------------------------------------------------------------
        // -------------------------------------------------------------------------------------------------------------
        List<String> turnTileLines = new ArrayList<>();
        String turnBorder = "+-----------------------+";
        turnTileLines.add(turnBorder);
        int[] tileModifier = clientController.getLocalModel().getTileModifier();
        for (int i = 0; i < tileModifier.length; i++) {
            String bonus;
            if (tileModifier[i] != 0 && i < tileModifier.length - 1)
                bonus = String.valueOf(tileModifier[i]) + TuiIcons.FOOD;
            else if (i == tileModifier.length - 1)
                bonus = "-1" + TuiIcons.FOOD_MALUS + "/-2" + TuiIcons.PRESTIGE_BONUS;
            else
                bonus = "";
            String paddedBonus = String.format("%-10s", bonus);
            String playerOnSlot = clientController.getLocalModel().getTurnTileStatus().getOrDefault(i, "");
            String finalPlayerOnSlot;
            if (!playerOnSlot.isEmpty()) {
                Color playerColor = clientController.getLocalModel().getTotemColors().get(playerOnSlot);
                String displayName = (playerOnSlot.length() > 10) ? playerOnSlot.substring(0, 7) + "..." : playerOnSlot;
                String paddedName = String.format("%-10s", displayName);
                finalPlayerOnSlot = playerColor.colorize(paddedName);
            } else
                finalPlayerOnSlot = String.format("%-10s", "---");
            turnTileLines.add(String.format("| %s %s |", paddedBonus, finalPlayerOnSlot));
        }
        turnTileLines.add(turnBorder);

        // COSTRUZIONE OFFER TRACK ------------------------------------------------------------------------------------
        // ------------------------------------------------------------------------------------------------------------
        List<String> offerTrackLines = new ArrayList<>();
        StringBuilder topBorder = new StringBuilder();
        StringBuilder actionRow = new StringBuilder();
        StringBuilder playerRow = new StringBuilder();
        StringBuilder bottomBorder = new StringBuilder();

        tuiState = TUIState.SHOW_OFFER_TRACK;
        System.out.print("This is the current offer track:");
        System.out.println();
        for (OfferTile tile : clientController.getLocalModel().getOfferTiles()) {
            topBorder.append("+-----------------+ ");

            if (tile.getFoodBonus() != 0) actionRow.append(String.format("| %-15s | ", tile.getFoodBonus() + TuiIcons.FOOD_BONUS));
            else if (tile.getCardsFromAbove() != 0 && tile.getCardsFromBelow() != 0) {
                actionRow.append(String.format("| %-15s | ", " " + TuiIcons.UP_ARROW + " " + tile.getCardsFromAbove() + " " + TuiIcons.DOWN_ARROW + " " + tile.getCardsFromBelow()));
            } else if (tile.getCardsFromAbove() != 0) {
                actionRow.append(String.format("| %-15s | ", " " + TuiIcons.UP_ARROW + " " + tile.getCardsFromAbove()));
            } else actionRow.append(String.format("| %-15s | ", " " + TuiIcons.DOWN_ARROW + " " + tile.getCardsFromBelow()));

            String playerOccupant = tile.getCurrentOccupant();
            if (playerOccupant != null) {
                Color playerColor = clientController.getLocalModel().getTotemColors().get(playerOccupant);
                if (playerOccupant.length() > 15) {
                    playerOccupant = playerOccupant.substring(0, 12) + "...";
                }
                String paddedOccupant = String.format("%-15s", playerOccupant);
                String coloredOccupant = playerColor.colorize(paddedOccupant);
                playerRow.append(String.format("| %s | ", coloredOccupant));
            }
            else playerRow.append(String.format("| %-15s | ", "---"));

            bottomBorder.append("+-----------------+ ");
        }

        offerTrackLines.add(topBorder.toString());
        offerTrackLines.add(actionRow.toString());
        offerTrackLines.add(playerRow.toString());
        offerTrackLines.add(bottomBorder.toString());

        int maxLines = Math.max(turnTileLines.size(), offerTrackLines.size());

        // Spazio vuoto compensativo per quando finiscono le righe della Turn Order tile.
        // La larghezza è esattamente 25 caratteri (la stessa di turnBorder).
        String emptyTurnSpace = String.format("%-25s", "");

        for (int i = 0; i < maxLines; i++) {
            String leftPart = (i < turnTileLines.size()) ? turnTileLines.get(i) : emptyTurnSpace;
            String rightPart = (i < offerTrackLines.size()) ? offerTrackLines.get(i) : "";

            System.out.println(leftPart + "   " + rightPart);
        }
//        System.out.println();
//        System.out.println(topBorder);
//        System.out.println(actionRow);
//        System.out.println(playerRow);
//        System.out.println(bottomBorder);
        System.out.println();
    }

    public void changeClientState(ClientState clientState) {
        System.out.println();
        // non sono sicuro che il clientState vada cambiato nella TUI
        clientController.setClientState(clientState);
        switch (clientState) {
            case SETUP -> {
                System.out.println("You are in the setup state.");
                tuiState = TUIState.SETUP;
                printAvailableActions(ClientState.SETUP, false);
            }
            case IN_LOBBY ->  {
                System.out.println("Welcome to the lobby!");
                tuiState = TUIState.IN_LOBBY;
                printAvailableActions(ClientState.IN_LOBBY, false);
            }
            case PLACE_TOTEM ->  {
                System.out.println("It's your turn to place the totem!");
                printAvailableActions(ClientState.PLACE_TOTEM, false);
            }
            case DRAW_CARD ->   {
                System.out.println("You can now draw a card.");
                printAvailableActions(ClientState.DRAW_CARD, false);
            }
            case NOT_IN_TURN ->   {
                System.out.println("Wait for your turn.");
                printGeneralCommands();
            }
        }
    }

//    @Override
//    public void notifyTurnChange(String player) {
//        if(this.player.equals(player)) {
//            System.out.println("It's your turn!");
//            printAvailableActions(ClientState.PLACE_TOTEM, false);
//        }
//        else {
//            System.out.println("It's now " + player + "'s turn!");
//        }
//    }

    @Override
    public void notifyNameSet(String newName) {
        this.player = newName;
        System.out.println("You have successfully set your name to " + newName + "!");
        System.out.println();
    }

    /**
     * Refreshes the available games' list in case a new game is created and the player is in the
     * "JOIN_GAME" TUI state.
     */
    @Override
    public void notifyNewAvailableGames() {
        if(tuiState == TUIState.JOIN_GAME) printAvailableGames();
    }

    @Override
    public void notifyGameCreated(int gameID) {
        tuiState = TUIState.IN_LOBBY;
        System.out.println("The game was successfully created with ID: " +  gameID + "!");
        System.out.println("You are the host of this game.");
        printAvailableActions(clientController.getClientState(), false);
    }

    @Override
    public void notifyPlayerJoinedLobby(String playerName) {
        if(tuiState == TUIState.IN_LOBBY) {
            System.out.println(playerName + " joined the lobby!");
        }
        //else if (tuiState == TUIState.JOIN_GAME) printAvailableGames();
    }

    @Override
    public void notifySuccessfullyJoinedGame(int gameID, ArrayList<String> playerNames, Map<String, Color> totemColors) {
        tuiState = TUIState.IN_LOBBY;
        System.out.println("You have successfully joined the game with ID: " + gameID + "!");
        printAvailableActions(clientController.getClientState(), false);
    }

    @Override
    public void notifyPlayerLeftLobby(String playerName, boolean hadColor) {
        if (playerName.equals(player)) {
            tuiState = TUIState.SETUP;
            System.out.println("You have successfully left the lobby!");
            printAvailableActions(clientController.getClientState(), false);
            return;
        }
        if(tuiState == TUIState.IN_LOBBY) {
            System.out.println("Player " + playerName + " left the lobby!");
            // se il player non ha ancora scelto il totem e il player che è uscito lo aveva scelto,
            // ristampa la lista dei colori aggiungendo il colore del player che è uscito
            if (!clientController.getLocalModel().getTotemColors().containsKey(this.player) && hadColor) {
                printAvailableColors();
            }
        }
        else if (tuiState == TUIState.JOIN_GAME) printAvailableGames();
    }

    @Override
    public void notifyNewHost() {
        System.out.println("You are the new host of this game.");
    }

    @Override
    public void notifyChosenTotemColor(String playerName, Color totemColor) {
        if (this.player.equals(playerName)) {
            System.out.println("You have successfully chosen the " + totemColor.colorize(String.valueOf(totemColor).toLowerCase()) + " totem!");
        }
        else if (tuiState == TUIState.IN_LOBBY) {
            System.out.println(playerName + " has chosen the " + totemColor.colorize(String.valueOf(totemColor).toLowerCase()) + " totem!");
            // se il player non ha ancora scelto il totem, ristampa la lista dei colori rimuovendo
            // il colore del player che ha appena scelto il totem
            if (!clientController.getLocalModel().getTotemColors().containsKey(this.player)) {
                printAvailableColors();
            }
        }
    }

    @Override
    public void notifyGameStarted() {
        System.out.println("                  ---------------------------------------------------                 ");
        System.out.println("       -------------------------------------------------------------------------      ");
        System.out.println("-------------------------------- THE GAME HAS STARTED --------------------------------");
        System.out.println("--------------------------------------- GLHF! ----------------------------------------");
        System.out.println("       --------------------------------------------------------------------------     ");
        System.out.println("                  ----------------------------------------------------                ");
        System.out.println();
    }

//    @Override
//    public void notifyGameEvent() {
//
//    }

    @Override
    public void notifyGiveInitialFood(Map<String, Integer> initialFood) {
        System.out.println();
        System.out.println("Every player gets some initial food based on their first turn order!");
        System.out.println("You get " + initialFood.get(this.player) + "!");
        System.out.println();
        printOfferTrack();
        printGeneralCommands();
    }

    /**
     * The player is notified when a new round starts.
     * @param round the current round.
     */
    @Override
    public void notifyStartRound(int round) {
        if (round < 10) {
            System.out.println();
            System.out.println("Round " + round + " has started!");
        }
        else
            System.out.println("The last round has started!");
    }

    @Override
    public void notifyCardDrawn(String player, Card card, boolean topRow) {
        if(this.player.equals(player)) {
            System.out.println();
            System.out.println(player + " has drawn " + card.getCardID() + "!");
        }
        if(tuiState == TUIState.SHOW_TOP_ROW && topRow) printTopRow();
        else if(tuiState == TUIState.SHOW_BOTTOM_ROW && !topRow) printBottomRow();
    }

    /**
     * The player is notified when a player has placed his totem. If the player was visualizing the offer track
     * before this notification, the offer track view is reloaded.
     * @param playerName the name of the player who placed the totem
     * @param index the index of the offer tile on which the totem was placed.
     */
    @Override
    public void notifyTileChosen(String playerName, int index) {
        System.out.println();
        if(!playerName.equals(this.player))
            System.out.println("\n" + clientController.getLocalModel().getColors(playerName).colorize(playerName) + " placed his totem on tile " + index + "!");
        else
            System.out.println("\nYou placed your totem on tile " + index + "!");
        if(tuiState == TUIState.SHOW_OFFER_TRACK) printOfferTrack();
    }

    @Override
    public void notifyNewCurrentPlayer(String currentPlayerName, ClientState clientState) {
//        //TODO: da togliere questa sleep e fare una queue sia lato client che lato server
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
        if(currentPlayerName.equals(this.player)) {
            System.out.println("\nIt's your turn!");
            printAvailableActions(clientController.getClientState(), false);
        }
        else
            System.out.println("\nIt's " + clientController.getLocalModel().getColors(currentPlayerName).colorize(currentPlayerName) + "'s turn, wait patiently!");
    }

    @Override
    public void notifyNewGamePhase(GamePhase newGamePhase) {
        System.out.println();
        if(newGamePhase == GamePhase.START_TURN) {
            System.out.println("New game phase: It's time to place the totems!");
        }
        else
            System.out.println("New game phase: It's time to draw the cards!");
    }

    /**
     * Prints to terminal the available colors the players can choose while in the lobby.
     */
    private void printAvailableColors() {
        System.out.println();
        System.out.println("Available colors:");
        EnumSet<Color> availableColors = EnumSet.allOf(Color.class);
        for(Color color : clientController.getLocalModel().getTotemColors().values()) {
            availableColors.remove(color);
        }
        for(Color color : availableColors) {
            System.out.println("- " + color.colorize(String.valueOf(color)));
        }
    }

    private Map<Integer, GamePlayers> printAvailableGames() {
        Map<Integer, GamePlayers> availableGames = clientController.getAvailableGames();
        System.out.println();
        if (availableGames.isEmpty()) {
            return null;
        }
        System.out.println("These are the available games:");
        for(Map.Entry<Integer, GamePlayers> entry : availableGames.entrySet()) {
            System.out.println("- gameID: " + entry.getKey() + " || Number of players: " + entry.getValue().playersNum());
            System.out.print("  Players in lobby: ");
            // print dei player presenti in lobby
            for(String player : entry.getValue().playerNames()) {
                System.out.print(player);
                if (!player.equals(entry.getValue().playerNames().getLast()))
                    System.out.print(", ");
            }
            System.out.println(";");
        }
        return availableGames;
    }

    /**
     * Prints to terminal the list of available games a client can join after he sent the "join_game()" command
     * and takes in input the gameID of the game the client wants to join.
     */
    private void joinAvailableGames() {
        if (clientController.getClientState() != ClientState.SETUP) {
            throw new IllegalClientStateActionException("You cannot do that right now!");
        }
        boolean gameJoined = false;
        tuiState = TUIState.JOIN_GAME;
        Map<Integer, GamePlayers> availableGames = printAvailableGames();
        if(availableGames == null) {
            System.out.println("There are no available games. You'll be sent back to the setup state");
            tuiState = TUIState.IN_LOBBY;
            printAvailableActions(ClientState.SETUP, false);
            return;
        }
        System.out.println();
        System.out.println("Enter a gameID to join the respective game.");
        System.out.println("Enter \"exit\" to go back to the setup state:");
        do {
            Scanner scanner = new Scanner(System.in);
            String input = scanner.nextLine();
            try {
                int gameID = Integer.parseInt(input);
                if(!availableGames.containsKey(gameID)) {
                    System.out.println("ERROR: This gameID is not in the available games. Please enter a valid gameID.");
                }
                else {
                    gameJoined = true;
                    // prova a joinare il game
                    try {
                        clientController.joinGame(player, gameID);
                    }
                    catch (NotJoinableGameException e) {
                        System.out.println(e.getMessage());
                        joinAvailableGames();
                    }
                }
            } catch(NumberFormatException e) {
                if(input.equalsIgnoreCase("exit")) {
                    tuiState = TUIState.SETUP;
                    printAvailableActions(ClientState.SETUP, false);
                    break;
                }
                else System.out.println("Invalid input. Please enter a valid gameID or enter \"exit\" to go back to the setup state.");
            }
        } while (!gameJoined);
    }

    /**
     * Prints the available actions a player can make while in a certain state.
     * @param clientState The state of the TUI the player is currently visualising.
     */
    private void printAvailableActions(ClientState clientState, boolean help) {
        System.out.println();
        switch(clientState) {
            case SETUP:
                System.out.println("These are the available actions in the setup state:");
                System.out.println("- create_game(number_of_players): To create a new game with a specific number of players.");
                System.out.println("- join_game(): To join an existing game.");
                System.out.println("- modify_name(new_name): To modify your name.");
                break;
            case IN_LOBBY:
                System.out.println("These are the available actions you can take while in lobby:");
                System.out.println("- choose_totem_color(color): To choose an available totem color from the list.");
                System.out.println("- start_game(): To start the game. It only works if you're the host, if the lobby is full and if all the players have chosen their totem color.");
                System.out.println("- leave_game(): To leave the lobby.");
                printAvailableColors();
                break;
            case NOT_IN_TURN:
                System.out.println("You can't perform any action, since it's not your turn.");
                System.out.println("- help(): to know the available actions.");
                break;
            case PLACE_TOTEM:
                System.out.println("These are the available actions:");
                System.out.println("- place_totem(offer_track_index): Place your totem on the offer track's tile indicated by the index. The tile must not be occupied by another player.");
                System.out.println("- help(): to know the available actions.");
                break;
            case DRAW_CARD:
                System.out.println("These are the available actions:");
                System.out.println("- draw_card(top/bottom, char/building, offer_track_index): To draw a card from top or bottom row. You have also to specify if the card\nis a character card or a building and the index of the row.");
                System.out.println("- help(): to know the available actions.");
                break;
        }
        if(help && clientState != ClientState.SETUP && clientState != ClientState.CONNECTING) printGeneralCommands();
        System.out.println();
    }
    /**
    * This method prints to terminal the commands who can be performed at every game phase during the entire
     * course of the game.
     */
    private void printGeneralCommands() {
        System.out.println();
        System.out.println("These are the general commands you can run at any time:");
        System.out.println("- show_offer_track(): to visualize the current state of the offer track.");
        System.out.println("- show_top_row(): to visualize the current state of the top row.");
        System.out.println("- show_bottom_row(): to visualize the current state of the bottom row.");
        System.out.println("- show_card_info(cardID): to see all the information about the card.");
        System.out.println("- show_my_tribe(): to visualize your own tribe.");
        System.out.println("- show_other_tribe(player_name): to visualize the tribe of another player.");
        System.out.println();
        System.out.println("Type \"help()\" at any time to know which commands are available in that game phase or turn!");
    }
}
