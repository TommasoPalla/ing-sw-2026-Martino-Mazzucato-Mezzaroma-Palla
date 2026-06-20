package it.polimi.ingsw.Networking.Socket;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.EventManagement.PlayerEventResults;
import it.polimi.ingsw.View.GamePlayers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Remote interface for client-side callbacks in the Socket/TCP networking protocol.
 * Defines methods that the server can invoke on a connected client to push updates,
 * game state changes, and error reports.
 */
public interface VirtualSocketClient {
    /**
     * Notifies the client that a new game has been created.
     * @param gameID the unique identifier of the created game.
     * @param numPlayers the total number of players for the game.
     */
    void updateGameCreated(int gameID, int numPlayers);

    /**
     * Notifies the client that the game has started.
     *
     * @param firstTurnOrder     the list of player names in their playing order.
     * @param initialFood        the initial food resources assigned to each player.
     * @param firstTopRow
     * @param firstBottomRow
     * @param buildingsTopRow
     * @param buildingsBottomRow
     */
    void updateGameStarted(List<String> firstTurnOrder, Map<String, Integer> initialFood, ArrayList<Card> firstTopRow, ArrayList<Card> firstBottomRow, ArrayList<BuildingCard> buildingsTopRow, ArrayList<BuildingCard> buildingsBottomRow);

    /**
     * Updates the list of currently joinable games.
     * @param availableGames a map of game IDs to their respective player information.
     */
    void updateAvailableGames(Map<Integer, GamePlayers> availableGames);

    /**
     * Notifies that a new player has joined the current lobby.
     * @param playerName the name of the joined player.
     */
    void updatePlayerConnected(String playerName);

    /**
     * Confirms that the client has successfully joined a game.
     * @param gameID the ID of the joined game.
     * @param playerNum the required number of players.
     * @param playerNames the list of names of players already in the lobby.
     * @param totemColors a map associating each player to their chosen totem color.
     */
    void successfullyJoinedGame(int gameID, int playerNum, ArrayList<String> playerNames, Map<String, Color> totemColors);

    /**
     * Notifies that a player has left the game lobby.
     * @param playerName the name of the player who left.
     */
    void updatePlayerLeftGame(String playerName);

    /**
     * Updates the player and setting it as the new host
     */
    void updateNewHost();

    /**
     * Notifies that a player has chosen a totem color.
     * @param playerName the name of the player.
     * @param totemColor the color chosen.
     */
    void updateChosenTotemColor(String playerName, Color totemColor);


    /**
     * Signals the start of a new game round.
     * @param lastEventsResults the results of all the events resolved at the end of the last round.
     * @param newTopRow the list of cards of the repopulated top row.
     * @param newBottomRow the list of cards of the new bottom row.
     * @param newTopBuildings the list of Building cards of the new top row (changes only when era changes).
     * @param newBottomBuildings the list of Building cards of the new bottom row (may change only when era changes).
     */
    void updateStartRound(Map<EventType, ArrayList<PlayerEventResults>> lastEventsResults, ArrayList<Card> newTopRow,
                          ArrayList<Card> newBottomRow, ArrayList<BuildingCard> newTopBuildings,
                          ArrayList<BuildingCard> newBottomBuildings);

    /**
     * Notifies that a player has drawn a card.
     * @param playerName the name of the drawing player.
     * @param fromTopRow true if drawn from the top row, false otherwise.
     * @param fromBuildings true if a building card was drawn, false if a character card.
     * @param index the position of the card on the track.
     */
    void updateDrawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index);

    /**
     * Notifies that a player has passed his turn
     * @param playerName the player that passed
     */
    void updateTurnPassed(String playerName);

    /**
     * Notifies that a player has chosen a specific tile on the offer track.
     * @param playerName the name of the player.
     * @param index the index of the chosen tile.
     */
    void updateChosenTile(String playerName, int index);

    /**
     * Updates the food reserve of a specific player.
     * @param playerName the player's name.
     * @param food the new food amount.
     */
    void updateFood(String playerName, int food);

    /**
     * Updates the Shaman Stars for a specific player.
     * @param playerName the player's name.
     * @param stars the new number of stars.
     */
    void updateShamansStars(String playerName, int stars);

    /**
     * Updates the Prestige Points for a specific player.
     * @param playerName the player's name.
     * @param points the new prestige points.
     */
    void updatePrestigePoints(String playerName, int points);

    /**
     * Updates the Prestige Points for a specific player.
     * @param playerName the player's name.
     * @param discount the new builders discount.
     */
    void updateBuildersDiscount(String playerName, int discount) ;

    /**
     * Updates the Prestige Points for a specific player.
     * @param playerName the player's name.
     * @param discount the new gatherers discount.
     */
    void updateGatherersDiscount(String playerName, int discount) ;

    /**
     * Notifies a change in the game phase.
     * @param phase the new GamePhase.
     */
    void updateGamePhase(GamePhase phase) ;

    /**
     * Notifies a change in the game era.
     * @param era the new era number.
     */
    void updateEra(int era) ;

    /**
     * Notifies the end of the game.
     * @param finalRanking the final ranking of players.
     */
    void updateEndGame(Map<String,Integer> finalRanking) ;

    /**
     * Notifies the player about their position in the general leaderboard for games with the same number of players
     * as this one.
     * @param leaderboard a list of Strings representing the leaderboard with the final scores of all the game played
     *                    with the same number of players.
     * @param playerPosition the player's position in the leaderboard.
     */
    void updateLeaderboardInfo(List<String> leaderboard, int playerPosition) ;

    /**
     * Forces the client to terminate the session, usually due to a server-side disconnection.
     */
    void forceQuit(String disconnectedPlayer) ;
}
