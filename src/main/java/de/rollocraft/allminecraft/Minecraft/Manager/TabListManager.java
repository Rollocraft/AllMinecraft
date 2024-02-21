package de.rollocraft.allminecraft.Minecraft.Manager;

import de.rollocraft.allminecraft.Main;
import de.rollocraft.allminecraft.Minecraft.Database.AchievementDatabaseManager;
import de.rollocraft.allminecraft.Minecraft.Database.ItemDatabaseManager;
import de.rollocraft.allminecraft.Minecraft.Database.MobDatabaseManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class TabListManager {
    private final Main plugin;

    private final ItemDatabaseManager itemDatabaseManager;
    private final AchievementDatabaseManager achievementDatabaseManager;
    private final MobDatabaseManager mobDatabaseManager;

    public TabListManager(Main plugin, ItemDatabaseManager itemDatabaseManager, AchievementDatabaseManager achievementDatabaseManager, MobDatabaseManager mobDatabaseManager) {
        this.plugin = plugin;
        this.itemDatabaseManager = itemDatabaseManager;
        this.achievementDatabaseManager = achievementDatabaseManager;
        this.mobDatabaseManager = mobDatabaseManager;
    }


    public void updateTabList() {
        try {
            String currentItem = itemDatabaseManager.getCurrentItem();
            if (currentItem != null) {
                String formattedItem = currentItem.replace("_", " ").toLowerCase();
                formattedItem = formattedItem.substring(0, 1).toUpperCase() + formattedItem.substring(1);
                int doneItems = itemDatabaseManager.countDoneItems();
                int totalItems = itemDatabaseManager.countTotalItems();
                String itemTitle = String.format("%s (%d/%d)", formattedItem, doneItems, totalItems);

                int doneAchievements = achievementDatabaseManager.countDoneAchievements();
                int totalAchievements = achievementDatabaseManager.countTotalAchievements();
                String achievementTitle = String.format("All Achievements (%d/%d)", doneAchievements, totalAchievements);

                int foundMobs = mobDatabaseManager.countFoundMobs();
                int totalMobs = mobDatabaseManager.countTotalMobs();
                String mobTitle = String.format("All Mobs (%d/%d)", foundMobs, totalMobs);

                String title = " " + "\n" + itemTitle + "\n" + achievementTitle + "\n" + mobTitle;

                // Set the tab list footer for all online players
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.setPlayerListFooter(title);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get data from database: " + e.getMessage());
        }
    }
}