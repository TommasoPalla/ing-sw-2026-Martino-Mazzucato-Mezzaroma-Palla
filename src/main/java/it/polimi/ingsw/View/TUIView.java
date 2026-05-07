package it.polimi.ingsw.View;

import it.polimi.ingsw.Controller.ClientController.ClientController;
import it.polimi.ingsw.CustomException.UIException.InvalidSelectionException;
import it.polimi.ingsw.Enums.*;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Controller.ClientController.ClientModel;
import it.polimi.ingsw.Model.GameBoard.OfferTile;
import it.polimi.ingsw.Controller.ClientController.LightTribe;
import it.polimi.ingsw.View.Listeners.Listener;

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
                //clientController.setPlayerName(playerName);
                nameVerified = true;
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
    private void offerTrackTUIView(String args) {
        if (!args.trim().isEmpty()) {
            throw new IllegalArgumentException("This method doesnt require arguments.");
        }
        tuiState = TUIState.SHOW_OFFER_TRACK;
        //printOfferTrack();
    }

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
            catch (IllegalArgumentException e) {
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
     * @param matcher the matcher containing the information about the input string, such as the
     *                arguments of the command.
     */
    private void commandParserSelector(CommandType commandType, Matcher matcher ) {
        String argsString = matcher.group(2);

        switch (commandType) {
            case CREATE_GAME            -> commandParser.parseCreateGame(argsString);
            case CHOOSE_TOTEM_COLOR     -> commandParser.parseChooseTotemColor(argsString);
            case SHOW_OTHER_TRIBE       -> offerTrackTUIView(argsString);
            case DRAW_CARD              -> commandParser.parseDrawCard(argsString);
            default                     -> throw new IllegalArgumentException("Invalid command, please try again...");
        }
    }

    /**
     * Prints to terminal the tribe of the player
     */
    private void printPersonalTribe() {
        System.out.println("This is your tribe:\n");
        LightTribe localTribe = localModel.getPlayerTribe(player);
        if(localTribe != null) {
            System.out.println("Food Reserve: " + localTribe.getFoodReserve());
            System.out.println("Prestige Points: " + localTribe.getPrestigePoints());
            for(CharacterRole role : CharacterRole.values()) {
                if(localTribe.getPopulation().get(role).isEmpty()) continue;
                System.out.print(role + ": ");
                //printRoleCardsInPopulation(clientController.getLocalModel().getPlayerTribe(), role );
                System.out.println("\n");
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
        for(CharacterCard card : cards) {
            // bisogna trovare un modo per stampare le carte anche in base al loro ruolo, possibilmente senza switch
            System.out.println();
        }
    }

    /**
     * Prints to terminal the top row of the offerTrack
     */
    private void printTopRow() {
        if(localModel.getTopRow().isEmpty()) {
            System.out.println("La fila superiore è vuota");
            return;
        }
        // TopRowTUIView();
    }

    /**
     * Prints to terminal the bottom row of the offerTrack
     */
    private void printBottomRow() {
        if(localModel.getBottomRow().isEmpty()) {
            System.out.println("La fila inferiore è vuota");
            return;
        }
        // TopRowTUIView();
    }

//    private void printOfferTrack() {
//        for (OfferTile offerTile : localModel.getOfferTiles()) {
//            System.out.println("______ ");
//        }
//        System.out.println("\n");
//        System.out.println("|\n ");
//        for (OfferTile offerTile : localModel.getOfferTiles()) {
//                System.out.println("Food: " + offerTile.getFoodBonus() + ", ");
//                System.out.println("^ " + offerTile.getCardsFromAbove() + ", ");
//                System.out.println("v " + offerTile.getCardsFromBelow());
//                System.out.println(" | ");
//        }
//        System.out.println("\n| ");
//        for (OfferTile offerTile : localModel.getOfferTiles()) {
//            if(offerTile.isOccupied()) {
//                System.out.println(offerTile.getCurrentOccupant().getName());
//            }
//            else  {
//                System.out.println("---");
//            }
//        }
//        //da finire, e da modificare perché ho sbagliato, meglio rappresentazione verticale dell'offerTrack
//    }

    public void changeClientState(ClientState clientState) {
        switch (clientState) {
            case SETUP -> {
                System.out.println("You are in the setup state.");
                printAvailableActions(ClientState.SETUP);
            }
            case IN_LOBBY ->  {
                System.out.println("Welcome to the lobby!");
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
            }
        }
    }

    @Override
    public void notifyTurnChange(String player) {
        if(this.player.equals(player)) {
            System.out.println("It's your turn!");
            printAvailableActions(ClientState.PLACE_TOTEM);
        }
        else  {
            System.out.println("It's now " + player + "'s turn!");
        }
    }

    @Override
    public void notifyGameEvent() {

    }

    @Override
    public void notifyTotemPlaced(String player, OfferTile offerTile) {

    }

    @Override
    public void notifyCardDrawn(String player, Card card, boolean topRow) {

    }

    /**
     * Prints the available actions a player can make while in a certain state.
     * @param clientState The state of the TUI the player is currently visualising.
     */
    private void printAvailableActions(ClientState clientState) {
        System.out.println("These are the available actions in the " +  clientState.toString().toLowerCase() + " state:");
        switch(clientState) {
            case SETUP:
                System.out.println("- create_game(number_of_players)");
                System.out.println("- join_game(gameID)");
                break;
            case IN_LOBBY:
                System.out.println("- choose_totem_color(color)");
                System.out.println("- start_game()");
                System.out.println();
                System.out.println("Available totem colors: ");
                EnumSet<Color> availableColors = EnumSet.allOf(Color.class);
                for(Color color : localModel.getTotemColors().values()) {
                    availableColors.remove(color);
                }
                for(Color color : availableColors) {
                    System.out.println("- " + color);
                }
                break;
            case PLACE_TOTEM:
                //
            case DRAW_CARD:
                //
        }
    }
}
