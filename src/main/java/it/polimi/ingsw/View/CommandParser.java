package it.polimi.ingsw.View;

import it.polimi.ingsw.Controller.ClientController;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.CommandType;
import it.polimi.ingsw.Model.Users.IllegalDrawException;
import it.polimi.ingsw.Model.Users.UnavailableColorException;

/**
 * This record class is used as a command parser. It translates command from the view and
 * sends them to the client controller
 * @param clientController controller of the client to whom the view refers
 */
public record CommandParser(ClientController clientController) {
    // IN QUESTO MODO TUTTE LE EXCEPTION CATCHATE NEI VARI METODI PARSE THROWANO A LORO VOLTA UN EXCEPTION CATCHATA
    // DALLA TUIVIEW, C'È DA RAGIONARE A UNA SOLUZIONE PIÙ PULITA, AD ESEMPIO UN METODO NOTIFY

    /**
     * Parses the arguments of the arguments of a command input of the player.
     * @param command the type of command
     * @param argsString the string containing the command arguments
     * @return It returns an array of strings, which are the command arguments.
     */
    private String[] parseArguments(CommandType command, String argsString) {
        String[] commandArgs = argsString.split("\\s*,\\s*");
        int argsNum = commandArgs.length;
        if (argsNum != command.getArgsNum()) {
            throw new IllegalArgumentException("This command requires " + command.getArgsNum() + " arguments, but got " + argsNum);
        }
        return commandArgs;
    }

    private boolean parseRowBoolean(String argument){
        if (argument.equalsIgnoreCase("top")) return true;
        else if (argument.equalsIgnoreCase("bottom")) return false;
        else throw new IllegalArgumentException("Row argument is invalid");
    }

    private boolean parseCardBoolean(String argument){
        if (argument.equalsIgnoreCase("char")) return true;
        else if (argument.equalsIgnoreCase("building")) return false;
        else throw new IllegalArgumentException("Card argument is invalid");
    }

    public void parseChooseTotemColor(String argsString) {
        if (argsString.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: you have to choose a color!");
        }
        String[] commandArgs = parseArguments(CommandType.CHOOSE_COLOR, argsString);
        try {
            clientController.chooseTotem(Color.valueOf(commandArgs[0]));
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("ERROR: " + commandArgs[0] + " is not a valid color!");
        }
        catch(UnavailableColorException e) {
            throw new IllegalArgumentException("ERROR: this totem colore is already taken!");
        }
    }

    /**
     * This method parses the command to create a new game.
     * @param argsString the string with the arguments passed by the player.
     */
    public void parseCreateGame(String argsString) {
        if (argsString.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: enter a game name to create a new game.");
        }
        String[] commandArgs = parseArguments(CommandType.CREATE_GAME, argsString);
//        try {
//            clientController.createGame(commandArgs[0]);
//        } catch (GameAlreadyExistsException e) {
//            throw new IllegalArgumentException("ERROR: game already exists!");
//        }
    }

    /**
     * This method parses the command to pick a card from top or bottom row of the offer track.
     * @param argsString the string with the arguments passed by the player.
     */
    public void parseDrawCard(String argsString) {
        if (argsString.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: this command requires arguments.");
        }
        String[] commandArgs = parseArguments(CommandType.DRAW_CARD, argsString);

        try {
            int index = Integer.parseInt(commandArgs[2]);
            boolean fromTopRow = parseRowBoolean(commandArgs[0]);
            boolean fromBuilding = parseCardBoolean(commandArgs[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ERROR: the last argument is not a number!");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        }

        try {
            //clientController.drawCard(commandArgs[0], commandArgs[1], commandArgs[2]);
        } catch (IllegalDrawException e) {
            throw new IllegalArgumentException("ERROR: invalid index.");
        }
    }
}
