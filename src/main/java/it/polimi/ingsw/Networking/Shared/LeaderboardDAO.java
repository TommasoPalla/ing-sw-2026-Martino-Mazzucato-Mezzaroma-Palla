package it.polimi.ingsw.Networking.Shared;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * This class makes use of the Data Access Object structural pattern: it serves as a communication bridge from
 * the {@link ServerController} to the MySql database and viceversa. It forwards the SQL queries to the db to perform
 * the following actions: 1 - Save the Match result of the finished game (for every game it creates a new row for every
 * player who have played that game, saving their final score, the number of players who have played and the date when the
 * game has been played. For every row there's an id that is incremented at every row addition, and it serves as the primary
 * key of the table). 2 - Extract the leaderboard only of games with a specified amount of players. 3 - Get a player's
 * position in the leaderboard for games played with a certain amount of players.
 * The database can only be accessed by the local host of the database, since it's stored locally and access to it is
 * limited to the 'root' user.
 */
public class LeaderboardDAO {

    /**
     * The connection to the Mesos database. This connection is assigned after the {@link DriverManager} connects to
     * the db using the USER (here 'root'), the database URL and the PASSWORD.
     */
    private final Connection connection;

    public LeaderboardDAO(Connection connection) {
        this.connection = connection;
    }

    /**
     * This method adds a player to the database when the game is over. It stores their nickname, their final score
     * the number of players of that game, and the data at which that game has been played.
     * @param player the player whose to be added.
     * @param finalScore the final score the player has obtained at the end of the game.
     * @param playersNum the number of players of that game.
     * @throws SQLException if it was not possible to execute the SQL query to the database.
     */
    public void saveMatchResult(String player, int finalScore, int playersNum) throws SQLException {
        String query = "INSERT INTO match_history (nickname, final_score, players_number) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, player);
            pstmt.setInt(2, finalScore);
            pstmt.setInt(3, playersNum);
            pstmt.executeUpdate();
        }
    }

    /**
     * This method extracts the leaderboard of players considering the games with the specified number of players.
     * @param playersNum the number of players to select only the scores who match this number.
     * @return a list of Strings, each one containing a player nickname, their position in the leaderboard, their
     * final score, and the date in which that score has been achieved.
     * @throws SQLException if it was not possible to execute the SQL query to the database.
     */
    public List<String> getLeaderboard(int playersNum) throws SQLException {
        List<String> leaderboard = new ArrayList<>();
        // First, it creates a table 'RankedMatches' extracting from the table 'match_history' the records grouped
        // by nickname and ordered by descending final_score, only for the games with a number of players equal to
        // playersNum, and adding the row number for every nickname's row saved as 'rn'.
        // Then it extracts the rows from this table with rn = 1 (the records of players with their best final score)
        // and finally it orders them by descending final_score.
        String query = "WITH RankedMatches AS (" +
                "    SELECT nickname, final_score, match_date, " +
                "           ROW_NUMBER() OVER(PARTITION BY nickname ORDER BY final_score DESC) as rn " +
                "    FROM match_history " +
                "    WHERE players_number = ?" +
                ") " +
                "SELECT nickname, final_score, match_date " +
                "FROM RankedMatches " +
                "WHERE rn = 1 " +
                "ORDER BY final_score DESC";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, playersNum);
            try (ResultSet lbTable = pstmt.executeQuery()) {
                int position = 1;
                while (lbTable.next()) {
                    String row = position + ". " + lbTable.getString("nickname") + " - " + lbTable.getInt("final_score") + " pt" + " - " + lbTable.getDate("match_date");
                    leaderboard.add(row);
                    position++;
                }
            }
        }
        return leaderboard;
    }

    /**
     * This method extracts a player's position from the leaderboard of players considering the games with the
     * specified number of players.
     * @param playersNum the number of players to select only the scores who match this number.
     * @param finalScore the final score of the player.
     * @return the position of the player in the leaderboard. -1 if there has been an error.
     * @throws SQLException if it was not possible to execute the SQL query to the database.
     */
    public int getPlayerPosition(int playersNum, int finalScore) throws SQLException {
        // First it extracts the table 'best_scores' containing the record of players with their best final scores
        // obtained in games with a number of players equal to playersNum. Then, from this table it counts the number of
        // records with a final score higher than finalScore, and it adds 1 to get the player's position.
        String query = "SELECT COUNT(*) + 1 AS player_rank " +
                "FROM (" +
                "    SELECT MAX(final_score) AS best_score " +
                "    FROM match_history " +
                "    WHERE players_number = ? " +
                "    GROUP BY nickname" +
                ") AS best_scores " +
                "WHERE best_score > ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, playersNum);
            pstmt.setInt(2, finalScore);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("player_rank");
                }
            }
        }
        return -1;
    }
}
