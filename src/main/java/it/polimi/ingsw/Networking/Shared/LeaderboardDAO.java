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
    private final Connection connection;

    public LeaderboardDAO(Connection connection) {
        this.connection = connection;
    }

    public void saveMatchResult(String player, int finalScore, int playersNum) throws SQLException {
        String query = "INSERT INTO match_history (nickname, final_score, players_number) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, player);
            pstmt.setInt(2, finalScore);
            pstmt.setInt(3, playersNum);
            pstmt.executeUpdate();
        }
    }

    public List<String> getLeaderboard(int playersNum) throws SQLException {
        List<String> leaderboard = new ArrayList<>();
        String query = "SELECT nickname, final_score, match_date FROM match_history WHERE players_number = ? ORDER BY final_score DESC";

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

    public int getPlayerPosition(int playersNum, int finalScore) throws SQLException {
        String query = "SELECT COUNT(*) + 1 AS player_rank FROM match_history WHERE players_number = ? AND final_score > ?";
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
