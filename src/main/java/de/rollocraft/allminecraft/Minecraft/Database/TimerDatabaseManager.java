package de.rollocraft.allminecraft.Minecraft.Database;
// TimerDatabaseManager.java

import de.rollocraft.allminecraft.Minecraft.Timer;

import java.sql.*;

public class TimerDatabaseManager {
    private Connection connection;

    public TimerDatabaseManager(Connection connection) {
        this.connection = connection;
    }

    public void createTimerTableIfNotExists() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS timer (id INTEGER PRIMARY KEY, time INTEGER NOT NULL)");
        }
    }

    public void saveTimer(Timer timer) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement("INSERT OR REPLACE INTO timer (id, time) VALUES (?, ?)")) {
            pstmt.setInt(1, 1); // Wir verwenden immer die ID 1 für den Timer
            pstmt.setInt(2, timer.getTime());
            pstmt.executeUpdate();
        }
    }

    public int loadTimer() throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT time FROM timer WHERE id = 1")) {
            if (rs.next()) {
                return rs.getInt("time");
            } else {
                return 0; // Standardwert, wenn kein Timer gespeichert wurde
            }
        }
    }
}