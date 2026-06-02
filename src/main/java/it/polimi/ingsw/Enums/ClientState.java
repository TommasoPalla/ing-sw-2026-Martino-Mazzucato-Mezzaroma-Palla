package it.polimi.ingsw.Enums;

/**
 * This enum contains all the possible states a client can have. It is used to decide which actions
 * and command he can perform and what he can visualize.
 */
public enum ClientState {
    CONNECTING,
    SETUP,
    IN_LOBBY,
    PLACE_TOTEM,
    DRAW_CARD,
    NOT_IN_TURN,
    END_GAME;
}
