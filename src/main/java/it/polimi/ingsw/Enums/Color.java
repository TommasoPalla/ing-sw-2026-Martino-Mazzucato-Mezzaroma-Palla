package it.polimi.ingsw.Enums;

/**
 * This enum represents all the possible colors for the player's totems. It can be chosen once after entering
 * a game lobby, and it can't be modified after it is chosen.
 */
public enum Color {
    YELLOW("\u001B[33m"),
    WHITE("\u001B[38;5;250m"),
    RED("\u001B[31m"),
    PURPLE("\u001B[35m"),
    BLUE("\u001B[34m");

    /**
     * the Ansi code used to colorize the player's name on the CLI.
     */
    private final String ansiCode;

    /**
     * Used to reset the text color after colorizing a string.
     */
    private static final String RESET = "\u001B[0m";

    Color(String ansiCode) {
        this.ansiCode = ansiCode;
    }

    /**
     * This method colorizes the string passed as parameter with the respective color.
     * @param text the input text to colorize.
     * @return the colorized text, obtained by attaching the Ansi code related to the color.
     */
    public String colorize(String text) {
        return this.ansiCode + text + RESET;
    }
}
