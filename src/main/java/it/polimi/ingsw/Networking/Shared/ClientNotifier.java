package it.polimi.ingsw.Networking.Shared;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.EventManagement.PlayerEventResults;
import it.polimi.ingsw.View.GamePlayers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Interface used by the server to notify clients about game events and state updates.
 * Implementations bridge the server-side logic to the specific networking protocol.
 */
public interface ClientNotifier {
    /**
     * Notifies that a new game has been created.
     * @param gameID the ID of the created game.
     * @param playerName the name of the creator.
     * @param playerNum the intended number of players.
     */
    void notifyGameCreated(int gameID, String playerName, int playerNum);

    /**
     * Notifies that the game has started.
     *
     * @param shuffledFirstPlayingOrder the turn order of players.
     * @param initialFood               the initial food assigned to each player.
     * @param buildingsTopRow
     * @param buildingsBottomRow
     */
    void notifyGameStarted(List<String> shuffledFirstPlayingOrder, Map<String,Integer> initialFood,
                           ArrayList<Card> firstTopRow, ArrayList<Card> firstBottomRow,
                           ArrayList<BuildingCard> buildingsTopRow, ArrayList<BuildingCard> buildingsBottomRow);

    /**
     * Notifies that a new player has connected to the lobby.
     * @param playerName the name of the new player.
     */
    void notifyNewPlayerConnected(String playerName);

    /**
     * Confirms a successful join and provides initial lobby state.
     * @param gameID the ID of the game joined.
     * @param playerNum the total number of players for the game.
     * @param players the list of players currently in the lobby.
     * @param totemColors the colors already chosen by players.
     */
    void notifySuccessfullyJoinedGame(int gameID, int playerNum, ArrayList<String> players, Map<String, Color> totemColors);

    /**
     * Notifies that a player has left the lobby.
     * @param playerName the name of the player who left.
     */
    void notifyPlayerLeftGame(String playerName);
    void notifyNewHost();

    /**
     * Updates the client with the list of available games.
     * @param availableGames map of game IDs to player information.
     */
    void notifyAvailableGames(Map<Integer, GamePlayers> availableGames);

    /**
     * Notifies that a player has chosen a totem color.
     * @param playerName the name of the player.
     * @param totemColor the color chosen.
     */
    void notifyTotemColor(String playerName, Color totemColor);

    /**
     * Signals the start of a new round.
     * @param lastEventsResults the results of all the events resolved at the end of the last round.
     */
    void notifyStartRound(Map<EventType, ArrayList<PlayerEventResults>> lastEventsResults);

    /**
     * Notifies that a card has been drawn by a player.
     * @param playerName the name of the player who drew.
     * @param fromTopRow true if from top row, false otherwise.
     * @param fromBuildings true if a building card, false for character.
     * @param index the position on the track.
     */
    void notifyDrawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index);

    /**
     * Notifies that a tile has been chosen on the offer track.
     * @param playerName the name of the player.
     * @param index the index of the chosen tile.
     */
    void notifyChosenTile(String playerName, int index);

    /**
     * Updates a player's food reserve.
     * @param playerName the name of the player.
     * @param food the amount of food to add or the new total (depending on implementation).
     */
    void notifyFoodToAdd(String playerName, int food);

    /**
     * Updates a player's Shaman Stars.
     * @param playerName the name of the player.
     * @param food the number of stars.
     */
    void notifyShamansStarsToAdd(String playerName, int food);

    /**
     * Updates a player's Prestige Points.
     * @param playerName the name of the player.
     * @param points the points to add.
     */
    void notifyPrestigePointsToAdd(String playerName, int points);

    /**
     * Updates the top row character cards.
     * @param newTopRow the new list of cards.
     */
    void notifyTopRow(ArrayList<Card> newTopRow);

    /**
     * Updates the top row building cards.
     * @param newTopBuildings the new list of building cards.
     */
    void notifyTopBuildings(ArrayList<BuildingCard> newTopBuildings);

    /**
     * Updates the bottom row character cards.
     * @param newBottomRow the new list of cards.
     */
    void notifyBottomRow(ArrayList<Card> newBottomRow);

    /**
     * Updates the bottom row building cards.
     * @param newBottomBuildings the new list of building cards.
     */
    void notifyBottomBuildings(ArrayList<BuildingCard> newBottomBuildings);

//    /**
//     * Notifies whose turn is next.
//     * @param playerName the name of the next player.
//     */
//    void notifyNextPlayer(String playerName);

    /**
     * Notifies a change in game phase.
     * @param newPhase the new phase.
     */
    void notifyGamePhase(GamePhase newPhase);

    /**
     * Notifies a change in era.
     * @param era the new era.
     */
    void notifyEra(int era);

    /**
     * Forces the client to quit, usually due to a critical error or disconnection.
     */
    void notifyForceQuit(String disconnectedPlayer);
}