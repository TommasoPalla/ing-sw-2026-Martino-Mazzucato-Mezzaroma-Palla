package it.polimi.ingsw.View;

import it.polimi.ingsw.Controller.ClientController.ClientController;
import it.polimi.ingsw.CustomException.UIException.IllegalClientStateActionException;
import it.polimi.ingsw.CustomException.UIException.InvalidSelectionException;

import it.polimi.ingsw.CustomException.UIException.NotEnoughPlayersException;
import it.polimi.ingsw.CustomException.UIException.NotJoinableGameException;
import it.polimi.ingsw.Enums.*;
import it.polimi.ingsw.Controller.ClientController.LightTribe;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Controller.ClientController.ClientModel;
import it.polimi.ingsw.Model.GameBoard.OfferTile;
import it.polimi.ingsw.View.Listeners.Listener;

import java.util.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TUIView implements ViewInterface, Listener {
    final private ClientModel localModel;
    final private ClientController clientController;
    final private CommandParser commandParser;
    private String player;
    private TUIState tuiState;

    public TUIView(ClientController clientController) {
        this.clientController = clientController;
        this.localModel = clientController.getLocalModel();
        this.commandParser = new CommandParser(clientController);
        /*
         * This attribute defines which state of the TUI the player is currently visualizing.
         */
        this.tuiState = TUIState.SETUP;
        // Starts the thread of this TUI, using 'run()' as Thread.run() method
        Thread TUIThread = new Thread(this::runView);
        TUIThread.start();
    }


    @Override
    public void runView() {
        System.out.println("Benvenuto su Mesos sesos pesos quevos");
        System.out.println("Scegli il tuo nome:");
        boolean nameVerified = false;
        do {
            Scanner scanner = new Scanner(System.in);
            String playerName = scanner.nextLine();
            try {
                nameVerified = true;
                clientController.setPlayerName(playerName);
                this.player = playerName;
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
//            throw new IllegalArgumentException("This method doesnt require arguments.");
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
            try {
                CommandType commandType = CommandType.valueOf(matcher.group(1).toUpperCase());
                commandParserSelector(commandType, matcher);
            }
            //sequenza di catch da gestire
            //per ora è gestito il caso di drawCard,
            //stampa "cant draw this card" + "insufficient food / cant draw event"
            catch(InvalidSelectionException e){
                System.out.println(e.getMessage()+e.getCause().getMessage());
            }
            catch (IllegalArgumentException | NotEnoughPlayersException | IllegalClientStateActionException e) {
                System.out.println(e.getMessage());
            }
        }
        else{
            System.out.println("Invalid command, please try again...");
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
            case JOIN_GAME              -> printAvailableGames();
            case LEAVE_GAME             -> { clientController.leaveGame();
                                             System.out.println("You have left the game successfully.");
                                             changeClientState(ClientState.SETUP);
                                           }
            case START_GAME             -> clientController.startGame(player);
            case MODIFY_NAME            -> commandParser.parseModifyName(argsString);
            case CHOOSE_TOTEM_COLOR     -> commandParser.parseChooseTotemColor(argsString);
            case SHOW_OFFER_TRACK       -> printOfferTrack();
            case SHOW_TOP_ROW           -> printTopRow();
            case SHOW_BOTTOM_ROW        -> printBottomRow();
            case SHOW_MY_TRIBE          -> printTribe(player);
            //case SHOW_OTHER_TRIBE       -> parsePrintTribe();
            case DRAW_CARD              -> commandParser.parseDrawCard(argsString);
            //case PLACE_TOTEM            ->
            case HELP                   -> printAvailableActions(clientController.getClientState());
            default                     -> throw new IllegalArgumentException("ERROR: Invalid command, please try again or enter \"help()\" to know the available commands.");
        }
    }

    /**
     * Prints to terminal the tribe of the player
     */
    private void printTribe(String playerName) {
        System.out.println("This is your tribe:\n");
        LightTribe localTribe = localModel.getPlayerTribe(player);
        if(localTribe != null) {
            System.out.println("Food Reserve: " + localTribe.getFoodReserve());
            System.out.println("Prestige Points: " + localTribe.getPrestigePoints());
            for(CharacterRole role : CharacterRole.values()) {
                if(localTribe.getPopulation().get(role).isEmpty()) continue;
                System.out.print(role + ": ");
                printRoleCardsInPopulation(clientController.getLocalModel().getPlayerTribe(playerName), role );
                System.out.println();
            }
        }
    }

    /**
     * Prints to terminal a list of cards of a specific role present in a player's tribe.
     * @param playerTribe the reference to the player's tribe whose cards want to be visualised.
     * @param role the role of the character cards which want to be visualised.
     */
    private void printRoleCardsInPopulation(LightTribe playerTribe, CharacterRole role) {
        ArrayList<CharacterCard> cards = playerTribe.getPopulation().get(role);
        ArrayList<Object> cardStats;
        int i = 0;
        System.out.print(role.toString() + ": ");
        for(CharacterCard card : cards) {
            cardStats = card.getUsefulStats();
            System.out.print(card.getCardID());
            while ( i <= cardStats.size()) {
                System.out.println(cardStats.get(i).toString() + ": " + cardStats.get(i+1).toString());
                i = i + 2;
            }
            // bisogna trovare un modo per stampare le carte anche in base al loro ruolo, possibilmente senza switch
            System.out.println();
        }
        System.out.println();
    }

    /**
     * Prints to terminal the top row of the offerTrack
     */
    private void printTopRow() {
        if(localModel.getTopRow().isEmpty()) {
            System.out.println("La fila superiore è vuota");
            return;
        }
    }

    /**
     * Prints to terminal the bottom row of the offerTrack
     */
    private void printBottomRow() {
        if(localModel.getBottomRow().isEmpty()) {
            System.out.println("La fila inferiore è vuota");
            return;
        }
    }

    private void printOfferTrack() {
        StringBuilder topBorder = new StringBuilder();
        StringBuilder actionRow = new StringBuilder();
        StringBuilder playerRow = new StringBuilder();
        StringBuilder bottomBorder = new StringBuilder();

        tuiState = TUIState.SHOW_OFFER_TRACK;
        System.out.print("This is the current offer track");
        for (OfferTile tile : localModel.getOfferTiles()) {
            topBorder.append("+-----------------+ ");

            if (tile.getFoodBonus() != 0) actionRow.append(String.format("| %-15s | ", tile.getFoodBonus()));
            else if (tile.getCardsFromAbove() != 0 && tile.getCardsFromBelow() != 0) {
                actionRow.append(String.format("| %-15s | ", "^ " + tile.getCardsFromAbove() + " v " + tile.getCardsFromBelow()));
            } else if (tile.getCardsFromAbove() != 0) {
                actionRow.append(String.format("| %-15s | ", "^ " + tile.getCardsFromAbove()));
            } else actionRow.append(String.format("| %-15s | ", "v " + tile.getCardsFromBelow()));

            String playerOccupant = tile.getCurrentOccupant();
            if (playerOccupant != null) {
                if (playerOccupant.length() > 15) {
                    playerOccupant = playerOccupant.substring(0, 12) + "...";
                    playerRow.append(String.format("| %-15s | ", playerOccupant));
                }
            }
            else playerRow.append(String.format("| %-15s | ", "---"));

            bottomBorder.append("+-----------------+ ");
        }
        System.out.println(topBorder);
        System.out.println(actionRow);
        System.out.println(playerRow);
        System.out.println(bottomBorder);
    }

    public void changeClientState(ClientState clientState) {
        System.out.println();
        // non sono sicuro che il clientState vada cambiato nella TUI
        clientController.setClientState(clientState);
        switch (clientState) {
            case SETUP -> {
                System.out.println("You are in the setup state.");
                tuiState = TUIState.SETUP;
                printAvailableActions(ClientState.SETUP);
            }
            case IN_LOBBY ->  {
                System.out.println("Welcome to the lobby!");
                tuiState = TUIState.IN_LOBBY;
                printAvailableActions(ClientState.IN_LOBBY);
            }
            case PLACE_TOTEM ->  {
                System.out.println("It's your turn to place the totem!");
                printAvailableActions(ClientState.PLACE_TOTEM);
            }
            case DRAW_CARD ->   {
                System.out.println("You can now draw a card.");
                printAvailableActions(ClientState.DRAW_CARD);
            }
            case NOT_IN_TURN ->   {
                System.out.println("Wait for your turn.");
                printGeneralCommands();
            }
        }
    }

    @Override
    public void notifyTurnChange(String player) {
        if(this.player.equals(player)) {
            System.out.println("It's your turn!");
            printAvailableActions(ClientState.PLACE_TOTEM);
        }
        else {
            System.out.println("It's now " + player + "'s turn!");
        }
    }

    @Override
    public void notifyGameCreated(int gameID) {
        System.out.println("The game was successfully created with ID: " +  gameID + "!");
        System.out.println("You are the host of this game.");
        changeClientState(ClientState.IN_LOBBY);
    }

    @Override
    public void notifyGameJoined(int gameID) {
        System.out.println("You have successfully joined the game with ID: " +  gameID + "!");
        changeClientState(ClientState.IN_LOBBY);
    }

    @Override
    public void notifyPlayerJoined(int gameID, String playerName) {
        if(tuiState == TUIState.IN_LOBBY && gameID == localModel.getGameId()) {
            System.out.println("Player " + playerName + " joined the lobby!");
        }
        else if (tuiState == TUIState.JOIN_GAME) printAvailableGames();
    }

    @Override
    public void notifyPlayerLeft(int gameID, String playerName) {
        if(tuiState == TUIState.IN_LOBBY && gameID == localModel.getGameId()) {
            System.out.println("Player " + playerName + " left the lobby!");
        }
        else if (tuiState == TUIState.JOIN_GAME) printAvailableGames();
    }

    @Override
    public void notifyGameStarted() {
        System.out.println("The game has started! GLHF!");
    }

    @Override
    public void notifyGameEvent() {

    }

    /**
     * The player is notified when a player has placed his totem. If the player was visualizing the offer track
     * before this notification, the offer track view is reloaded.
     * @param player the name of the player who placed the totem
     * @param offerTile the offer tile on which the totem was placed.
     */
    @Override
    public void notifyTotemPlaced(String player, OfferTile offerTile) {
        if(this.player.equals(player)) {
            System.out.println();
            System.out.println(player + " has placed his totem on offer tile " + offerTile.getTileCode());
        }
        if(tuiState == TUIState.SHOW_OFFER_TRACK) printOfferTrack();
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
     * Prints to terminal the list of available games a client can join after he sent the "join_game()" command
     * and takes in input the gameID of the game the client wants to join.
     */
    private void printAvailableGames() {
        boolean gameJoined = false;
        tuiState = TUIState.JOIN_GAME;
        Map<Integer, GamePlayers> availableGames = clientController.getActiveGames();
        System.out.println();
        if (availableGames.isEmpty()) {
            System.out.println("There are no available games. You'll be sent back to the setup state");
            printAvailableActions(ClientState.SETUP);
            return;
        }
        System.out.println("These are the available games:");
        for(Map.Entry<Integer, GamePlayers> entry : availableGames.entrySet()) {
            System.out.println("- gameID: " + entry.getKey() + " || Number of players: " + entry.getValue().playersNum());
            System.out.print("  Players in lobby: ");
            for(String player : entry.getValue().playerNames()) {
                System.out.print(player + ", ");
            }
            System.out.println(";");
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
                        changeClientState(ClientState.IN_LOBBY);
                    }
                    catch (NotJoinableGameException e) {
                        System.out.println(e.getMessage());
                        printAvailableGames();
                    }
                }
            } catch(NumberFormatException e) {
                if(input.equalsIgnoreCase("exit")) {
                    tuiState = TUIState.SETUP;
                    printAvailableActions(ClientState.SETUP);
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
    private void printAvailableActions(ClientState clientState) {
        System.out.println();
        System.out.println("These are the available actions in the " +  clientState.toString().toLowerCase() + " state:");
        switch(clientState) {
            case SETUP:
                System.out.println("- create_game(number_of_players): To create a new game with a specific number of players.");
                System.out.println("- join_game(): To join an existing game.");
                System.out.println("- modify_name(new_name): To modify your name.");
                break;
            case IN_LOBBY:
                System.out.println("- choose_totem_color(color): To choose an available totem color from the list.");
                System.out.println("- start_game(): To start the game. It only works if you're the host.");
                System.out.println("- leave_game(): To end the game.");
                System.out.println();
                System.out.println("Available totem colors: ");
                EnumSet<Color> availableColors = EnumSet.allOf(Color.class);
//                for(Color color : localModel.getTotemColors().values()) {
//                    availableColors.remove(color);
//                }
//                for(Color color : availableColors) {
//                    System.out.println("- " + color);
//                }
                break;
            case PLACE_TOTEM:
                System.out.println("- place_totem(offer_track_index)");
                printGeneralCommands();
            case DRAW_CARD:
                System.out.println("- draw_card(top/bottom, char/building, offer_track_index): To draw a card from top or bottom row. You have also to specify if the card\nis a character card or a building and the index of the row.");
                printGeneralCommands();
        }
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
        System.out.println(" -show_other_tribe(player_name): to visualize the tribe of another player.");
    }
}
