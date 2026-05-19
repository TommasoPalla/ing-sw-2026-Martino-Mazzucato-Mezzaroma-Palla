package it.polimi.ingsw.Enums;
/*
this enum represents all the possible colours a player can choose to be*/

public enum Color {
    YELLOW("\u001B[33m"),
    WHITE("\u001B[38;5;250m"),
    RED("\u001B[31m"),
    BLACK("\u001B[35m"),
    BLUE("\u001B[34m");

    private final String ansiCode;
    private static final String RESET = "\u001B[0m";

    Color(String ansiCode) {
        this.ansiCode = ansiCode;
    }

    /**
     *
     */
    public String colorize(String text) {
        return this.ansiCode + text + RESET;
    }
}
