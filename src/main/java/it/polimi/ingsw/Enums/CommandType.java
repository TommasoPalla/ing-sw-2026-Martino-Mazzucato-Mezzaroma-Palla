package it.polimi.ingsw.Enums;

/**
 * This enum contains all command types a player can send in input to the TUI
 */
public enum CommandType {
    CREATE_GAME("create_game", 1),
    JOIN_GAME("join_game", 1),
    START_GAME("start_game", 0),
    LEAVE_GAME("leave_game", 0),
    MODIFY_NAME("modify_name", 1),
    CHOOSE_TOTEM_COLOR("choose_color", 1),
    SHOW_MY_TRIBE("show_my_tribe", 0),
    SHOW_OFFER_TRACK("show_offer_track", 0),
    SHOW_OTHER_TRIBE("show_other_tribe", 1),
    SHOW_TOP_ROW("show_top_row", 0),
    SHOW_BOTTOM_ROW("show_bottom_row", 0),
    PLACE_TOTEM("place_totem", 1),
    DRAW_CARD("draw_card", 3),
    PASS_TURN("pass_turn", 0),
    SHOW_BUILDING_INFO("show_building_info", 1),
    HELP("help", 0);

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
