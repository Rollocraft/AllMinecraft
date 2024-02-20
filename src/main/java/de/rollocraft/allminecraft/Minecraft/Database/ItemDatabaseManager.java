package de.rollocraft.allminecraft.Minecraft.Database;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.sql.*;

import static de.rollocraft.allminecraft.Minecraft.utils.ItemAvialabel.isObtainableInSurvival;

public class ItemDatabaseManager {

    protected Connection connection;

    public void connectToDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:./plugins/AllMinecraft/Database/Database.db");
    }

    public boolean isConnected() {
        return connection != null;
    }

    public void createTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS items (" +
                "item_name TEXT NOT NULL, " +
                "done INTEGER NOT NULL, " +
                "current_item INTEGER NOT NULL DEFAULT 0" +
                ");";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.execute();
        }
    }

    public void setCurrentItem(String itemName) throws SQLException {
        String resetSql = "UPDATE items SET current_item = 0";
        try (PreparedStatement resetStmt = connection.prepareStatement(resetSql)) {
            resetStmt.execute();
        }

        String setSql = "UPDATE items SET current_item = 1 WHERE item_name = ?";
        try (PreparedStatement setStmt = connection.prepareStatement(setSql)) {
            setStmt.setString(1, itemName);
            setStmt.execute();
        }
    }

    public void saveAllItemsToDatabase() throws SQLException {
        if (connection == null) {
            throw new SQLException("Not connected to the database.");
        }
        if (!isTableEmpty()) {
            return;
        }
        String sql = "INSERT INTO items (item_name, done) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (Material material : Material.values()) {
                if (isObtainableInSurvival(material)) {
                    String itemName = material.name();
                    statement.setString(1, itemName);
                    statement.setInt(2, 0); // Set 'done' to 0 (false) initially
                    statement.executeUpdate();
                }
            }
        }
    }

    public void disconnectFromDatabase() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public String getRandomItem() throws SQLException {
        if (connection == null) {
            throw new SQLException("Not connected to the database.");
        }

        String sql = "SELECT item_name FROM items ORDER BY RANDOM() LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("item_name");
            } else {
                return null;
            }
        }
    }

    public boolean isItemDone(String item) throws SQLException {
        if (connection == null) {
            throw new SQLException("Not connected to the database.");
        }

        String sql = "SELECT done FROM items WHERE item_name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, item);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("done") == 1;
            } else {
                return false;
            }
        }
    }

    public void markItemAsDone(String item) throws SQLException {
        if (connection == null) {
            throw new SQLException("Not connected to the database.");
        }

        String sql = "UPDATE items SET done = 1 WHERE item_name = ?";
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
        }
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, item);
            statement.executeUpdate();
        }
    }

    public int countDoneItems() throws SQLException {
        if (connection == null) {
            throw new SQLException("Not connected to the database.");
        }

        String sql = "SELECT COUNT(*) FROM items WHERE done = 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? resultSet.getInt(1) : 0;
        }
    }

    public int countTotalItems() throws SQLException {
        if (connection == null) {
            throw new SQLException("Not connected to the database.");
        }

        String sql = "SELECT COUNT(*) FROM items";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? resultSet.getInt(1) : 0;
        }
    }

    public boolean isTableEmpty() throws SQLException {
        if (connection == null) {
            throw new SQLException("Not connected to the database.");
        }

        String sql = "SELECT COUNT(*) FROM items";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            return !resultSet.next() || resultSet.getInt(1) == 0;
        }
    }

    public Connection getConnection() {
        return connection;
    }

    public String getCurrentItem() throws SQLException {
        String sql = "SELECT item_name FROM items WHERE current_item = 1";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("item_name");
            } else {
                return null;
            }
        }
    }
}