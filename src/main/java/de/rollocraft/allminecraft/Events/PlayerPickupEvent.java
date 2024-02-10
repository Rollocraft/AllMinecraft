package de.rollocraft.allminecraft.Events;

import de.rollocraft.allminecraft.Main;
import de.rollocraft.allminecraft.Manager.BossBarManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.inventory.ItemStack;

import de.rollocraft.allminecraft.Manager.Database.ItemDatabaseManager;

import java.sql.SQLException;

public class PlayerPickupEvent implements Listener {
    private final Main plugin;
    private final ItemDatabaseManager databaseManager;
    private final BossBarManager bossBarManager;

    public PlayerPickupEvent(Main plugin, ItemDatabaseManager databaseManager, BossBarManager bossBarManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.bossBarManager = bossBarManager;
    }

    @EventHandler
    public void onPlayerPickupItem(EntityPickupItemEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            ItemStack itemStack = event.getItem().getItemStack();
            if (itemStack != null) {
                String itemName = itemStack.getType().name();
                String currentItem = bossBarManager.getCurrentItem();
                if (itemName.equals(currentItem)) {
                    try {
                        databaseManager.markItemAsDone(itemName);
                        bossBarManager.updateBossBar(); // Update the boss bar
                    } catch (SQLException e) {
                        plugin.getLogger().severe("Failed to mark item as done: " + e.getMessage());
                    }
                }
            }
        }
    }
}