package it.polimi.ingsw.Networking.RMI;

import it.polimi.ingsw.Enums.Color;
import it.polimi.ingsw.Enums.EventType;
import it.polimi.ingsw.Enums.GamePhase;
import it.polimi.ingsw.Model.Cards.BuildingCard;
import it.polimi.ingsw.Model.Cards.Card;
import it.polimi.ingsw.Model.EventManagement.PlayerEventResults;
import it.polimi.ingsw.View.GamePlayers;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Remote interface for client-side callbacks in the RMI networking protocol.
 * Defines methods that the server can invoke on a connected client to push updates,
 * game state changes, and error reports.
 */
public interface VirtualRMIClient extends Remote {

    void showUpdate() throws RemoteException;
    void reportError(String errorMessage) throws RemoteException;

    /**
     * Notifies the client that a new game has been created.
     * @param gameID the unique identifier of the created game.
     * @param numPlayers the total number of players for the game.
     * @throws RemoteException if RMI communication fails.
     */
    void updateGameCreated(int gameID, int numPlayers) throws RemoteException;

    /**
     * Notifies the client that the game has started.
     *
     * @param firstTurnOrder     the list of player names in their playing order.
     * @param initialFood        the initial food resources assigned to each player.
     * @param firstTopRow
     * @param firstBottomRow
     * @param buildingsTopRow
     * @param buildingsBottomRow
     * @throws RemoteException if RMI communication fails.
     */
    void updateGameStarted(List<String> firstTurnOrder, Map<String,Integer> initialFood, ArrayList<Card> firstTopRow,
                           ArrayList<Card> firstBottomRow, ArrayList<BuildingCard> buildingsTopRow,
                           ArrayList<BuildingCard> buildingsBottomRow) throws RemoteException;

    /**
     * Updates the list of currently joinable games.
     * @param availableGames a map of game IDs to their respective player information.
     * @throws RemoteException if RMI communication fails.
     */
    void updateAvailableGames(Map<Integer, GamePlayers> availableGames) throws RemoteException;

    /**
     * Notifies that a new player has joined the current lobby.
     * @param playerName the name of the joined player.
     * @throws RemoteException if RMI communication fails.
     */
    void playerJoinedGame(String playerName) throws RemoteException;

    /**
     * Confirms that the client has successfully joined a game.
     * @param gameID the ID of the joined game.
     * @param numPlayers the required number of players.
     * @param players the list of names of players already in the lobby.
     * @param totemColors a map associating each player to their chosen totem color.
     * @throws RemoteException if RMI communication fails.
     */
    void successfullyJoinedGame(int gameID, int numPlayers, ArrayList<String> players, Map<String, Color> totemColors) throws RemoteException;

    /**
     * Notifies that a player has left the game lobby.
     * @param playerName the name of the player who left.
     * @throws RemoteException if RMI communication fails.
     */
    void playerLeftGame(String playerName) throws RemoteException;

    void updateNewHost() throws RemoteException;

    /**
     * Notifies that a player has chosen a totem color.
     * @param playerName the name of the player.
     * @param totemColor the color chosen.
     * @throws RemoteException if RMI communication fails.
     */
    void chosenTotem(String playerName, Color totemColor) throws RemoteException;


    /**
     * Signals the start of a new game round.
     * @param lastEventsResults the results of all the events resolved at the end of the last round.
     * @throws RemoteException if RMI communication fails.
     */
    void startRound(Map<EventType, ArrayList<PlayerEventResults>> lastEventsResults) throws RemoteException;

    /**
     * Notifies that a player has drawn a card.
     * @param playerName the name of the drawing player.
     * @param fromTopRow true if drawn from the top row, false otherwise.
     * @param fromBuildings true if a building card was drawn, false if a character card.
     * @param index the position of the card on the track.
     * @throws RemoteException if RMI communication fails.
     */
    void drawnCard(String playerName, boolean fromTopRow, boolean fromBuildings, int index) throws RemoteException;

    void turnPassed(String playerName) throws RemoteException;

    /**
     * Notifies that a player has chosen a specific tile on the offer track.
     * @param playerName the name of the player.
     * @param index the index of the chosen tile.
     * @throws RemoteException if RMI communication fails.
     */
    void chosenTile(String playerName, int index) throws RemoteException;

    /**
     * Updates the food reserve of a specific player.
     * @param playerName the player's name.
     * @param food the new food amount.
     * @throws RemoteException if RMI communication fails.
     */
    void updateFood(String playerName, int food) throws RemoteException;

    /**
     * Updates the Shaman Stars for a specific player.
     * @param playerName the player's name.
     * @param stars the new number of stars.
     * @throws RemoteException if RMI communication fails.
     */
    void updateShamansStars(String playerName, int stars) throws RemoteException;

    /**
     * Updates the Prestige Points for a specific player.
     * @param playerName the player's name.
     * @param pp the new prestige points.
     * @throws RemoteException if RMI communication fails.
     */
    void updatePp(String playerName, int pp) throws RemoteException;

    void updateBuildersDiscount(String playerName, int discount) throws RemoteException;

    void updateGatherersDiscount(String playerName, int discount) throws RemoteException;

    /**
     * Updates the cards in the top row of the offer track.
     * @param newTopRow the new list of character cards.
     * @throws RemoteException if RMI communication fails.
     */
    void updateTopRow(ArrayList<Card> newTopRow) throws RemoteException;

    /**
     * Updates the building cards in the top row.
     * @param newTopBuildings the new list of building cards.
     * @throws RemoteException if RMI communication fails.
     */
    void updateTopBuildings(ArrayList<BuildingCard> newTopBuildings) throws RemoteException;

    /**
     * Updates the cards in the bottom row.
     * @param newBottomRow the new list of character cards.
     * @throws RemoteException if RMI communication fails.
     */
    void updateBottomRow(ArrayList<Card> newBottomRow) throws RemoteException;

    /**
     * Updates the building cards in the bottom row.
     * @param newBottomBuildings the new list of building cards.
     * @throws RemoteException if RMI communication fails.
     */
    void updateBottomBuildings(ArrayList<BuildingCard> newBottomBuildings) throws RemoteException;

//    /**
//     * Notifies the name of the player whose turn is next.
//     * @param playerName the name of the next player.
//     * @throws RemoteException if RMI communication fails.
//     */
//    void updateNextPlayer(String playerName) throws RemoteException;

    /**
     * Notifies a change in the game phase.
     * @param phase the new GamePhase.
     * @throws RemoteException if RMI communication fails.
     */
    void updateGamePhase(GamePhase phase) throws RemoteException;

    /**
     * Notifies a change in the game era.
     * @param era the new era number.
     * @throws RemoteException if RMI communication fails.
     */
    void updateEra(int era) throws RemoteException;

    /**
     * Notifies all players that the game is ended and sends them the final ranking.
     * @param finalRanking the ranking based on final points.
     */
    void updateEndGame(Map<String, Integer> finalRanking) throws RemoteException;

    /**
     * Notifies the player about their position in the general leaderboard for games with the same number of players
     * as this one.
     * @param leaderboard a list of Strings representing the leaderboard with the final scores of all the game played
     *                    with the same number of players.
     * @param playerPosition the player's position in the leaderboard.
     * @throws RemoteException if RMI communication fails.
     */
    void updateLeaderboardInfo(List<String> leaderboard, int playerPosition) throws RemoteException;

    /**
     * Forces the client to terminate the session, usually due to a server-side disconnection.
     * @throws RemoteException if RMI communication fails.
     */
    void forceQuit(String disconnectedPlayer) throws RemoteException;
}
