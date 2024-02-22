package de.rollocraft.allminecraft.Minecraft.Database;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.sql.*;

public class MobDatabaseManager {

    protected Connection connection;

    public void connectToDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:./plugins/AllMinecraft/Database/Database.db");
    }

    public boolean isConnected() {
        return connection != null;
    }

    public void createTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS mobs (" +
                "mob_key TEXT NOT NULL, " +
                "found INTEGER NOT NULL DEFAULT 0" +
                ");";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.execute();
        }
    }

    public void saveAllMobsToDatabase() throws SQLException {

        if (connection == null) {
            throw new SQLException("Not connected to the database.");
        }
        if (!isTableEmpty()) {
            return;
        }
        String sql = "INSERT INTO mobs (mob_key, found) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (EntityType entityType : EntityType.values()) {
                if (entityType.isAlive()) {
                    statement.setString(1, entityType.name());
                    statement.setInt(2, 0); // Set 'found' to 0 (false) initially
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

    public boolean isMobFound(String mob) throws SQLException {
        String sql = "SELECT found FROM mobs WHERE mob_key = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, mob);
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() && resultSet.getInt("found") == 1;
        }
    }

    public void markMobAsFound(String mob) throws SQLException {
        String sql = "UPDATE mobs SET found = 1 WHERE mob_key = ?";
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ITEM_TRIDENT_RETURN, 1.0F, 1.0F);
        }
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, mob);
            statement.executeUpdate();
        }
    }

    public int countFoundMobs() throws SQLException {
        String sql = "SELECT COUNT(*) FROM mobs WHERE found = 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? resultSet.getInt(1) : 0;
        }
    }

    public int countTotalMobs() throws SQLException {
        String sql = "SELECT COUNT(*) FROM mobs";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? resultSet.getInt(1) : 0;
        }
    }

    public boolean isTableEmpty() throws SQLException {
        String sql = "SELECT COUNT(*) FROM mobs";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            return !resultSet.next() || resultSet.getInt(1) == 0;
        }
    }

    public String getDisplayName(String mobName) {
        String[] words = mobName.replace("_", " ").split(" ");
        for (int i = 0; i < words.length; i++) {
            words[i] = words[i].substring(0, 1).toUpperCase() + words[i].substring(1).toLowerCase();
        }
        return String.join(" ", words);
    }
}