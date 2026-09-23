package classes;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LeaderBoardManager {
    private static final String DB_URL = "jdbc:postgresql://dblabs.iee.ihu.gr:5432/alexkara5";
    private static final String DB_USER_ENV = "DB_USER";
    private static final String DB_PASSWORD_ENV = "DB_PASSWORD";

    static {
        // Initialize table in Postgres
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            String sqlAlg = "CREATE TABLE IF NOT EXISTS alg_leaderboard (" +
                    "name VARCHAR(50) PRIMARY KEY, " +
                    "score INTEGER NOT NULL)";
            stmt.execute(sqlAlg);

            String sqlBin = "CREATE TABLE IF NOT EXISTS bin_leaderboard (" +
                    "name VARCHAR(50) PRIMARY KEY, " +
                    "score INTEGER NOT NULL)";
            stmt.execute(sqlBin);
        } catch (SQLException e) {
            System.err.println("Database init error: " + e.getMessage());
        }
    }

    private static Connection getConnection() throws SQLException {
        String user = System.getenv(DB_USER_ENV);
        String password = System.getenv(DB_PASSWORD_ENV);

        if (user == null || user.isBlank() || password == null || password.isBlank()) {
            throw new SQLException("Missing required database environment variables: " + DB_USER_ENV + " and/or " + DB_PASSWORD_ENV);
        }

        return DriverManager.getConnection(DB_URL, user, password);
    }

    private static String getTableName(String mode) {
        return "BINARY".equalsIgnoreCase(mode) ? "bin_leaderboard" : "alg_leaderboard";
    }

    public static void saveScore(String name, int newScore) {
        saveScore(name, newScore, MainMenuController.gameMode);
    }

    public static void saveScore(String name, int newScore, String mode) {
        String tableName = getTableName(mode);
        // PostgreSQL "UPSERT" syntax (Insert or Update on Conflict)
        // Only updates the score if the new score (EXCLUDED.score) is higher than the existing one
        String upsertSQL = "INSERT INTO " + tableName + " (name, score) VALUES (?, ?) " +
                "ON CONFLICT (name) DO UPDATE " +
                "SET score = EXCLUDED.score " +
                "WHERE EXCLUDED.score > " + tableName + ".score";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(upsertSQL)) {

            pstmt.setString(1, name);
            pstmt.setInt(2, newScore);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Could not save score to Postgres: " + e.getMessage());
        }
    }

    public static List<String> getSortedScores() {
        return getSortedScores(MainMenuController.gameMode);
    }

    public static List<String> getSortedScores(String mode) {
        String tableName = getTableName(mode);
        List<String> scores = new ArrayList<>();
        String query = "SELECT name, score FROM " + tableName + " ORDER BY score DESC";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                String name = rs.getString("name");
                int score = rs.getInt("score");
                scores.add(name + ": " + score);
            }
        } catch (SQLException e) {
            System.err.println("Could not retrieve scores from Postgres: " + e.getMessage());
        }
        return scores;
    }
}