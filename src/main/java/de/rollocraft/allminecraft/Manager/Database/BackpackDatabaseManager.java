package de.rollocraft.allminecraft.Manager.Database;

import de.rollocraft.allminecraft.Manager.Backpack;
import de.rollocraft.allminecraft.utils.Base64;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BackpackDatabaseManager {

    private Connection connection;

    public BackpackDatabaseManager(String filename) {
        connect(filename);
        createTable();
    }

    private void connect(String filename) {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:" + filename);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTable() {
        try (PreparedStatement statement = connection.prepareStatement(
                "CREATE TABLE IF NOT EXISTS backpacks (items TEXT)")) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void saveBackpack(Backpack backpack) {
        try (PreparedStatement statement = connection.prepareStatement(
                "REPLACE INTO backpacks (items) VALUES (?)")) {
            statement.setString(1, backpack.toBase64());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Backpack loadBackpack() {
        try (PreparedStatement statement = connection.prepareStatement(
                "SELECT items FROM backpacks")) {
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String items = resultSet.getString("items");
                return new Backpack(items);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}