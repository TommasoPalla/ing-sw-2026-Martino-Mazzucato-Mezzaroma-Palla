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
        if (argument.equalsIgnoreCase("char")) return false;
        else if (argument.equalsIgnoreCase("building")) return true;
        else throw new IllegalArgumentException("ERROR: Card argument is invalid");
    }

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
     * This method parses the command to create a new game.
     * @param argsString one argument is expected: the number of players.
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
     * This method parses the command to change the player name.
     * @param argsString one argument is expected: the new name of the player.
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
     * This method parses the command to pick a card from top or bottom row of the offer track.
     * @param argsString the string with the arguments passed by the player.
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
     * Parsing the request to show another player's tribe
     * @param argsString the argument(s) passed via terminal input
     * @return the name of the player in input, cleaned
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
     * Parsing the request to show a specific card info
     * @param argsString the argument passed via terminal input, the cardID of the card we want to know info of
     * @return the description of the card
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