package de.rollocraft.allminecraft.Minecraft.Manager;

import de.rollocraft.allminecraft.Minecraft.Database.BackpackDatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.SQLException;

public class BackpackManager {

    private final BackpackDatabaseManager backpackDatabaseManager;

    public BackpackManager(BackpackDatabaseManager backpackDatabaseManager) {
        this.backpackDatabaseManager = backpackDatabaseManager;
    }

    public void saveBackpack(Inventory inventory) throws SQLException {
        String data = itemStackArrayToBase64(inventory.getContents());
        backpackDatabaseManager.saveBackpack(data);
    }

    public Inventory loadBackpack() {
        try {
            String data = backpackDatabaseManager.loadBackpack();
            if (data == null) {
                return Bukkit.createInventory(null, 27);
            } else {
                ItemStack[] items = itemStackArrayFromBase64(data);
                Inventory inventory = Bukkit.createInventory(null, items.length);
                inventory.setContents(items);
                return inventory;
            }
        } catch (SQLException | IOException e) {
            Bukkit.getLogger().severe("Failed to load backpack: " + e.getMessage());
        }
        return null;
    }

    private ItemStack[] itemStackArrayFromBase64(String data) throws IOException {
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(Base64Coder.decodeLines(data));
            BukkitObjectInputStream dataInput = new BukkitObjectInputStream(inputStream);
            ItemStack[] items = new ItemStack[dataInput.readInt()];

            // Read the serialized inventory
            for (int i = 0; i < items.length; i++) {
                items[i] = (ItemStack) dataInput.readObject();
            }

            dataInput.close();
            return items;
        } catch (ClassNotFoundException e) {
            throw new IOException("Unable to decode class type.", e);
        }
    }

    private String itemStackArrayToBase64(ItemStack[] items) throws IllegalStateException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            BukkitObjectOutputStream dataOutput = new BukkitObjectOutputStream(outputStream);

            // Write the size of the inventory
            dataOutput.writeInt(items.length);

            // Save every element in the list
            for (ItemStack item : items) {
                dataOutput.writeObject(item);
            }

            // Serialize that array
            dataOutput.close();
            return Base64Coder.encodeLines(outputStream.toByteArray());
        } catch (Exception e) {
            throw new IllegalStateException("Unable to save item stacks.", e);
        }
    }

}