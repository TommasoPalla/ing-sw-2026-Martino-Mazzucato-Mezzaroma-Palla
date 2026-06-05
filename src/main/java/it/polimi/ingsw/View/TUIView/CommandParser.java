package it.polimi.ingsw.View.TUIView;

import it.polimi.ingsw.Controller.ClientController.ClientController;
import it.polimi.ingsw.Controller.ClientController.LightTribe;
import it.polimi.ingsw.CustomException.IllegalActionPhaseException;
import it.polimi.ingsw.CustomException.IllegalClientStateActionException;
import it.polimi.ingsw.CustomException.OccupiedTileException;
import it.polimi.ingsw.CustomException.UIException.AlreadyChosenTotemException;
import it.polimi.ingsw.CustomException.UnavailableColorException;
import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.CommandType;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Utils.CardIDValidator;


/**
 * This record class is used as a command parser. It translates commands from the view and
 * sends them to the client controller.
 * @param clientController controller of the client to whom the view refers
 */
public record CommandParser(ClientController clientController) {

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

    /**
     * It parses the parameter of the draw_card command indicating the row from where the player wants to draw the card.
     * It throws {@link IllegalArgumentException} if the argument is invalid.
     * @param argument Must be 'top' or 'bottom'.
     * @return true if the argument passed is 'top', false if 'bottom'
     */
    private boolean parseRowBoolean(String argument){
        if (argument.equalsIgnoreCase("top")) return true;
        else if (argument.equalsIgnoreCase("bottom")) return false;
        else throw new IllegalArgumentException("ERROR: Row argument is invalid");
    }

    /**
     * It parses the parameter of the draw_card command indicating the type of card the player wants to draw (Character
     * or Building). It throws {@link IllegalArgumentException} if the argument is invalid.
     * @param argument must be 'char' or 'building'.
     * @return true if the argument passed is 'building', false if 'char'
     */
    private boolean parseCardBoolean(String argument){
        if (argument.equalsIgnoreCase("char")) return false;
        else if (argument.equalsIgnoreCase("building")) return true;
        else throw new IllegalArgumentException("ERROR: Card argument is invalid");
    }

    /**
     * Parses the choice of the totem color of the player. It requires 1 argument, containing the name of one of the
     * 5 possible totem colors
     * @param argsString the string containing the arguments of the command.
     */
    public void parseChooseTotemColor(String argsString) {
        if (argsString.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: you have to choose a color!");
        }
        String[] commandArgs = parseArguments(CommandType.CHOOSE_TOTEM_COLOR, argsString);
        try {
            clientController.chooseTotemColor(Color.valueOf(commandArgs[0].toUpperCase()));
        } catch (IllegalClientStateActionException e) {
            throw new IllegalClientStateActionException(e.getMessage());
        } catch (UnavailableColorException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("ERROR: " + commandArgs[0] + " is not a valid color!");
        } catch (AlreadyChosenTotemException e){
            Color alreadyChosen = clientController.getLocalModel().getColors(clientController.getPlayerName());
            throw new IllegalArgumentException("You already chose the " + alreadyChosen.colorize(alreadyChosen.toString()) + " totem.");
        }
    }

    /**
     * Parses the choice of the Offer Tile of the Offer Track where the player wants to place their totem. It requires
     * 1 argument, containing the index of the Offer Tile.
     * @param argsString the string containing the arguments of the command.
     */
    public void parseChooseOfferTile(String argsString){
        if (argsString.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: you have to choose an offer tile!");
        }
        String[] commandArgs = parseArguments(CommandType.PLACE_TOTEM, argsString);
        int index;
        try {
            index = Integer.parseInt(commandArgs[0]);
        } catch (NumberFormatException e) {
            //throw new RuntimeException(e.getMessage()); forse??
            return;
        }
        try {
            clientController.chooseOfferTile(index);
        } catch (IllegalClientStateActionException e) {
            throw new IllegalClientStateActionException(e.getMessage());
        } catch (IllegalActionPhaseException e) {
            System.out.println(clientController.getLocalModel().getCurrentPhase());
            throw new IllegalActionPhaseException();
        } catch (OccupiedTileException e) {
            throw new OccupiedTileException();
        } catch (IndexOutOfBoundsException e) {
            throw new IllegalArgumentException("ERROR: the index must be a number ranging from 1 to " + clientController.getLocalModel().getOfferTiles().size());
        }
    }

