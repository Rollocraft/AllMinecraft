package de.rollocraft.allminecraft.Minecraft.Database;

import java.sql.*;


public class BackpackDatabaseManager {
    private Connection connection;


    public void connectToDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:./plugins/AllMinecraft/Database/Database.db");
    }
    public boolean isConnected() {
        return connection != null;
    }

    public void createBackpackTableIfNotExists() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE TABLE IF NOT EXISTS backpack (id INTEGER, itemstring TEXT);");
        }
    }

    public void saveBackpack(String backpack) throws SQLException {
        try (PreparedStatement pstmt = connection.prepareStatement("INSERT OR REPLACE INTO backpack (id, itemstring) VALUES (?, ?)")) {
            pstmt.setInt(1, 1); // Wir verwenden immer die ID 1 für den Timer
            pstmt.setString(2, backpack); // Angenommen, getTimeAsString() ist eine Methode, die die Zeit als String zurückgibt
            pstmt.executeUpdate();
        }
    }

    public String loadBackpack() throws SQLException {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT itemstring FROM backpack WHERE id = 1")) {
            if (rs.next()) {
                return rs.getString("itemstring");
            } else {
                return null;
            }
        }
    }
    public void disconnectFromDatabase() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}