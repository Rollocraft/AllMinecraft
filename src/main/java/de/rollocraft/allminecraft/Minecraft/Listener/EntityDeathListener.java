package de.rollocraft.allminecraft.Minecraft.Listener;

import de.rollocraft.allminecraft.Minecraft.Database.MobDatabaseManager;
import de.rollocraft.allminecraft.Minecraft.Manager.TabListManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.sql.SQLException;

public class EntityDeathListener implements Listener {
    private final MobDatabaseManager mobDatabaseManager;
    private final TabListManager tabListManager;
    public EntityDeathListener(MobDatabaseManager mobDatabaseManager, TabListManager tabListManager) {
        this.mobDatabaseManager = mobDatabaseManager;
        this.tabListManager = tabListManager;

    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        Entity mob = event.getEntity();
        if (killer != null) {
            String mobKey = mob.getType().name();
            try {
                if(!mobDatabaseManager.isMobFound(mobKey)){
                    mobDatabaseManager.markMobAsFound(mobKey);
                    tabListManager.updateTabList();
                    Bukkit.getServer().broadcastMessage(ChatColor.AQUA + "[AllMobs] " + ChatColor.WHITE + "Das Mob: " + mob.getName() + ", wurde von " + killer.getName() + " gefunden!");
                }
            } catch (SQLException e) {
                Bukkit.getLogger().info("Failed to mark mob as found: " + e.getMessage());
            }
        }
    }
}