    /**
     * Parses the command to create a new game. It requires 1 argument, the number of players needed to start the game.
     * @param argsString the string containing the arguments of the command.
     */
    public void parseCreateGame(String argsString) {
        if (argsString.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: you need to enter the number of players!");
        }
        String[] commandArgs = parseArguments(CommandType.CREATE_GAME, argsString);
        try {
            clientController.createGame(Integer.parseInt(commandArgs[0]));
        } catch (IllegalActionPhaseException e) {
            throw new IllegalActionPhaseException();
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ERROR: command argument must be a number!");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("ERROR: invalid number of participants. Mesos requires from 2 to 5 players!");
        }
    }

    /**
     * This method parses the command to change the player name. It requires 1 argument, the String containing the new
     * nickname of the player.
     * @param argsString the string containing the arguments of the command.
     */
    public void parseModifyName(String argsString) {
        if (argsString.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: you need to enter the new name you want to use!");
        }
        String[] commandArgs = parseArguments(CommandType.CREATE_GAME, argsString);
        try {
            clientController.setPlayerName(commandArgs[0]);
        } catch (IllegalClientStateActionException e) {
            throw new IllegalClientStateActionException(e.getMessage());
        }
    }

    /**
     * Parses the command to pick a card from top or bottom row of the offer track. It requires 3 arguments:
     * the row (top or bottom), the type of card (Character or Building) and the index of the respective row.
     * @param argsString the string containing the arguments of the command.
     */
    public void parseDrawCard(String argsString) {
        int index;
        boolean fromTopRow;
        boolean fromBuilding;
        if (argsString.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: this command requires arguments.");
        }
        String[] commandArgs = parseArguments(CommandType.DRAW_CARD, argsString);

        try {
            index = Integer.parseInt(commandArgs[2]);
            fromTopRow = parseRowBoolean(commandArgs[0]);
            fromBuilding = parseCardBoolean(commandArgs[1]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("ERROR: the last argument is not a number!");
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
        //non vanno try e catch perché drawCard lancia eccezioni già "formattate" nel formato che piace alla view
        clientController.drawCard(fromTopRow, fromBuilding,  index);
        /*catch (IllegalActionPhaseException e) {
            throw new IllegalActionPhaseException();
        }*/
    }

    /**
     * Parses the command to show another player's tribe. It requires 1 argument, the name of the player.
     * @param argsString the string containing the arguments of the command.
     * @return the name of the player in input, cleaned.
     */
    //TODO: da fixare (?)
    public String parseOtherTribe(String argsString){
        if(argsString.trim().isEmpty())
            throw new IllegalArgumentException("ERROR: this command requires arguments.");
        String[] commandArgs = parseArguments(CommandType.SHOW_OTHER_TRIBE, argsString);

        if(commandArgs[0] != null)
            return commandArgs[0];
        else return "";
    }

    /**
     * Parses the command to show a specific building's effect.
     * @param argsString the argument passed via terminal input, the cardID of the card we want to know info of.
     * @return the description of the card.
     */
    public BuildingCard parseBuildingInfo(String argsString){
        if(argsString.trim().isEmpty()) {
            throw new IllegalArgumentException("ERROR: this command requires arguments.");
        }
        String cardID = argsString.replace("\"", "").replace("'", "").trim();
        CardIDValidator validator = new CardIDValidator(clientController);

        if(!validator.checkCardID(cardID))
            throw new IllegalArgumentException("ERROR: CardID '" + cardID + "' is not valid cardID or the card is not currently visible." +
                    " Please enter a valid cardID.");

        // if the cardID is valid we iterate through top and bottom buildings to find the correct description associated with the cardID
        for(BuildingCard building : clientController.getLocalModel().getTopBuildings()){
            if(building != null && cardID.equalsIgnoreCase(building.getCardID()))
                return building;
        }

        for(BuildingCard building : clientController.getLocalModel().getBottomBuildings()){
            if(building != null && cardID.equalsIgnoreCase(building.getCardID()))
                return building;
        }

        for(String playerName : clientController.getLocalModel().getPlayersNames()){
            LightTribe tribe = clientController.getLocalModel().getPlayerTribe(playerName);
            for(BuildingCard building : tribe.getBuildings()){
                if(building != null && cardID.equalsIgnoreCase(building.getCardID()))
                    return building;
            }
        }

        // otherwise we default to error (should never happen, but it's safe to do)
        throw new RuntimeException("ERROR: The cardID '" + cardID + "' does not correspond to any available buildings.");
    }
}