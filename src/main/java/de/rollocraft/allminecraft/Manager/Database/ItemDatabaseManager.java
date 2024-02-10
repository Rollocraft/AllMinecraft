package de.rollocraft.allminecraft.Manager.Database;

import org.bukkit.Material;
import java.sql.*;

import static de.rollocraft.allminecraft.Manager.ItemAvialabel.isObtainableInSurvival;

public class ItemDatabaseManager {

    protected Connection connection;

    public void connectToDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:./plugins/Challenges/Database/Database.db");
    }

    public boolean isConnected() {
        return connection != null;
    }

    public void createTableIfNotExists() throws SQLException {
        if (connection == null) {
            throw new SQLException("Not connected to the database.");
        }

        String sql = "CREATE TABLE IF NOT EXISTS items (item_name TEXT, done INTEGER)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
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
}