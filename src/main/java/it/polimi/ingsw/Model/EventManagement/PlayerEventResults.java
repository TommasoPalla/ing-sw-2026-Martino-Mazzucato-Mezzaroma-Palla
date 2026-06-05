package it.polimi.ingsw.Model.EventManagement;

import java.io.Serializable;

/**
 * This records contains the changes to a player Prestige Points and Food reserve after an event is resolved.
 * @param player the name of the player whose results are referred to.
 * @param foodAndPP an array of size 2 containing the new Food and Prestige Points gained or lost by the player.
 */
public record PlayerEventResults(String player, int[] foodAndPP) implements Serializable {}
