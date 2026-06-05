package it.polimi.ingsw.View;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This record is used as a container of information for the available Mesos games a player can join.
 * @param playersNum the number of players that will play in that game.
 * @param playerNames the list of players that are waiting in the game lobby.
 */
public record GamePlayers(int playersNum, ArrayList<String> playerNames) implements Serializable {
}
