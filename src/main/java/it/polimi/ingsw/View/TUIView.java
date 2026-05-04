package it.polimi.ingsw.View;

import it.polimi.ingsw.Controller.ClientController;
import it.polimi.ingsw.Enums.CharacterRole;
import it.polimi.ingsw.Enums.CommandType;
import it.polimi.ingsw.Enums.TUIState;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.Cards.Characters.CharacterCard;
import it.polimi.ingsw.Model.ClientModel;
import it.polimi.ingsw.Model.LightTribe;
import it.polimi.ingsw.View.GUIView.ViewInterface;

import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TUIView implements ViewInterface {
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
        // Starts the thread of this TUI, using "runTUI()" as Thread.run() method
        Thread TUIThread = new Thread(this::runTUI);
        TUIThread.start();
    }


    public void runTUI() {
        System.out.println("Benvenuto su Mesos sesos pesos quevos");
        System.out.println("Scegli il tuo nome:");
        Scanner scanner = new Scanner(System.in);
        String playerName = scanner.nextLine();
        //check della validità del nome e che non sia già utilizzato
        clientController.setPlayerName(playerName);
        this.player = playerName;
        System.out.println("Scegli il colore del totem:");
        Scanner scanner1 = new Scanner(System.in);
        //check scelta del totem
        String color = scanner1.nextLine();
        printAvailableActions(tuiState);
        // ciclo di ascolto comandi
        Scanner commandScanner = new Scanner(System.in);
        while(!Thread.currentThread().isInterrupted()) {
            String command = commandScanner.nextLine();
            // gestione instradamento command
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
        printAvailableActions(TUIState.SHOW_OFFER_TRACK);
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
     * @param matcher the matcher containing the information about the input string, like the
     *                arguments of the command.
     */
    private void commandParserSelector(CommandType commandType, Matcher matcher ) {
        String argsString = matcher.group(2);

        switch (commandType) {
            case CREATE_GAME            -> commandParser.parseCreateGame(argsString);
            case SHOW_OTHER_TRIBE       -> offerTrackTUIView(argsString);
            case DRAW_CARD              -> commandParser.parseDrawCard(argsString);
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

    /**
     * Prints the available actions a player can make while in a certain state of the TUI.
     * @param tuiState The state of the TUI the player is currently visualising.
     */
    private void printAvailableActions(TUIState tuiState) {
        System.out.println("Available Actions:\n");
        switch(tuiState) {
            case SETUP:
                //
            case IN_LOBBY:
                //
            case SHOW_OFFER_TRACK:
                //
            case SHOW_PERSONAL_TRIBE:
                //
            case SHOW_OTHER_TRIBE:
                //
            case SHOW_TOP_ROW:
                //
            case SHOW_BOTTOM_ROW:
                //
            case SHOW_TOP_BUILDINGS:
                //
            case SHOW_BOTTOM_BUILDINGS:
                //
        }

    }
}
