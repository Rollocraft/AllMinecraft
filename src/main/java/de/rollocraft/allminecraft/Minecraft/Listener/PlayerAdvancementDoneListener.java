package de.rollocraft.allminecraft.Minecraft.Listener;

import de.rollocraft.allminecraft.Main;
import de.rollocraft.allminecraft.Minecraft.Database.AchievementDatabaseManager;
import de.rollocraft.allminecraft.Minecraft.Manager.TabListManager;
import org.bukkit.ChatColor;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementDisplay;
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
                Advancement advancement = event.getAdvancement();
                AdvancementDisplay display = advancement.getDisplay();
                String advancementDisplayName = (display != null) ? display.getTitle() : null;
                String namespace = advancement.getKey().getNamespace();
                String key = advancement.getKey().getKey();
                String fullKey = namespace + ":" + key;
                if (fullKey.contains("minecraft:adventure") || fullKey.contains("minecraft:end") || fullKey.contains("minecraft:nether") || fullKey.contains("minecraft:husbandry") || fullKey.contains("minecraft:story")) {
                    if (advancementDisplayName != null) {
                        plugin.getServer().broadcastMessage(ChatColor.AQUA + "[AllAchievements] " + ChatColor.WHITE + "Das Achievement: " + achievementDatabaseManager.getDisplayName(advancementDisplayName)  + ", wurde von " + player.getName() + " erledigt!");
                    }
                }
                tabListManager.updateTabList();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to mark achievement as done: " + e.getMessage());
        }
    }
}
