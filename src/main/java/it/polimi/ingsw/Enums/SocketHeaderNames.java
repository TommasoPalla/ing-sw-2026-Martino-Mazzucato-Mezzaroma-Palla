package it.polimi.ingsw.Enums;

/**
 * This enum represents all the possible socket header names a socket message can have, divided by context, based on if
 * it's a player's action, the updates from the server or the connection handling.
 */
public enum SocketHeaderNames {
    // actions that a player can perform
    CREATE_GAME,
    START_GAME,
    SET_NAME,       //TODO: forse fare il corrispettivo NAME_SET
    GET_AVAILABLE_GAMES,
    JOIN_GAME,
    LEAVE_GAME,
    CHOOSE_TOTEM_COLOR,
    DRAW_CARD,
    PASS_TURN,
    CHOOSE_OFFER_TILE,

    // updates that the server sends to players
    GAME_CREATED,
    GAME_STARTED,
    NEW_HOST,
    RECEIVED_AVAILABLE_GAMES,
    PLAYER_JOINED_GAME,
    SUCCESSFULLY_JOINED,
    LEFT_GAME,
    CHOSEN_TOTEM_COLOR,
    DRAWN_CARD,
    TURN_PASSED,
    CHOSEN_OFFER_TILE,

    START_ROUND,
    ADDED_FOOD,
    ADDED_SHAMAN_STARS,
    ADDED_BUILDERS_DISCOUNT,
    ADDED_GATHERERS_DISCOUNT,
    ADDED_PRESTIGE_POINTS,
    UPDATED_TOP_ROW,
    UPDATED_TOP_BUILDINGS,
    UPDATED_BOTTOM_ROW,
    UPDATED_BOTTOM_BUILDINGS,
    NEXT_PLAYER,
    CHANGED_GAME_PHASE,
    CHANGED_ERA,
    GAME_ENDED,

    PING,
    FORCE_QUIT
}
