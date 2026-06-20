package it.polimi.ingsw.Networking.Shared;

/**
 * This class binds a gameID to a player, since two players cannot be
 * in the same game with the same name, this univocally identifies
 * a player.
 * @param gameID the game the player is part of
 * @param playerName his name in the game
 */
public record PlayerRecord(int gameID, String playerName){}