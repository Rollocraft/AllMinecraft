package de.rollocraft.allminecraft.Listener;

import de.rollocraft.allminecraft.Main;
import de.rollocraft.allminecraft.Manager.BossBarManager;
import de.rollocraft.allminecraft.Manager.Database.ItemDatabaseManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;

public class InventoryInteractListener implements Listener {
    private final Main plugin;
    private final ItemDatabaseManager databaseManager;
    private final BossBarManager bossBarManager;

    public InventoryInteractListener(Main plugin, ItemDatabaseManager databaseManager, BossBarManager bossBarManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.bossBarManager = bossBarManager;
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player player = (Player) event.getWhoClicked();
            ItemStack itemStack = event.getCurrentItem();
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
