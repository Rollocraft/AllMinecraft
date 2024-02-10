package de.rollocraft.allminecraft.Manager.Database;

import de.rollocraft.allminecraft.Manager.Position;

import java.sql.*;

public class PositionDatabaseManager {

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

        String sql = "CREATE TABLE IF NOT EXISTS positions (name TEXT, x REAL, y REAL, z REAL)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        }
    }

    public void savePositionToDatabase(String name, double x, double y, double z) throws SQLException {
        if (connection == null) {
            throw new SQLException("Not connected to the database.");
        }

        String sql = "INSERT INTO positions (name, x, y, z) VALUES (?, ?, ?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.setDouble(2, x);
            statement.setDouble(3, y);
            statement.setDouble(4, z);
            statement.executeUpdate();
        }
    }

    public void deletePositionFromDatabase(String name) throws SQLException {
        if (connection == null) {
            throw new SQLException("Not connected to the database.");
        }

        String sql = "DELETE FROM positions WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            statement.executeUpdate();
        }
    }

    public Position getPositionFromDatabase(String name) throws SQLException {
        if (connection == null) {
            throw new SQLException("Not connected to the database.");
        }

        String sql = "SELECT x, y, z FROM positions WHERE name = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, name);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                double x = resultSet.getDouble("x");
                double y = resultSet.getDouble("y");
                double z = resultSet.getDouble("z");
                return new Position(formatDouble(x), formatDouble(y), formatDouble(z));
            } else {
                return null;
            }
        }
    }

    public int formatDouble(double value) {
        return (int) Math.round(value);
    }


    public void disconnectFromDatabase() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public Connection getConnection() {
        return connection;
    }
}