package de.rollocraft.allminecraft.Manager;

import de.rollocraft.allminecraft.Main;
import de.rollocraft.allminecraft.Manager.Database.ItemDatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.sql.SQLException;

public class BossBarManager {
    private final Main plugin;
    private final ItemDatabaseManager databaseManager;
    private BossBar bossBar;
    private String currentItem;

    public BossBarManager(Main plugin, ItemDatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        createBossBar();
    }

    public void createBossBar() {
        if (bossBar != null) {
            bossBar.removeAll();
        }

        bossBar = Bukkit.createBossBar("", BarColor.PURPLE, BarStyle.SOLID);
        bossBar.setVisible(true);

        updateBossBar();
    }

    public void updateBossBar() {
        try {
            currentItem = databaseManager.getRandomItem();
            if (currentItem != null && !databaseManager.isItemDone(currentItem)) {
                String formattedItem = currentItem.replace("_", " ").toLowerCase();
                formattedItem = formattedItem.substring(0, 1).toUpperCase() + formattedItem.substring(1);
                int doneItems = databaseManager.countDoneItems();
                int totalItems = databaseManager.countTotalItems();
                bossBar.setTitle(String.format("%s (%d/%d)", formattedItem, doneItems, totalItems));
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get random item from database: " + e.getMessage());
        }
    }

    public void removeBossBar() {
        if (bossBar != null) {
            bossBar.removeAll();
        }
    }

    public BossBar getBossBar() {
        return bossBar;
    }

    public String getCurrentItem() {
        return currentItem;
    }
}