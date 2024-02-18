package de.rollocraft.allminecraft.Minecraft.Manager.Database;

import de.rollocraft.allminecraft.Minecraft.Manager.Backpack;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BackpackDatabaseManager {

    private Connection connection;

    public BackpackDatabaseManager() throws SQLException {
        connect();
        createTable();
    }

    public void connect() throws SQLException {
        connection = DriverManager.getConnection("jdbc:sqlite:./plugins/AllMinecraft/Database/Database.db");
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