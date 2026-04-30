package it.polimi.ingsw.Enums;

/**
 * This enum contains all command types a player can send in input to the TUI
 */
public enum CommandType {
    CREATE_GAME("create_game", 1),
    JOIN_GAME("join_game", 1),
    CHOOSE_COLOR("choose_color", 1),
    SHOW_MY_TRIBE("show_my_tribe", 0),
    SHOW_OTHER_TRIBE("show_other_tribe", 1),
    SHOW_TOP_ROW("show_top_row", 0),
    SHOW_BOTTOM_ROW("show_bottom_row", 0),
    PLACE_TOTEM("place_totem", 1),
    DRAW_CARD("draw_card", 3);

    /*
    * Name of the command.
     */
    private String commandName;
    /*
    * Number of arguments required by the command.
     */
    private int argsNum;

    CommandType(String commandName, int argsNum) {
        this.commandName = commandName;
        this.argsNum = argsNum;
    }

    /*
    * Getters for commands.
     */
    public String getCommandName() {
        return this.commandName;
    }

    public int getArgsNum() {
        return this.argsNum;
    }
}
