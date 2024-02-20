package de.rollocraft.allminecraft.Minecraft.Listener;

import de.rollocraft.allminecraft.Main;
import de.rollocraft.allminecraft.Minecraft.Database.AchievementDatabaseManager;
import de.rollocraft.allminecraft.Minecraft.Manager.TabListManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;

import java.sql.SQLException;

public class PlayerAdvancementDoneListener implements Listener {

    private final Main plugin;
    private final TabListManager tabListManager;
    private final AchievementDatabaseManager achievementDatabaseManager;

    public PlayerAdvancementDoneListener(Main plugin, AchievementDatabaseManager achievementDatabaseManager, TabListManager tablistManager) {
        this.plugin = plugin;
        this.achievementDatabaseManager = achievementDatabaseManager;
        this.tabListManager = tablistManager;
    }

    @EventHandler
    public void onPlayerAdvancementDone(PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        String advancementName = event.getAdvancement().getKey().getKey();
        try {
            if (!achievementDatabaseManager.isAchievementDone(advancementName)) {
                achievementDatabaseManager.markAchievementAsDone(advancementName);
                plugin.getServer().broadcastMessage("Das Achievement " + advancementName + "wurde von " + player.getName() + "erledigt");
                tabListManager.updateTabList();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to mark achievement as done: " + e.getMessage());
        }
    }
}
