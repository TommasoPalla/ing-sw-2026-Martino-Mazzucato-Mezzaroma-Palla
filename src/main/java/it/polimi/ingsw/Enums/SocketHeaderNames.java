package it.polimi.ingsw.Enums;

public enum SocketHeaderNames {
    //azioni che il player puo' fare
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

    //conseguenze che il server manda come aggiornamento ai player
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
