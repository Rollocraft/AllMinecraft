package de.rollocraft.allminecraft.Manager.Database;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class TimerDatabaseManager {
    private Connection connection;

    public TimerDatabaseManager() {
        connectToDatabase();
    }

    private void connectToDatabase() {
        try {
            File dbFile = new File("./plugins/Challenges/Database/timer.db");
            if (!dbFile.exists()) {
                dbFile.createNewFile();
            }
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbFile);
            createTableIfNotExists();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    public void createTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS timer (id INT PRIMARY KEY, time BIGINT)";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    public void saveTime(long time) throws SQLException {
        String sql = "INSERT OR REPLACE INTO timer (id, time) VALUES (1, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setLong(1, time);
            pstmt.executeUpdate();
        }
    }

    public long loadTime() throws SQLException {
        String sql = "SELECT time FROM timer WHERE id = 1";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getLong("time");
            } else {
                return 0;
            }
        }
    }
}