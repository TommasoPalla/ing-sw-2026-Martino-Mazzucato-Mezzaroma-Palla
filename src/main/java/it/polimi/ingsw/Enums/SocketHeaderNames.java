package it.polimi.ingsw.Enums;

public enum SocketHeaderNames {
    //azioni che il player puo' fare
    CREATE_GAME,
    CONNECT_TO_GAME,
    CHOOSE_TOTEM_COLOR,
    DRAW_CARD,
    CHOOSE_OFFER_TILE,
    START_GAME,

    //conseguenze che il server manda come aggiornamento ai player
    GAME_CREATED,
    GAME_STARTED,
    CONNECTED_TO_GAME,
    CHOSEN_TOTEM_COLOR,
    DRAWN_CARD,
    CHOSEN_OFFER_TILE,

    ADDED_FOOD,
    ADDED_SHAMAN_STARS,
    ADDED_PRESTIGE_POINTS,
    UPDATED_TOP_ROW,
    UPDATED_TOP_BUILDINGS,
    UPDATED_BOTTOM_ROW,
    UPDATED_BOTTOM_BUILDINGS,
    NEXT_PLAYER,
    CHANGED_GAME_PHASE,
    CHANGED_ERA

}
