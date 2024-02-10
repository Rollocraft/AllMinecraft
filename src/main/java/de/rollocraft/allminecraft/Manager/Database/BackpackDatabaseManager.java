package de.rollocraft.allminecraft.Manager.Database;

import org.bukkit.inventory.ItemStack;
import java.sql.*;
import java.io.*;

public class BackpackDatabaseManager {
    protected Connection connection;

    public BackpackDatabaseManager() {
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:./plugins/Challenges/Database/Database.db");
            PreparedStatement statement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS backpack (slot INTEGER PRIMARY KEY, item BLOB)"
            );
            statement.execute();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public ItemStack[] loadBackpack(int size) {
        ItemStack[] backpack = new ItemStack[size];
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT slot, item FROM backpack"
            );
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int slot = resultSet.getInt("slot");
                byte[] itemBytes = resultSet.getBytes("item");
                ByteArrayInputStream bais = new ByteArrayInputStream(itemBytes);
                ObjectInputStream ois = new ObjectInputStream(bais);
                ItemStack item = (ItemStack) ois.readObject();
                backpack[slot] = item;
            }

            resultSet.close();
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return backpack;
    }

    public void saveBackpack(ItemStack[] backpack) {
        try {
            PreparedStatement deleteStatement = connection.prepareStatement(
                    "DELETE FROM backpack"
            );
            deleteStatement.execute();
            deleteStatement.close();

            PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO backpack (slot, item) VALUES (?, ?)"
            );

            for (int i = 0; i < backpack.length; i++) {
                if (backpack[i] != null) {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);
                    oos.writeObject(backpack[i]);
                    byte[] itemBytes = baos.toByteArray();

                    insertStatement.setInt(1, i);
                    insertStatement.setBytes(2, itemBytes);
                    insertStatement.execute();
                }
            }

            insertStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void connectToDatabase() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection("jdbc:sqlite:./plugins/Challenges/Database/Database.db");
            PreparedStatement statement = connection.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS backpack (slot INTEGER PRIMARY KEY, item BLOB)"
            );
            statement.execute();
            statement.close();
        }
    }

    public void disconnectFromDatabase() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }
}