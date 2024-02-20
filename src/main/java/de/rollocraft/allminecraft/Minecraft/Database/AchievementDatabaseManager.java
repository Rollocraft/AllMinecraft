package de.rollocraft.allminecraft.Minecraft.Database;

import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Iterator;
import java.sql.*;

public class AchievementDatabaseManager {

    protected Connection connection;

    public void connectToDatabase() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:./plugins/AllMinecraft/Database/Database.db");
    }

    public boolean isConnected() {
        return connection != null;
    }

    public void createTableIfNotExists() throws SQLException {
        String sql = "CREATE TABLE IF NOT EXISTS achievements (" +
                "achievement_key TEXT NOT NULL, " +
                "done INTEGER NOT NULL DEFAULT 0" +
                ");";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.execute();
        }
    }

    public void saveAllAchievementsToDatabase() throws SQLException {

        if (connection == null) {
            throw new SQLException("Not connected to the database.");
        }
        if (!isTableEmpty()) {
            return;
        }
        String sql = "INSERT INTO achievements (achievement_key, done) VALUES (?, ?)";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            Iterator<Advancement> advancementIterator = Bukkit.getServer().advancementIterator();
            while (advancementIterator.hasNext()) {
                Advancement advancement = advancementIterator.next();
                String namespace = advancement.getKey().getNamespace();
                String key = advancement.getKey().getKey();
                String fullKey = namespace + ":" + key;
                if (fullKey.contains("minecraft:adventure") || fullKey.contains("minecraft:end") || fullKey.contains("minecraft:nether") || fullKey.contains("minecraft:husbandry") || fullKey.contains("minecraft:story")) {
                    statement.setString(1, key);
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

    public boolean isAchievementDone(String achievement) throws SQLException {
        String sql = "SELECT done FROM achievements WHERE achievement_key = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, achievement);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("done") == 1;
            } else {
                return false;
            }
        }
    }

    public void markAchievementAsDone(String achievement) throws SQLException {
        String sql = "UPDATE achievements SET done = 1 WHERE achievement_key = ?";
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0F, 1.0F);
        }
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, achievement);
            statement.executeUpdate();
        }
    }

    public int countDoneAchievements() throws SQLException {
        String sql = "SELECT COUNT(*) FROM achievements WHERE done = 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? resultSet.getInt(1) : 0;
        }
    }

    public int countTotalAchievements() throws SQLException {
        String sql = "SELECT COUNT(*) FROM achievements";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? resultSet.getInt(1) : 0;
        }
    }

    public boolean isTableEmpty() throws SQLException {
        String sql = "SELECT COUNT(*) FROM achievements";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            ResultSet resultSet = statement.executeQuery();
            return !resultSet.next() || resultSet.getInt(1) == 0;
        }
    }
}

