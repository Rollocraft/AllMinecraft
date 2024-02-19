package de.rollocraft.allminecraft.Minecraft.Manager;

import de.rollocraft.allminecraft.Main;
import de.rollocraft.allminecraft.Minecraft.Database.ItemDatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class TabListManager {
    private final Main plugin;
    private final BossBarManager bossBarManager;
    private final ItemDatabaseManager databaseManager;

    public TabListManager(Main plugin, ItemDatabaseManager databaseManager, BossBarManager bossBarManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.bossBarManager = bossBarManager;
    }

    public void updateTabList() {
        Bukkit.getLogger().info("Updating tab list");
        try {
            String currentItem = databaseManager.getCurrentItem();
            Bukkit.getLogger().info("Current item: " + currentItem);
            if (currentItem != null) {
                String formattedItem = currentItem.replace("_", " ").toLowerCase();
                Bukkit.getLogger().info("Updating tab list with current item: " + currentItem);
                formattedItem = formattedItem.substring(0, 1).toUpperCase() + formattedItem.substring(1);
                int doneItems = databaseManager.countDoneItems();
                int totalItems = databaseManager.countTotalItems();
                String title = String.format("%s (%d/%d)", formattedItem, doneItems, totalItems);

                // Set the tab list footer for all online players
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.setPlayerListFooter(title);
                }
            }
        } catch(SQLException e){
            plugin.getLogger().severe("Failed to get current item from database: " + e.getMessage());
        }
    }
}